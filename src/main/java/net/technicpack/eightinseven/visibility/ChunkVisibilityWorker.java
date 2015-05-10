package net.technicpack.eightinseven.visibility;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.technicpack.eightinseven.mixin.VisibilityChunkMixin;

import java.util.ArrayList;
import java.util.List;

public class ChunkVisibilityWorker implements Runnable {
    private short[][] chunkFloodfill;
    private World world;
    private int worldX;
    private int worldY;
    private int worldZ;
    private VisibilityChunkMixin mixinChunk;
    private int subChunk;

    private VisibilityGraphNode workingGraphNode;
    private boolean floodedAny = false;
    private List<Boolean> floodedCache = new ArrayList<Boolean>(6);
    private List<ForgeDirection> floodedSides = new ArrayList<ForgeDirection>(6);

    public ChunkVisibilityWorker(Chunk chunk, VisibilityChunkMixin mixinChunk, int subChunk) {
        this.world = chunk.worldObj;
        this.worldY = subChunk*16;
        this.worldX = chunk.xPosition*16;
        this.worldZ = chunk.zPosition*16;
        this.mixinChunk = mixinChunk;
        this.subChunk = subChunk;

        clearFloodCache();
        workingGraphNode = new VisibilityGraphNode();
        workingGraphNode.clearConnections();

        this.chunkFloodfill = new short[16][];
        for (int i = 0; i < 16; i++) {
            this.chunkFloodfill[i] = new short[16];
            for (int j = 0; j < 16; j++) {
                this.chunkFloodfill[i][j] = 0;
            }
        }
    }

    @Override
    public void run() {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    //Only check the outer shell of the chunk
                    if (y != 0 && y != 15 && x != 0 && x != 15 && z != 0 && z != 15)
                        continue;

                    floodBlock(x,y,z);
                    if (floodedAny) {
                        pushFloodCache();
                        clearFloodCache();
                    }
                }
            }
        }

        mixinChunk.updateVisibility(subChunk, workingGraphNode.getVisibilityData());
    }

    protected void floodBlock(int x, int y, int z) {

        if (x < 0) {
            addFloodedSide(ForgeDirection.WEST);
            return;
        }

        if (x >= 16) {
            addFloodedSide(ForgeDirection.EAST);
            return;
        }

        if (y < 0) {
            addFloodedSide(ForgeDirection.DOWN);
            return;
        }

        if ( y >= 16) {
            addFloodedSide(ForgeDirection.UP);
            return;
        }

        if (z < 0) {
            addFloodedSide(ForgeDirection.NORTH);
            return;
        }

        if (z >= 16) {
            addFloodedSide(ForgeDirection.SOUTH);
            return;
        }

        //If we've already checked this block, skip
        if (isFloodFill(x, y, z))
            return;

        //Only check out blocks you might be able to see through
        if (world.isBlockNormalCubeDefault(worldX+x, worldY+y, worldZ+z, false))
            return;

        setFloodFill(x, y, z);

        floodBlock(x-1, y, z);
        floodBlock(x+1, y, z);
        floodBlock(x, y-1, z);
        floodBlock(x, y+1, z);
        floodBlock(x, y, z-1);
        floodBlock(x, y, z+1);
    }

    private void addFloodedSide(ForgeDirection side) {
        floodedAny = true;
        floodedCache.set(side.ordinal(), true);
    }

    private void clearFloodCache() {
        floodedAny = false;
        for (int i = 0; i < 6; i++) {
            floodedCache.set(i, false);
        }
    }

    private void pushFloodCache() {
        floodedSides.clear();
        for (int i = 0; i < 6; i++) {
            if (floodedCache.get(i))
                floodedSides.add(ForgeDirection.VALID_DIRECTIONS[i]);
        }

        if (floodedSides.size() < 2)
            return;

        for (int i = 0; i < floodedSides.size()-1; i++) {
            for (int j = 1; j < floodedSides.size(); j++) {
                workingGraphNode.addVisibility(floodedSides.get(i), floodedSides.get(j));
            }
        }
    }

    private void setFloodFill(int x, int y, int z) {
        int flag = 1 << z;
        chunkFloodfill[x][y] |= flag;
    }

    private boolean isFloodFill(int x, int y, int z) {
        int flag = 1 << z;
        return (chunkFloodfill[x][y] & flag) != 0;
    }
}

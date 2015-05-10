package net.technicpack.eightinseven.mixin;

import javafx.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.ForgeDirection;
import net.technicpack.eightinseven.EightInSeven;
import net.technicpack.eightinseven.visibility.VisibilityGraphNode;
import net.technicpack.eightinseven.visibility.VisibilityWalkStep;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;

@Mixin(Chunk.class)
public abstract class VisibilityChunkMixin {
    private VisibilityGraphNode[] graphNode;

    public void updateVisibility(int subChunk, short visibilityData) {
        synchronized (graphNode) {
            graphNode[subChunk].setVisibilityData(visibilityData);
        }
    }

    public void invalidateVisibility(int subChunk) {
        synchronized (graphNode[subChunk]) {
            graphNode[subChunk].invalidate();
        }
    }

    public void walkVisbility(int subChunk, Vec3 viewVector, ForgeDirection sourceSide, int stepsSoFar, Queue<VisibilityWalkStep> walkingQueue) {

        synchronized (graphNode[subChunk]) {
            //Visit chunk

            if (graphNode[subChunk].needsWork() && !graphNode[subChunk].isWorking()) {
                EightInSeven.instance.queueChunkRecalculate((Chunk) (Object) this, this, subChunk);
                graphNode[subChunk].markBeingWorked();
            }

            if (stepsSoFar >= EightInSeven.instance.getMaxVisibilitySteps()+Math.max(subChunk, 15 - subChunk))
                return;

            for (ForgeDirection direction : ForgeDirection.values()) {
                if (viewVector.dotProduct(Vec3.createVectorHelper(direction.offsetX, direction.offsetY, direction.offsetZ)) > 0)
                    continue;

                if (!graphNode[subChunk].isVisibleThrough(sourceSide, direction))
                    continue;

                int newSubChunk = subChunk;
                VisibilityChunkMixin newChunk = this;

                if (direction == ForgeDirection.UP) {
                    newSubChunk++;
                } else if (direction == ForgeDirection.DOWN) {
                    newSubChunk--;
                } else {
                    newChunk = (VisibilityChunkMixin)(Object)worldObj.getChunkFromChunkCoords(this.xPosition+direction.offsetX, this.zPosition+direction.offsetZ);
                }

                if (newChunk == null || newSubChunk < 0 || newSubChunk >= 16)
                    continue;

                //Frustum culling check

                int cost = 1;
                if (lacksLight(newChunk, newSubChunk))
                    cost += 3;
                if (direction == ForgeDirection.DOWN && newSubChunk*16 < worldObj.provider.getAverageGroundLevel())
                    cost++;
                walkingQueue.add(new VisibilityWalkStep(newChunk, newSubChunk, ForgeDirection.VALID_DIRECTIONS[ForgeDirection.OPPOSITES[direction.ordinal()]], stepsSoFar+cost));
            }
        }
    }

    private boolean lacksLight(VisibilityChunkMixin chunk, int subChunk) {
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[subChunk];
        byte[] skylightData = storage.getSkylightArray().data;

        for (int i = 0; i < skylightData.length; i++) {
            if (skylightData[i] != 0)
                return false;
        }

        byte[] torchData = storage.getBlocklightArray().data;

        for (int i = 0; i < torchData.length; i++) {
            if (torchData[i] != 0)
                return false;
        }

        return true;
    }

    @Shadow
    public World worldObj;
    /** The x coordinate of the chunk. */
    @Shadow
    public final int xPosition=0;
    /** The z coordinate of the chunk. */
    @Shadow
    public final int zPosition=0;

    @Shadow
    public abstract ExtendedBlockStorage[] getBlockStorageArray();

    @Inject(method="<init>", at=@At("RETURN"))
    private void onConstructed(World p_i1995_1_, int p_i1995_2_, int p_i1995_3_, CallbackInfo info) {
        initGraphNode();
    }

    @Inject(method="<init>", at=@At("RETURN"))
    private void onConstructed(World p_i45446_1_, Block[] p_i45446_2_, int p_i45446_3_, int p_i45446_4_, CallbackInfo info) {
        initGraphNode();
    }

    @Inject(method="<init>", at=@At("RETURN"))
    private void onConstructed(World p_i45447_1_, Block[] p_i45447_2_, byte[] p_i45447_3_, int p_i45447_4_, int p_i45447_5_, CallbackInfo info) {
        initGraphNode();
    }

    private void initGraphNode() {
        graphNode = new VisibilityGraphNode[16];

        for (int i = 0; i < 16; i++) {
            graphNode[i] = new VisibilityGraphNode();
            invalidateVisibility(i);
        }
    }
}

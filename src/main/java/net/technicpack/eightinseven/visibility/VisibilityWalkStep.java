package net.technicpack.eightinseven.visibility;

import net.minecraftforge.common.util.ForgeDirection;
import net.technicpack.eightinseven.mixin.VisibilityChunkMixin;

public class VisibilityWalkStep {
    private VisibilityChunkMixin mixinChunk;
    private int subChunk;
    private ForgeDirection sourceSide;
    private int stepsSoFar;

    public VisibilityWalkStep(VisibilityChunkMixin mixinChunk, int subChunk, ForgeDirection sourceSide, int stepsSoFar) {
        this.stepsSoFar = stepsSoFar;
        this.mixinChunk = mixinChunk;
        this.subChunk = subChunk;
        this.sourceSide = sourceSide;
        this.stepsSoFar = stepsSoFar;
    }

    public VisibilityChunkMixin getMixinChunk() { return mixinChunk; }
    public int getSubChunk() { return subChunk; }
    public ForgeDirection getSourceSide() { return sourceSide; }
    public int getStepsSoFar() { return stepsSoFar; }
}

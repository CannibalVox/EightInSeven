package net.technicpack.eightinseven.visibility;

import net.minecraftforge.common.util.ForgeDirection;
import net.technicpack.eightinseven.EightInSeven;

public class VisibilityGraphNode {
    private short visibilityData;
    private boolean isWorking = false;

    public void setVisibilityData(short visibilityData) {
        this.visibilityData = (short)((this.visibilityData & 0x8000) | (visibilityData & 0x7FFF));
        isWorking = false;
    }

    public short getVisibilityData() {
        return visibilityData;
    }

    public void invalidate() {
        visibilityData = (short)0xFFFF;
    }

    public void markNeedsWork() {
        visibilityData |= 0x8000;
    }

    public void clearConnections() {
        visibilityData = (short)0x8000;
    }

    public void markBeingWorked() {
        visibilityData &= 0x7FFF;
        isWorking = true;
    }

    public boolean isWorking() { return isWorking; }
    public boolean needsWork() { return (this.visibilityData & 0x8000) != 0; }

    public boolean isVisibleThrough(ForgeDirection from, ForgeDirection to) {
        if (from == ForgeDirection.UNKNOWN || to == ForgeDirection.UNKNOWN)
            return false;

        int flag = getPathFlag(from, to);
        flag = 1 << flag;

        return (visibilityData & flag) != 0;
    }

    public void addVisibility(ForgeDirection from, ForgeDirection to) {
        if (from == ForgeDirection.UNKNOWN || to == ForgeDirection.UNKNOWN)
            return;

        int flag = getPathFlag(from, to);
        flag = 1 << flag;
        visibilityData |= flag;
    }

    private static int getPathFlag(ForgeDirection from, ForgeDirection to) {
        if (from == ForgeDirection.UNKNOWN || to == ForgeDirection.UNKNOWN)
            return -1;

        int fromOrdinal = from.ordinal();
        int toOrdinal = to.ordinal();

        int minOrdinal = Math.min(fromOrdinal, toOrdinal);
        int maxOrdinal = Math.max(fromOrdinal, toOrdinal);

        int currentFlag = 0;
        int ordinalSize = 5;
        for (int i = 0; i < minOrdinal; i++) {
            currentFlag += ordinalSize;
            ordinalSize--;
        }

        currentFlag += (maxOrdinal - minOrdinal - 1);
        return currentFlag;
    }
}

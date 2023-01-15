package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.EnumFacing;

public interface IRedstoneTarget extends ITarget {
    boolean addRedstonePower(EnumFacing relativeFacing, int strength);
    void subtractRedstonePower(EnumFacing relativeFacing, int strength);
}

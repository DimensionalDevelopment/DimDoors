package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.util.EnumFacing;

public interface IRedstoneTarget extends ITarget {
    public boolean addRedstonePower(EnumFacing relativeFacing, int strength);
    public void subtractRedstonePower(EnumFacing relativeFacing, int strength);
}

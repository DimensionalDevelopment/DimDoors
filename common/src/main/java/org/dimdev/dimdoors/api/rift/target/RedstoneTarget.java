package org.dimdev.dimdoors.api.rift.target;

import net.minecraft.core.Direction;

public interface RedstoneTarget extends Target {
	boolean addRedstonePower(Direction relativeFacing, int strength);

	void subtractRedstonePower(Direction relativeFacing, int strength);
}

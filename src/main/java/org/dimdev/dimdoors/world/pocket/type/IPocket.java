package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.util.math.BlockBox;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public interface IPocket<T extends IPocket<T>> extends IAbstractPocket<T> {

	void setBox(BlockBox box);

	void setVirtualLocation(VirtualLocation virtualLocation);
}

package org.dimdev.dimdoors.world.pocket.type;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public interface IAbstractPocket<T extends IAbstractPocket<T>> {
	void setID(int id);

	void setWorld(RegistryKey<World> world);
}

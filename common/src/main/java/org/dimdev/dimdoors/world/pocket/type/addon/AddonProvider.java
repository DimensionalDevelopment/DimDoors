package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface AddonProvider {
	<C extends PocketAddon> C getAddon(ResourceLocation id);

	boolean hasAddon(ResourceLocation id);

	<C extends PocketAddon> boolean addAddon(C addon);

	default void ensureIsPocket() {
		if (! (this instanceof Pocket)) throw new UnsupportedOperationException("Cannot apply pocket addons to non Pocket Object.");
	}
}

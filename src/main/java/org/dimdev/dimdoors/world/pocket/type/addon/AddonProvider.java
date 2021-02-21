package org.dimdev.dimdoors.world.pocket.type.addon;

import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface AddonProvider {
	<C extends PocketAddon> C getAddon(Identifier id);

	boolean hasAddon(Identifier id);

	<C extends PocketAddon> boolean addAddon(C addon);

	default void ensureIsPocket() {
		if (! (this instanceof Pocket)) throw new UnsupportedOperationException("Cannot apply pocket addons to non Pocket Object.");
	}
}

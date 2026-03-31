package org.dimdev.dimdoors.world.pocket.type.addon;

public interface AddonProvider {

    <C extends PocketAddon> boolean addAddon(C addon);

	default void ensureIsPocket() {
		if (! (this instanceof Pocket)) throw new UnsupportedOperationException("Cannot apply pocket addons to non Pocket Object.");
	}
}

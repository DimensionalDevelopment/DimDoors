package org.dimdev.dimdoors.world.pocket.type.addon;

import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface PocketAddon<A extends PocketAddon<A>> {

	interface PocketBuilderExtension<T extends Pocket.PocketBuilder<T, ?>, P extends PocketAddon<P>> {
		<C extends PocketAddon.PocketBuilderAddon<P>, P extends PocketAddon<P>> C getAddon(Class<C> addonClass);

		void initAddons();

		T getSelf();
	}

	interface PocketBuilderAddon<T extends PocketAddon> {

		void apply(T pocket);
	}
}

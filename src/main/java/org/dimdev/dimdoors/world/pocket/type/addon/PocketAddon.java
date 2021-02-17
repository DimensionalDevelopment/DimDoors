package org.dimdev.dimdoors.world.pocket.type.addon;

import org.dimdev.dimdoors.world.pocket.type.IPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

public interface PocketAddon<C extends IPocket<C>> extends IPocket<C>{

	interface PocketBuilderExtension<T extends Pocket.PocketBuilder<T, P>, P extends IPocket<P>> extends Pocket.IPocketBuilder<T, P> {
		void initAddons();

		T getSelf();
	}

	interface PocketBuilderAddon<T> {
		void apply(T pocket);
	}
}

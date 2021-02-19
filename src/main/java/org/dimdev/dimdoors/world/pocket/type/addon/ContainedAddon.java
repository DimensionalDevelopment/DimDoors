package org.dimdev.dimdoors.world.pocket.type.addon;


import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;

public interface ContainedAddon extends PocketAddon {
	Identifier getContainerId();

	@Override
	default void addAddon(Map<Identifier, PocketAddon> addons) {
		throw new UnsupportedOperationException("ContainedEventListenerAddons cannot be attach to a Pocket directly");
	}

	interface ContainedBuilderAddon<T extends ContainedAddon> extends PocketAddon.PocketBuilderAddon<T> {
		Identifier getContainerId();

		AddonContainer<T> supplyContainer();

		@Override
		default void apply(Pocket pocket) {
			AddonContainer<T> container;
			if (pocket.hasAddon(getContainerId())) {
				container = pocket.getAddon(getContainerId());
			} else {
				container = supplyContainer();
				pocket.addAddon(container);
			}
			container.add(buildAddon());
		}

		T buildAddon();
	}
}

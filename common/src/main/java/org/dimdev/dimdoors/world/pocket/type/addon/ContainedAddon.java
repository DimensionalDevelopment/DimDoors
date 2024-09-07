package org.dimdev.dimdoors.world.pocket.type.addon;


import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Map;

public interface ContainedAddon extends PocketAddon {
	ResourceLocation getContainerId();

	@Override
	default void addAddon(Map<ResourceLocation, PocketAddon> addons) {
		throw new UnsupportedOperationException("ContainedEventListenerAddons cannot be attach to a Pocket directly");
	}

	interface ContainedBuilderAddon<T extends ContainedAddon> extends PocketBuilderAddon<T> {
		ResourceLocation getContainerId();

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

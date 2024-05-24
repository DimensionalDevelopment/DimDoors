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

			var addon = pocket.getAddon(getContainerId());

			if(addon.isPresent()) {
				container = (AddonContainer<T>) addon.get();
			} else {
				container = supplyContainer();
				pocket.addAddon(container);
			}
			container.add(buildAddon());
		}

		T buildAddon();
	}
}

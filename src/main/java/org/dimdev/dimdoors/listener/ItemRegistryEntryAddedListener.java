package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

public record ItemRegistryEntryAddedListener(DimensionalDoorItemRegistrar registrar) implements RegistryEntryAddedCallback<Item> {

	@Override
	public void onEntryAdded(int rawId, Identifier id, Item object) {
		registrar.handleEntry(id, object);
	}
}

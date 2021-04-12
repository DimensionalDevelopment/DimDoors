package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

public class ItemRegistryEntryAddedListener implements RegistryEntryAddedCallback<Item> {
	private final DimensionalDoorItemRegistrar registrar;

	public ItemRegistryEntryAddedListener(DimensionalDoorItemRegistrar registrar) {
		this.registrar = registrar;
	}

	@Override
	public void onEntryAdded(int rawId, Identifier id, Item object) {
		registrar.handleEntry(id, object);
	}
}

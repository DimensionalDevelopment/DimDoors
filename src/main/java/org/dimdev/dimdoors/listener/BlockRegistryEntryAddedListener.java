package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;

public record BlockRegistryEntryAddedListener(DimensionalDoorBlockRegistrar registrar) implements RegistryEntryAddedCallback<Block> {
	@Override
	public void onEntryAdded(int rawId, Identifier id, Block object) {
		registrar.handleEntry(id, object);
	}
}

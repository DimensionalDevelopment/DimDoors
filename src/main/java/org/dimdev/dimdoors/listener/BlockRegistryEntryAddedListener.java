package org.dimdev.dimdoors.listener;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;

public class BlockRegistryEntryAddedListener implements RegistryEntryAddedCallback<Block> {
	private final DimensionalDoorBlockRegistrar registrar;

	public BlockRegistryEntryAddedListener(DimensionalDoorBlockRegistrar registrar) {
		this.registrar = registrar;
	}

	@Override
	public void onEntryAdded(int rawId, Identifier id, Block object) {
		registrar.handleEntry(id, object);
	}
}

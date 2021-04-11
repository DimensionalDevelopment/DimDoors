package org.dimdev.dimdoors.block.door;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.listener.BlockRegistryEntryAddedListener;

import java.util.ArrayList;
import java.util.function.Function;

public class DimensionalDoorBlockRegistrar {
	private static final String PREFIX = "autogen_dimensional_";

	private final Registry<Block> registry;
	private final DimensionalDoorItemRegistrar itemRegistrar;

	private final BiMap<Identifier, Identifier> mappedDoorBlocks = HashBiMap.create();

	public DimensionalDoorBlockRegistrar(Registry<Block> registry, DimensionalDoorItemRegistrar itemRegistrar) {
		this.registry = registry;
		this.itemRegistrar = itemRegistrar;

		init();
		RegistryEntryAddedCallback.event(registry).register(new BlockRegistryEntryAddedListener(this));
	}

	private void init() {
		new ArrayList<>(registry.getEntries())
				.forEach(entry -> handleEntry(entry.getKey().getValue(), entry.getValue()));
	}

	public void handleEntry(Identifier identifier, Block block) {
		switch (DoorBlockClassCase.getCase(block)) {
			case DOOR_BLOCK:
				register(identifier, block, DimensionalDoorBlock::new);
				break;
			case TRAPDOOR_BLOCK:
				register(identifier, block, DimensionalTrapdoorBlock::new);
				break;
			default:
				// do nothing
				break;
		}
	}

	private void register(Identifier identifier, Block block, Function<AbstractBlock.Settings, ? extends Block> constructor) {
		Identifier gennedId = new Identifier("dimdoors", PREFIX + identifier.getPath());
		Block newBlock = Registry.register(registry, gennedId, constructor.apply(FabricBlockSettings.copy(block)));
		ModBlockEntityTypes.ENTRANCE_RIFT_BLOCKS.add(newBlock);
		mappedDoorBlocks.put(gennedId, identifier);
		itemRegistrar.notifyBlockMapped(block, newBlock);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			BlockRenderLayerMap.INSTANCE.putBlock(newBlock, RenderLayer.getCutout());
		}
	}

	public Identifier get(Identifier identifier) {
		return mappedDoorBlocks.get(identifier);
	}

	public boolean isMapped(Identifier identifier) {
		return mappedDoorBlocks.containsKey(identifier);
	}
}

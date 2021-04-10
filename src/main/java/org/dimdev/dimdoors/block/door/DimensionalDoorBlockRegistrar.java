package org.dimdev.dimdoors.block.door;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DimensionalDoorBlockRegistrar {
	private static final String PREFIX = "autogen_dimensional_";

	private final Registry<Block> registry;

	private final Map<Identifier, Identifier> mappedDoorBlocks = new HashMap<>();

	public DimensionalDoorBlockRegistrar(Registry<Block> registry) {
		this.registry = registry;
	}

	public void init() {
		new ArrayList<>(registry.getEntries())
				.forEach(entry -> handleEntry(entry.getKey().getValue(), entry.getValue()));
	}

	public void handleEntry(Identifier identifier, Block block) {
		switch (DoorClassCase.getCase(block)) {
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
		mappedDoorBlocks.put(gennedId, identifier);

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

	// Just in case we want to support more Door classes in the future.
	// Need to move away from an enum for better mod support though.
	private enum DoorClassCase {
		NONE(null),
		DOOR_BLOCK(DoorBlock.class),
		TRAPDOOR_BLOCK(TrapdoorBlock.class);

		private static final Map<Class<? extends Block>, DoorClassCase> CASE_MAP = new HashMap<>();

		static {
			for (DoorClassCase doorClassCase : DoorClassCase.values()) {
				CASE_MAP.put(doorClassCase.doorClazz, doorClassCase);
			}
		}

		public static DoorClassCase getCase(Block block) {
			DoorClassCase doorClassCase = CASE_MAP.get(block.getClass());
			return doorClassCase == null ? NONE : doorClassCase;
		}

		private final Class<? extends Block> doorClazz;

		DoorClassCase(Class<? extends Block> doorClazz) {
			this.doorClazz = doorClazz;
		}
	}
}

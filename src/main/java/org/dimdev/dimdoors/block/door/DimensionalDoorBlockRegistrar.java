package org.dimdev.dimdoors.block.door;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.listener.BlockRegistryEntryAddedListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

public class DimensionalDoorBlockRegistrar {
	private static final String PREFIX = "block_ag_dim_";

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

	public void handleEntry(Identifier identifier, Block original) {
		if (DimensionalDoorsInitializer.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (!(original instanceof DimensionalDoorBlock) && original instanceof DoorBlock) {
				register(identifier, original, DimensionalDoorBlockRegistrar::createAutoGenDimensionalDoorBlock);
			} else if (!(original instanceof DimensionalTrapdoorBlock) && original instanceof TrapdoorBlock) {
				register(identifier, original, DimensionalDoorBlockRegistrar::createAutoGenDimensionalTrapdoorBlock);
			}
		}
	}

	private void register(Identifier identifier, Block original, BiFunction<AbstractBlock.Settings, Block, ? extends Block> constructor) {
		Identifier gennedId = new Identifier("dimdoors", PREFIX + identifier.getNamespace() + "_" + identifier.getPath());
		Block dimBlock = Registry.register(registry, gennedId, constructor.apply(FabricBlockSettings.copy(original), original));
		ModBlockEntityTypes.ENTRANCE_RIFT.addBlock(dimBlock);
		mappedDoorBlocks.put(gennedId, identifier);
		itemRegistrar.notifyBlockMapped(original, dimBlock);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			putCutout(dimBlock);
		}
	}

	private void putCutout(Block original) {
		BlockRenderLayerMap.INSTANCE.putBlock(original, RenderLayer.getCutout());
	}

	public Identifier get(Identifier identifier) {
		return mappedDoorBlocks.get(identifier);
	}

	public boolean isMapped(Identifier identifier) {
		return mappedDoorBlocks.containsKey(identifier);
	}

	private static <T extends Comparable<T>> BlockState transferProperty(BlockState from, BlockState to, Property<T> property) {
		return to.with(property, from.get(property));
	}

	private static AutoGenDimensionalDoorBlock createAutoGenDimensionalDoorBlock(AbstractBlock.Settings settings, Block originalBlock) {
		return new AutoGenDimensionalDoorBlock(settings, originalBlock) {
			@Override
			protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, originalBlock, WATERLOGGED);
			}
		};
	}

	private static AutoGenDimensionalTrapdoorBlock createAutoGenDimensionalTrapdoorBlock(AbstractBlock.Settings settings, Block originalBlock) {
		return new AutoGenDimensionalTrapdoorBlock(settings, originalBlock) {
			@Override
			protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, originalBlock, WATERLOGGED);
			}
		};
	}

	private static void appendPropertiesOverride(StateManager.Builder<Block, BlockState> builder, Block originalBlock, Property<?>... requiredProperties) {
		HashSet<Property<?>> properties = new HashSet<>(originalBlock.getStateManager().getProperties());
		properties.addAll(List.of(requiredProperties));
		builder.add(properties.toArray(new Property[0]));
	}

	private static class AutoGenDimensionalDoorBlock extends DimensionalDoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalDoorBlock(Settings settings, Block originalBlock) {
			super(settings);
			this.originalBlock = originalBlock;

			BlockState state = this.getStateManager().getDefaultState();
			BlockState originalState = originalBlock.getDefaultState();
			for (Property<?> property : originalBlock.getDefaultState().getProperties()) {
				state = transferProperty(originalState, state, property);
			}
			setDefaultState(state.with(WATERLOGGED, false));
		}

		@Override
		protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
			// This method has to be defined in an anonymous inner class,
			// 		since Block#appendProperties is run before originalBlock can be set.
			throw new RuntimeException("AutoGenDimensionalDoorBlock should be instantiated as anonymous inner class overriding appendProperties!");
		}

		@Override
		public MutableText getName() {
			return new TranslatableText("dimdoors.autogen_block_prefix").append(originalBlock.getName());
		}
	}

	private static class AutoGenDimensionalTrapdoorBlock extends DimensionalTrapdoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalTrapdoorBlock(Settings settings, Block originalBlock) {
			super(settings);
			this.originalBlock = originalBlock;

			BlockState state = this.getStateManager().getDefaultState();
			BlockState originalState = originalBlock.getDefaultState();
			for (Property<?> property : originalBlock.getDefaultState().getProperties()) {
				state = transferProperty(originalState, state, property);
			}
			setDefaultState(state.with(WATERLOGGED, false));
		}

		@Override
		protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
			// This method has to be defined in an anonymous inner class,
			// 		since Block#appendProperties is run before originalBlock can be set.
			throw new RuntimeException("AutoGenDimensionalTrapdoorBlock should be instantiated as anonymous inner class overriding appendProperties!");
		}

		@Override
		public MutableText getName() {
			return new TranslatableText("dimdoors.autogen_block_prefix").append(originalBlock.getName());
		}
	}
}

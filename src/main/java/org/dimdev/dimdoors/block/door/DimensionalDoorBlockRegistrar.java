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
import net.minecraft.registry.Registry;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;
import org.dimdev.dimdoors.listener.BlockRegistryEntryAddedListener;

import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

public class DimensionalDoorBlockRegistrar<T extends Block & DoorSoundProvider> {
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
		registry.getEntrySet().forEach(entry -> handleEntry(entry.getKey().getValue(), entry.getValue()));
	}

	public void handleEntry(Identifier identifier, Block original) {
		if (DimensionalDoors.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (!(original instanceof DimensionalDoorBlock) && original instanceof DoorBlock doorBlock) {
				register(identifier, doorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalDoorBlock);
			} else if (!(original instanceof DimensionalTrapdoorBlock) && original instanceof TrapdoorBlock trapdoorBlock) {
				register(identifier, trapdoorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalTrapdoorBlock);
			}
		}
	}

	private void register(Identifier identifier, DoorSoundProvider original, BiFunction<AbstractBlock.Settings, DoorSoundProvider, ? extends Block> constructor) {
		Identifier gennedId = DimensionalDoors.id(PREFIX + identifier.getNamespace() + "_" + identifier.getPath());
		Block dimBlock = Registry.register(registry, gennedId, constructor.apply(FabricBlockSettings.copy((AbstractBlock) original), original));
		ModBlockEntityTypes.ENTRANCE_RIFT.addBlock(dimBlock);
		mappedDoorBlocks.put(gennedId, identifier);
		itemRegistrar.notifyBlockMapped((Block) original, dimBlock);

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

	private static AutoGenDimensionalDoorBlock createAutoGenDimensionalDoorBlock(AbstractBlock.Settings settings, DoorSoundProvider originalBlock) {
		return new AutoGenDimensionalDoorBlock(settings, originalBlock) {
			@Override
			protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, (Block) originalBlock, WATERLOGGED);
			}
		};
	}

	private static AutoGenDimensionalTrapdoorBlock createAutoGenDimensionalTrapdoorBlock(AbstractBlock.Settings settings, DoorSoundProvider originalBlock) {
		return new AutoGenDimensionalTrapdoorBlock(settings, originalBlock) {
			@Override
			protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, (Block) originalBlock, WATERLOGGED);
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

		public AutoGenDimensionalDoorBlock(Settings settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getCloseSound(), originalBlock.getOpenSound());
			this.originalBlock = (Block) originalBlock;

			BlockState state = this.getStateManager().getDefaultState();
			BlockState originalState = this.originalBlock.getDefaultState();
			for (Property<?> property : this.originalBlock.getDefaultState().getProperties()) {
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
			return MutableText.of(new TranslatableTextContent("dimdoors.autogen_block_prefix")).append(originalBlock.getName());
		}
	}

	private static class AutoGenDimensionalTrapdoorBlock extends DimensionalTrapdoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalTrapdoorBlock(Settings settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getCloseSound(), originalBlock.getOpenSound());
			this.originalBlock = (Block) originalBlock;

			BlockState state = this.getStateManager().getDefaultState();
			BlockState originalState = this.originalBlock.getDefaultState();
			for (Property<?> property : this.originalBlock.getDefaultState().getProperties()) {
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
			return MutableText.of(new TranslatableTextContent("dimdoors.autogen_block_prefix")).append(originalBlock.getName());
		}
	}
}

package org.dimdev.dimdoors.block.door;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.item.DimensionalDoorItemRegistrar;

public class DimensionalDoorBlockRegistrar {
	private static final String PREFIX = "block_ag_dim_";

	private final Registry<Block> registry;
	private final DimensionalDoorItemRegistrar itemRegistrar;

	private final BiMap<ResourceLocation, ResourceLocation> mappedDoorBlocks = HashBiMap.create();

	public DimensionalDoorBlockRegistrar(Registry<Block> registry, DimensionalDoorItemRegistrar itemRegistrar) {
		this.registry = registry;
		this.itemRegistrar = itemRegistrar;

		init();
		RegistryEntryAddedCallback.event(registry).register((rawId, id, object) -> handleEntry(id, object));
	}

	private void init() {
		new ArrayList<>(registry.entrySet()).forEach(entry -> handleEntry(entry.getKey().location(), entry.getValue()));
	}

	public void handleEntry(ResourceLocation identifier, Block original) {
		if (DimensionalDoors.getConfig().getDoorsConfig().isAllowed(identifier)) {
			if (!(original instanceof DimensionalDoorBlock) && original instanceof DoorBlock doorBlock) {
				register(identifier, doorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalDoorBlock);
			} else if (!(original instanceof DimensionalTrapdoorBlock) && original instanceof TrapDoorBlock trapdoorBlock) {
				register(identifier, trapdoorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalTrapdoorBlock);
			}
		}
	}

	private void register(ResourceLocation identifier, DoorSoundProvider original, BiFunction<BlockBehaviour.Properties, DoorSoundProvider, ? extends Block> constructor) {
		ResourceLocation gennedId = DimensionalDoors.id(PREFIX + identifier.getNamespace() + "_" + identifier.getPath());
		Block dimBlock = Registry.register(registry, gennedId, constructor.apply(FabricBlockSettings.copy((BlockBehaviour) original), original));
		ModBlockEntityTypes.ENTRANCE_RIFT.addBlock(dimBlock);
		mappedDoorBlocks.put(gennedId, identifier);
		itemRegistrar.notifyBlockMapped((Block) original, dimBlock);

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			putCutout(dimBlock);
		}
	}

	private void putCutout(Block original) {
		BlockRenderLayerMap.INSTANCE.putBlock(original, RenderType.cutout());
	}

	public ResourceLocation get(ResourceLocation identifier) {
		return mappedDoorBlocks.get(identifier);
	}

	public boolean isMapped(ResourceLocation identifier) {
		return mappedDoorBlocks.containsKey(identifier);
	}

	private static <T extends Comparable<T>> BlockState transferProperty(BlockState from, BlockState to, Property<T> property) {
		return to.setValue(property, from.getValue(property));
	}

	private static AutoGenDimensionalDoorBlock createAutoGenDimensionalDoorBlock(BlockBehaviour.Properties settings, DoorSoundProvider originalBlock) {
		return new AutoGenDimensionalDoorBlock(settings, originalBlock) {
			@Override
			protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, (Block) originalBlock, WATERLOGGED);
			}
		};
	}

	private static AutoGenDimensionalTrapdoorBlock createAutoGenDimensionalTrapdoorBlock(BlockBehaviour.Properties settings, DoorSoundProvider originalBlock) {
		return new AutoGenDimensionalTrapdoorBlock(settings, originalBlock) {
			@Override
			protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
				appendPropertiesOverride(builder, (Block) originalBlock, WATERLOGGED);
			}
		};
	}

	private static void appendPropertiesOverride(StateDefinition.Builder<Block, BlockState> builder, Block originalBlock, Property<?>... requiredProperties) {
		HashSet<Property<?>> properties = new HashSet<>(originalBlock.getStateDefinition().getProperties());
		properties.addAll(List.of(requiredProperties));
		builder.add(properties.toArray(new Property[0]));
	}

	private static class AutoGenDimensionalDoorBlock extends DimensionalDoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalDoorBlock(Properties settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getCloseSound(), originalBlock.getOpenSound());
			this.originalBlock = (Block) originalBlock;

			BlockState state = this.getStateDefinition().any();
			BlockState originalState = this.originalBlock.defaultBlockState();
			for (Property<?> property : this.originalBlock.defaultBlockState().getProperties()) {
				state = transferProperty(originalState, state, property);
			}
			registerDefaultState(state.setValue(WATERLOGGED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			// This method has to be defined in an anonymous inner class,
			// 		since Block#appendProperties is run before originalBlock can be set.
			throw new RuntimeException("AutoGenDimensionalDoorBlock should be instantiated as anonymous inner class overriding appendProperties!");
		}

		@Override
		public MutableComponent getName() {
			return MutableComponent.create(new TranslatableContents("dimdoors.autogen_block_prefix")).append(originalBlock.getName());
		}
	}

	private static class AutoGenDimensionalTrapdoorBlock extends DimensionalTrapdoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalTrapdoorBlock(Properties settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getCloseSound(), originalBlock.getOpenSound());
			this.originalBlock = (Block) originalBlock;

			BlockState state = this.getStateDefinition().any();
			BlockState originalState = this.originalBlock.defaultBlockState();
			for (Property<?> property : this.originalBlock.defaultBlockState().getProperties()) {
				state = transferProperty(originalState, state, property);
			}
			registerDefaultState(state.setValue(WATERLOGGED, false));
		}

		@Override
		protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
			// This method has to be defined in an anonymous inner class,
			// 		since Block#appendProperties is run before originalBlock can be set.
			throw new RuntimeException("AutoGenDimensionalTrapdoorBlock should be instantiated as anonymous inner class overriding appendProperties!");
		}

		@Override
		public MutableComponent getName() {
			return MutableComponent.create(new TranslatableContents("dimdoors.autogen_block_prefix")).append(originalBlock.getName());
		}
	}
}

package org.dimdev.dimdoors.block.door;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.utils.Env;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;

import java.util.*;
import java.util.function.BiFunction;

public class DimensionalDoorBlockRegistrar {
	public static final String PREFIX = "block_ag_dim_";

	private final Registrar<Block> registry;
	private final DimensionalDoorItemRegistrar itemRegistrar;

	private final BiMap<ResourceLocation, ResourceLocation> mappedDoorBlocks = HashBiMap.create();

	public DimensionalDoorBlockRegistrar(DimensionalDoorItemRegistrar itemRegistrar) {
		this.registry = RegistrarManager.get(DimensionalDoors.MOD_ID).get(Registries.BLOCK);
		this.itemRegistrar = itemRegistrar;

		if(Platform.isFabric()) {
			init();
		}

		RegistrarManager.get(DimensionalDoors.MOD_ID).forRegistry(Registries.BLOCK, registrar -> {
			new ArrayList<>(registrar.entrySet()).forEach(entry -> handleEntry(registrar, entry.getKey().location(), entry.getValue()));
		});

		LifecycleEvent.SETUP.register(() -> mappedDoorBlocks.keySet().stream().map(BuiltInRegistries.BLOCK::get).forEach(a -> ModBlockEntityTypes.ENTRANCE_RIFT.get().addBlock(a)));
	}

	private void init() {
		new ArrayList<>(registry.entrySet()).forEach(entry -> handleEntry(registry, entry.getKey().location(), entry.getValue()));
	}

	public void handleEntry(Registrar<Block> registrar, ResourceLocation ResourceLocation, Block original) {
		if (DimensionalDoors.getConfig().getDoorsConfig().isAllowed(ResourceLocation)) {
			if (!(original instanceof DimensionalDoorBlock) && original instanceof DoorBlock doorBlock) {
				register(registrar, ResourceLocation, doorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalDoorBlock);
			} else if (!(original instanceof DimensionalTrapdoorBlock) && original instanceof TrapDoorBlock trapdoorBlock) {
//				register(registrar, ResourceLocation, trapdoorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalTrapdoorBlock); //TODO: readd once plan for handling trapdoors is figured out.
			}
		}
	}

	private void register(Registrar<Block> registrar, ResourceLocation location, DoorSoundProvider original, BiFunction<BlockBehaviour.Properties, DoorSoundProvider, Block> constructor) {
		ResourceLocation gennedId = DimensionalDoors.id(PREFIX + location.getNamespace() + "_" + location.getPath());

		if(mappedDoorBlocks.containsKey(gennedId)) return;

		Block dimBlock = registrar.register(gennedId, () -> constructor.apply(BlockBehaviour.Properties.copy((BlockBehaviour) original), original)).get();
//		ModBlockEntityTypes.ENTRANCE_RIFT.get().addBlock(dimBlock); //TODO: Add
		mappedDoorBlocks.put(gennedId, location);
		itemRegistrar.notifyBlockMapped((Block) original, dimBlock);

		if (Platform.getEnvironment() == Env.CLIENT) {
			putCutout(dimBlock);
		}
	}

	private void putCutout(Block original) {
		RenderTypeRegistry.register(RenderType.cutout(), original);
	}

	public ResourceLocation get(ResourceLocation ResourceLocation) {
		return mappedDoorBlocks.get(ResourceLocation);
	}

	public boolean isMapped(ResourceLocation ResourceLocation) {
		return mappedDoorBlocks.containsKey(ResourceLocation);
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

    public void forEach() {
    }

	public Set<ResourceLocation> getGennedIds() {
		return mappedDoorBlocks.keySet();
	}

	public static class AutoGenDimensionalDoorBlock extends DimensionalDoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalDoorBlock(Properties settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getSetType());
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
			return Component.translatable("dimdoors.autogen_block_prefix", originalBlock.getName());
		}

		public Block getOriginalBlock() {
			return originalBlock;
		}
	}

	private static class AutoGenDimensionalTrapdoorBlock extends DimensionalTrapdoorBlock {
		private final Block originalBlock;

		public AutoGenDimensionalTrapdoorBlock(Properties settings, DoorSoundProvider originalBlock) {
			super(settings, originalBlock.getSetType());
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
			return Component.translatable("dimdoors.autogen_block_prefix", originalBlock.getName());
		}
	}
}
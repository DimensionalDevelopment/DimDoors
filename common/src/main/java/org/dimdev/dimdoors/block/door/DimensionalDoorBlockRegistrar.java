package org.dimdev.dimdoors.block.door;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.utils.Env;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.DoorSoundProvider;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.dimdev.dimdoors.block.DimensionalPortalBlock.Dummy.checkType;

public class DimensionalDoorBlockRegistrar {
	public static final String PREFIX = "block_ag_dim_";

	private final Registrar<Block> registry;
	private final DimensionalDoorItemRegistrar itemRegistrar;

	private final BiMap<ResourceLocation, ResourceLocation> mappedDoorBlocks = HashBiMap.create();

	private final Map<ResourceLocation, AutoGenLogic<? extends RiftBlockEntity>> customDoorFunction = new HashMap<>();

	public record AutoGenLogic<T extends RiftBlockEntity>(Supplier<MutableBlockEntityType<T>> blockEntityType,
														  BiFunction<BlockBehaviour.Properties, DoorSoundProvider, Block> function) {
		public void register(Block block) {
			blockEntityType.get().addBlock(block);
		}
	}

	private static AutoGenLogic<EntranceRiftBlockEntity> defaultLogic = new AutoGenLogic<>(ModBlockEntityTypes.ENTRANCE_RIFT, DimensionalDoorBlockRegistrar::createAutoGenDimensionalDoorBlock);

	public DimensionalDoorBlockRegistrar(DimensionalDoorItemRegistrar itemRegistrar) {
		this.registry = Registries.get(DimensionalDoors.MOD_ID).get(Registry.BLOCK);
		this.itemRegistrar = itemRegistrar;

//		if(Platform.isFabric()) {
//			init();
//		}

		if (Platform.isForge()) {
			Registries.get(DimensionalDoors.MOD_ID).forRegistry(Registry.BLOCK_REGISTRY, registrar -> {
				new ArrayList<>(registrar.entrySet()).forEach(entry -> handleEntry(registrar, entry.getKey().location(), entry.getValue()));
			});
		}

		LifecycleEvent.SETUP.register(() -> {
			if (Platform.isFabric()) {
				Registries.get(DimensionalDoors.MOD_ID).forRegistry(Registry.BLOCK_REGISTRY, registrar -> {
					new ArrayList<>(registrar.entrySet()).forEach(entry -> handleEntry(registrar, entry.getKey().location(), entry.getValue()));
				});
			}

			mappedDoorBlocks.keySet().forEach(location -> {
				var block = Registry.BLOCK.get(location);
				var logic = customDoorFunction.getOrDefault(location, defaultLogic);
				logic.register(block);
			});
		});
	}

	private void init() {
		new ArrayList<>(registry.entrySet()).forEach(entry -> handleEntry(registry, entry.getKey().location(), entry.getValue()));
	}

	public void handleEntry(Registrar<Block> registrar, ResourceLocation location, Block original) {
		if (DimensionalDoors.getConfig().getDoorsConfig().isAllowed(location)) {
			if (!(original instanceof DimensionalDoorBlock) && original instanceof DoorBlock doorBlock) {
				System.out.println("Rare -> Registering: " + location);
				register(registrar, location, doorBlock, customDoorFunction.getOrDefault(location, defaultLogic).function());
			} else if (!(original instanceof DimensionalTrapdoorBlock) && original instanceof TrapDoorBlock trapdoorBlock) {
//				register(registrar, ResourceLocation, trapdoorBlock, DimensionalDoorBlockRegistrar::createAutoGenDimensionalTrapdoorBlock); //TODO: readd once plan for handling trapdoors is figured out.
			}
		}
	}

	private void register(Registrar<Block> registrar, ResourceLocation location, DoorSoundProvider original, BiFunction<BlockBehaviour.Properties, DoorSoundProvider, Block> constructor) {
		ResourceLocation gennedId = DimensionalDoors.id(PREFIX + location.getNamespace() + "_" + location.getPath());

		if (mappedDoorBlocks.containsKey(gennedId)) return;

		Block dimBlock = registrar.register(gennedId, () -> constructor.apply(BlockBehaviour.Properties.copy((BlockBehaviour) original).requiresCorrectToolForDrops(), original)).get();
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

	public <T extends DoorBlock, K extends RiftBlockEntity> void registerCustomDoorLogic(ResourceLocation id, AutoGenLogic<K> logic) {
		customDoorFunction.putIfAbsent(id, logic);
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

	private static AutoGenDimensionalDoorBlock.AutoGenDimensionalTrapdoorBlock createAutoGenDimensionalTrapdoorBlock(BlockBehaviour.Properties settings, DoorSoundProvider originalBlock) {
		return new AutoGenDimensionalDoorBlock.AutoGenDimensionalTrapdoorBlock(settings, originalBlock) {
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
			super(settings);
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
		public List<ItemStack> getDrops(BlockState state, LootContext.Builder params) {
			var defaultState = originalBlock.defaultBlockState();

			return originalBlock.getDrops(defaultState, params);
		}

		@Override
		public MutableComponent getName() {
			return Component.translatable("dimdoors.autogen_block_prefix").append(originalBlock.getName());
		}

		public Block getOriginalBlock() {
			return originalBlock;
		}

		@Nullable
		@Override
		public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
			return checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT.get(), (level, blockPos, blockState, blockEntity) -> blockEntity.tick(world, blockPos, blockState));
		}

		private static class AutoGenDimensionalTrapdoorBlock extends DimensionalTrapdoorBlock {
			private final Block originalBlock;

			public AutoGenDimensionalTrapdoorBlock(Properties settings, DoorSoundProvider originalBlock) {
				super(settings);
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

			@Nullable
			@Override
			public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
				return checkType(type, ModBlockEntityTypes.ENTRANCE_RIFT.get(), (level, blockPos, blockState, blockEntity) -> blockEntity.tick(world, blockPos, blockState));
			}
		}
	}
}
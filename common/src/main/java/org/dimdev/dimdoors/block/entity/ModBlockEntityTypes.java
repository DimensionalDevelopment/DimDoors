package org.dimdev.dimdoors.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.tesselating_loom.TesselatingLoomBlockEntity;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ModBlockEntityTypes {
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

	public static final RegistrySupplier<BlockEntityType<DetachedRiftBlockEntity>> DETACHED_RIFT = register(
			"detached_rift",
			DetachedRiftBlockEntity::new,
			ModBlocks.DETACHED_RIFT
	);

	public static final RegistrySupplier<MutableBlockEntityType<EntranceRiftBlockEntity>> ENTRANCE_RIFT = registerMutable(
			"entrance_rift",
			EntranceRiftBlockEntity::new,
			ModBlocks.DIMENSIONAL_PORTAL);

    public static final RegistrySupplier<BlockEntityType<TesselatingLoomBlockEntity>> TESSELATING_LOOM = register("tesselating_loom", TesselatingLoomBlockEntity::new, ModBlocks.TESSELATING_LOOM);


    private static <E extends BlockEntity> RegistrySupplier<BlockEntityType<E>> register(String id, BiFunction<BlockPos, BlockState, E> factory, RegistrySupplier<Block>... blocks) {
		return BLOCK_ENTITY_TYPES.register(id, () -> BlockEntityType.Builder.of(factory::apply, Stream.of(blocks).map(Supplier::get).toArray(Block[]::new)).build(null));
	}

	private static <E extends BlockEntity> RegistrySupplier<MutableBlockEntityType<E>> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, RegistrySupplier<Block>... blocks) {
		return BLOCK_ENTITY_TYPES.register(id, () -> MutableBlockEntityType.Builder.create(factory, Stream.of(blocks).map(Supplier::get).toArray(Block[]::new)).build());
	}

	public static void init() {
		BLOCK_ENTITY_TYPES.register();
	}
}

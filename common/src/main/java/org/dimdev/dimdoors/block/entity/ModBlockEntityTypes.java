package org.dimdev.dimdoors.block.entity;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.ModBlocks;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ModBlockEntityTypes {
	private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

	public static final RegistrySupplier<BlockEntityType<DetachedRiftBlockEntity>> DETACHED_RIFT = register(
			"dimdoors:detached_rift",
			DetachedRiftBlockEntity::new,
			ModBlocks.DETACHED_RIFT);

	public static final RegistrySupplier<MutableBlockEntityType<EntranceRiftBlockEntity>> ENTRANCE_RIFT = registerMutable(
			"dimdoors:entrance_rift",
			EntranceRiftBlockEntity::new,
			ModBlocks.DIMENSIONAL_PORTAL);

    public static final RegistrySupplier<BlockEntityType<TesselatingLoomBlockEntity>> TESSELATING_LOOM = register("dimdoors:tesselating_loom", TesselatingLoomBlockEntity::new, ModBlocks.TESSELATING_LOOM);


    private static <E extends BlockEntity> RegistrySupplier<BlockEntityType<E>> register(String id, BiFunction<BlockPos, BlockState, E> factory, Block... blocks) {
		return BLOCK_ENTITY_TYPES.register(id, of(factory, blocks));
	}

	public static <E extends BlockEntity> Supplier<BlockEntityType<E>> of(BiFunction<BlockPos, BlockState, E> blockEntityFunction, Block... blocks) {
		throw new RuntimeException();
	}

	private static <E extends BlockEntity> RegistrySupplier<MutableBlockEntityType<E>> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
		return BLOCK_ENTITY_TYPES.register(id, MutableBlockEntityType.Builder.create(factory, blocks)::build);
	}

	public static void init() {
		BLOCK_ENTITY_TYPES.register();
	}
}

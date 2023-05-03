package org.dimdev.dimdoors.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;
import org.dimdev.dimdoors.block.ModBlocks;

public class ModBlockEntityTypes {
	public static final BlockEntityType<DetachedRiftBlockEntity> DETACHED_RIFT = register(
			"dimdoors:detached_rift",
			DetachedRiftBlockEntity::new,
			ModBlocks.DETACHED_RIFT);

	public static final MutableBlockEntityType<EntranceRiftBlockEntity> ENTRANCE_RIFT = registerMutable(
			"dimdoors:entrance_rift",
			EntranceRiftBlockEntity::new,
			ModBlocks.DIMENSIONAL_PORTAL);

    public static final BlockEntityType<TesselatingLoomBlockEntity> TESSELATING_LOOM = register("dimdoors:tesselating_loom", TesselatingLoomBlockEntity::new, ModBlocks.TESSELATING_LOOM);


    private static <E extends BlockEntity> BlockEntityType<E> register(String id, FabricBlockEntityTypeBuilder.Factory<E> factory, Block... blocks) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, blocks).build());
	}

	private static <E extends BlockEntity> MutableBlockEntityType<E> registerMutable(String id, MutableBlockEntityType.BlockEntityFactory<E> factory, Block... blocks) {
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, MutableBlockEntityType.Builder.create(factory, blocks).build());
	}

	public static void init() {
		//just loads the class
	}
}

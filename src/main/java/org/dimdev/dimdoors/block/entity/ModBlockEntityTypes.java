package org.dimdev.dimdoors.block.entity;

import org.apache.commons.lang3.ArrayUtils;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.data.DoorData;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ModBlockEntityTypes {
	// needed for the autogen doors
	public static final Set<Block> ENTRANCE_RIFT_BLOCKS = new HashSet<>(Arrays.asList(ArrayUtils.add(DoorData.DOORS.toArray(new Block[0]), ModBlocks.DIMENSIONAL_PORTAL)));

	public static final BlockEntityType<DetachedRiftBlockEntity> DETACHED_RIFT = register(
			"dimdoors:detached_rift",
			DetachedRiftBlockEntity::new,
			ModBlocks.DETACHED_RIFT);

	public static final BlockEntityType<EntranceRiftBlockEntity> ENTRANCE_RIFT = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			"dimdoors:entrance_rift",
			new BlockEntityType<>(EntranceRiftBlockEntity::new, ENTRANCE_RIFT_BLOCKS, null));

	private static <E extends BlockEntity> BlockEntityType<E> register(String id, FabricBlockEntityTypeBuilder.Factory<E> factory, Block... blocks) {
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, blocks).build());
	}

	public static void init() {
		//just loads the class
	}
}

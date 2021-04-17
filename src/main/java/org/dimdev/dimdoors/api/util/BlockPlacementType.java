package org.dimdev.dimdoors.api.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum BlockPlacementType {
	// TODO: do we need some update fluids only option?
	SECTION_NO_UPDATE_QUEUE_BLOCK_ENTITY("section_no_update_queue_block_entity", true, false, BlockPlacementType::queueBlockEntity),
	SECTION_NO_UPDATE("section_no_update", true, false, World::addBlockEntity),
	SECTION_UPDATE("section_update", true, true, World::addBlockEntity),
	SET_BLOCK_STATE("set_block_state", false, false, World::addBlockEntity),
	SET_BLOCK_STATE_QUEUE_BLOCK_ENTITY("set_block_state_queue_block_entity", false, false, BlockPlacementType::queueBlockEntity);

	private final static Map<String, BlockPlacementType> idMap = new HashMap<>();

	static {
		for (BlockPlacementType type : BlockPlacementType.values()) {
			idMap.put(type.getId(), type);
		}
	}

	final String id;
	final boolean useSection;
	final boolean markForUpdate;
	final BiConsumer<World, BlockEntity> blockEntityPlacer;


	BlockPlacementType(String id, boolean useSection, boolean markForUpdate, BiConsumer<World, BlockEntity> blockEntityPlacer) {
		this.id = id;
		this.useSection = useSection;
		this.markForUpdate = markForUpdate;
		this.blockEntityPlacer = blockEntityPlacer;
	}

	public boolean useSection() {
		return useSection;
	}

	public boolean shouldMarkForUpdate() {
		return markForUpdate;
	}

	public BiConsumer<World, BlockEntity> getBlockEntityPlacer() {
		return blockEntityPlacer;
	}

	public String getId() {
		return id;
	}

	public static BlockPlacementType getFromId(String id) {
		return idMap.get(id);
	}

	private static void queueBlockEntity(World world, BlockEntity blockEntity) {
		MinecraftServer server = world.getServer();
		server.send(new ServerTask(server.getTicks(), () -> world.addBlockEntity(blockEntity)));
	}
}

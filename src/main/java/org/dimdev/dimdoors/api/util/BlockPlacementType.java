package org.dimdev.dimdoors.api.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public enum BlockPlacementType implements StringRepresentable {
	// TODO: do we need some update fluids only option?
	SECTION_NO_UPDATE_QUEUE_BLOCK_ENTITY("section_no_update_queue_block_entity", true, false, BlockPlacementType::queueBlockEntity),
	SECTION_NO_UPDATE("section_no_update", true, false, Level::setBlockEntity),
	SECTION_UPDATE("section_update", true, true, Level::setBlockEntity),
	SET_BLOCK_STATE("set_block_state", false, false, Level::setBlockEntity),
	SET_BLOCK_STATE_QUEUE_BLOCK_ENTITY("set_block_state_queue_block_entity", false, false, BlockPlacementType::queueBlockEntity);

	private final static Map<String, BlockPlacementType> idMap = new HashMap<>();

	public static final EnumCodec<BlockPlacementType> CODEC = StringRepresentable.fromEnum(BlockPlacementType::values);

	static {
		for (BlockPlacementType type : BlockPlacementType.values()) {
			idMap.put(type.getId(), type);
		}
	}

	final String id;
	final boolean useSection;
	final boolean markForUpdate;
	final BiConsumer<Level, BlockEntity> blockEntityPlacer;


	BlockPlacementType(String id, boolean useSection, boolean markForUpdate, BiConsumer<Level, BlockEntity> blockEntityPlacer) {
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

	public BiConsumer<Level, BlockEntity> getBlockEntityPlacer() {
		return blockEntityPlacer;
	}

	public String getId() {
		return id;
	}

	public static BlockPlacementType getFromId(String id) {
		return idMap.get(id);
	}

	private static void queueBlockEntity(Level world, BlockEntity blockEntity) {
		MinecraftServer server = world.getServer();
		server.tell(new TickTask(server.getTickCount(), () -> world.setBlockEntity(blockEntity)));
	}

	@Override
	public String getSerializedName() {
		return id;
	}
}

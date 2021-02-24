package org.dimdev.dimdoors.util;

import java.util.HashMap;
import java.util.Map;

public enum BlockPlacementType {
	// TODO: do we need some update fluids only option?
	SECTION_NO_UPDATE(true, false, "section_no_update"),
	SECTION_UPDATE(true, true, "section_update"),
	SET_BLOCK_STATE(false, false, "set_block_state");

	private final static Map<String, BlockPlacementType> idMap = new HashMap<>();

	static {
		for (BlockPlacementType type : BlockPlacementType.values()) {
			idMap.put(type.getId(), type);
		}
	}

	final boolean useSection;
	final boolean markForUpdate;
	final String id;

	BlockPlacementType(boolean useSection, boolean markForUpdate, String id) {
		this.useSection = useSection;
		this.markForUpdate = markForUpdate;
		this.id = id;
	}

	public boolean useSection() {
		return useSection;
	}

	public boolean shouldMarkForUpdate() {
		return markForUpdate;
	}

	public String getId() {
		return id;
	}

	public static BlockPlacementType getFromId(String id) {
		return idMap.get(id);
	}
}

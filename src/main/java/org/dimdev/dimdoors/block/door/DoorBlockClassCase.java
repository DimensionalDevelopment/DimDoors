package org.dimdev.dimdoors.block.door;

import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.TrapdoorBlock;

import java.util.HashMap;
import java.util.Map;

// Just in case we want to support more Door classes in the future.
// Need to move away from an enum for better mod support though.
public enum DoorBlockClassCase {
	NONE(null),
	DOOR_BLOCK(DoorBlock.class),
	TRAPDOOR_BLOCK(TrapdoorBlock.class);

	private static final Map<Class<? extends Block>, DoorBlockClassCase> CASE_MAP = new HashMap<>();

	static {
		for (DoorBlockClassCase doorBlockClassCase : DoorBlockClassCase.values()) {
			CASE_MAP.put(doorBlockClassCase.doorClazz, doorBlockClassCase);
		}
	}

	public static DoorBlockClassCase getCase(Block block) {
		DoorBlockClassCase doorBlockClassCase = CASE_MAP.get(block.getClass());
		return doorBlockClassCase == null ? NONE : doorBlockClassCase;
	}

	private final Class<? extends Block> doorClazz;

	DoorBlockClassCase(Class<? extends Block> doorClazz) {
		this.doorClazz = doorClazz;
	}
}

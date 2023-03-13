package org.dimdev.dimdoors.sound;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModSoundEvents {
	public static final SoundEvent CRACK = register("crack");
	public static final SoundEvent CREEPY = register("creepy");
	public static final SoundEvent DOOR_LOCKED = register("door_locked");
	public static final SoundEvent DOOR_LOCK_REMOVED = register("door_lock_removed");
	public static final SoundEvent KEY_LOCK = register("key_lock");
	public static final SoundEvent KEY_UNLOCKED = register("key_unlock");
	public static final SoundEvent MONK = register("monk");
	public static final SoundEvent RIFT = register("rift");
	public static final SoundEvent RIFT_CLOSE = register("rift_close");
	public static final SoundEvent RIFT_DOOR = register("rift_door");
	public static final SoundEvent RIFT_END = register("rift_end");
	public static final SoundEvent RIFT_START = register("rift_start");
	public static final SoundEvent TEARING = register("tearing");
	public static final SoundEvent WHITE_VOID = register("white_void");
	public static final SoundEvent BLOOP = register("bloop");
	public static final SoundEvent TESSELATING_WEAVE = register("tesselating_weave");

	private static SoundEvent register(String id) {
		ResourceLocation identifier = DimensionalDoors.resource(id);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
	}

	public static void init() {
		//just loads the class
	}
}

package org.dimdev.dimdoors.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModSoundEvents {
	public static final SoundEvent CRACK = register("dimdoors:crack");
	public static final SoundEvent CREEPY = register("dimdoors:creepy");
	public static final SoundEvent DOOR_LOCKED = register("dimdoors:door_locked");
	public static final SoundEvent DOOR_LOCK_REMOVED = register("dimdoors:door_lock_removed");
	public static final SoundEvent KEY_LOCK = register("dimdoors:key_lock");
	public static final SoundEvent KEY_UNLOCKED = register("dimdoors:key_unlock");
	public static final SoundEvent MONK = register("dimdoors:monk");
	public static final SoundEvent RIFT = register("dimdoors:rift");
	public static final SoundEvent RIFT_CLOSE = register("dimdoors:rift_close");
	public static final SoundEvent RIFT_DOOR = register("dimdoors:rift_door");
	public static final SoundEvent RIFT_END = register("dimdoors:rift_end");
	public static final SoundEvent RIFT_START = register("dimdoors:rift_start");
	public static final SoundEvent TEARING = register("dimdoors:tearing");
	public static final SoundEvent WHITE_VOID = register("dimdoors:white_void");
	public static final SoundEvent BLOOP = register("dimdoors:bloop");

	private static SoundEvent register(String id) {
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(new Identifier(id)));
	}

	public static void init() {
		//just loads the class
	}
}

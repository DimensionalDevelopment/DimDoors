package org.dimdev.dimdoors.sound;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModSoundEvents {
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.SOUND_EVENT);
	public static final RegistrySupplier<SoundEvent> CRACK = register("crack");
	public static final RegistrySupplier<SoundEvent> CREEPY = register("creepy");
	public static final RegistrySupplier<SoundEvent> DOOR_LOCKED = register("door_locked");
	public static final RegistrySupplier<SoundEvent> DOOR_LOCK_REMOVED = register("door_lock_removed");
	public static final RegistrySupplier<SoundEvent> KEY_LOCK = register("key_lock");
	public static final RegistrySupplier<SoundEvent> KEY_UNLOCKED = register("key_unlock");
	public static final RegistrySupplier<SoundEvent> MONK = register("monk");
	public static final RegistrySupplier<SoundEvent> RIFT = register("rift");
	public static final RegistrySupplier<SoundEvent> RIFT_CLOSE = register("rift_close");
	public static final RegistrySupplier<SoundEvent> RIFT_DOOR = register("rift_door");
	public static final RegistrySupplier<SoundEvent> RIFT_END = register("rift_end");
	public static final RegistrySupplier<SoundEvent> RIFT_START = register("rift_start");
	public static final RegistrySupplier<SoundEvent> TEARING = register("tearing");
	public static final RegistrySupplier<SoundEvent> WHITE_VOID = register("white_void");
	public static final RegistrySupplier<SoundEvent> BLOOP = register("bloop");
	public static final RegistrySupplier<SoundEvent> TESSELATING_WEAVE = register("tesselating_weave");

	private static RegistrySupplier<SoundEvent> register(String id) {
		return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(DimensionalDoors.id(id)));
	}

	public static void init() {
		SOUND_EVENTS.register();
		//just loads the class
	}
}

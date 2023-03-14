package org.dimdev.dimdoors.sound;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import org.dimdev.dimdoors.Constants;
import org.dimdev.dimdoors.DimensionalDoors;

public final class ModSoundEvents {
	private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Constants.MODID);

	public static final RegistryObject<SoundEvent> CRACK = SOUND_EVENTS.register("crack", () -> register("crack"));
	public static final RegistryObject<SoundEvent> CREEPY = SOUND_EVENTS.register("creepy", () -> register("creepy"));
	public static final RegistryObject<SoundEvent> DOOR_LOCKED = SOUND_EVENTS.register("door_locked", () -> register("door_locked"));
	public static final RegistryObject<SoundEvent> DOOR_LOCK_REMOVED = SOUND_EVENTS.register("door_lock_removed", () -> register("door_lock_removed"));
	public static final RegistryObject<SoundEvent> KEY_LOCK = SOUND_EVENTS.register("key_lock", () -> register("key_lock"));
	public static final RegistryObject<SoundEvent> KEY_UNLOCKED = SOUND_EVENTS.register("key_unlock", () -> register("key_unlock"));
	public static final RegistryObject<SoundEvent> MONK = SOUND_EVENTS.register("monk", () -> register("monk"));
	public static final RegistryObject<SoundEvent> RIFT = SOUND_EVENTS.register("rift", () -> register("rift"));
	public static final RegistryObject<SoundEvent> RIFT_CLOSE = SOUND_EVENTS.register("rift_close", () -> register("rift_close"));
	public static final RegistryObject<SoundEvent> RIFT_DOOR = SOUND_EVENTS.register("rift_door", () -> register("rift_door"));
	public static final RegistryObject<SoundEvent> RIFT_END = SOUND_EVENTS.register("rift_end", () -> register("rift_end"));
	public static final RegistryObject<SoundEvent> RIFT_START = SOUND_EVENTS.register("rift_start", () -> register("rift_start"));
	public static final RegistryObject<SoundEvent> TEARING = SOUND_EVENTS.register("tearing", () -> register("tearing"));
	public static final RegistryObject<SoundEvent> WHITE_VOID = SOUND_EVENTS.register("white_void", () -> register("white_void"));
	public static final RegistryObject<SoundEvent> BLOOP = SOUND_EVENTS.register("bloop", () -> register("bloop"));
	public static final RegistryObject<SoundEvent> TESSELATING_WEAVE = SOUND_EVENTS.register("tesselating_weave", () -> register("tesselating_weave"));

	private static SoundEvent register(String id) {
		ResourceLocation identifier = DimensionalDoors.resource(id);
		return SoundEvent.createVariableRangeEvent(identifier);
	}

	public static void init(IEventBus eventBus) {
		SOUND_EVENTS.register(eventBus);
	}
}

package org.dimdev.dimdoors.shared.sound;

import org.dimdev.dimdoors.DimDoors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ModSounds {

    // Don't forget to change sounds.json too if you're changing an ID!
    public static final SoundEvent CRACK = create("crack");
    public static final SoundEvent CREEPY = create("creepy");
    public static final SoundEvent DOOR_LOCKED = create("door_locked");
    public static final SoundEvent DOOR_LOCK_REMOVED = create("door_lock_removed");
    public static final SoundEvent KEY_LOCK = create("key_lock");
    public static final SoundEvent KEY_UNLOCKED = create("key_unlock");
    public static final SoundEvent MONK = create("monk");
    public static final SoundEvent RIFT = create("rift");
    public static final SoundEvent RIFT_CLOSE = create("rift_close");
    public static final SoundEvent RIFT_DOOR = create("rift_door");
    public static final SoundEvent RIFT_END = create("rift_end");
    public static final SoundEvent RIFT_START = create("rift_start");
    public static final SoundEvent TEARING = create("tearing");

    private static SoundEvent create(String name) {
        ResourceLocation id = new ResourceLocation(DimDoors.MODID, name);
        return new SoundEvent(id).setRegistryName(name);
    }

    @SubscribeEvent
    public static void registerSoundEvents(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                CRACK,
                CREEPY,
                DOOR_LOCKED,
                DOOR_LOCK_REMOVED,
                KEY_LOCK,
                KEY_UNLOCKED,
                MONK,
                RIFT,
                RIFT_CLOSE,
                RIFT_DOOR,
                RIFT_END,
                RIFT_START,
                TEARING);
    }
}

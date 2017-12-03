package com.zixiken.dimdoors.server.sound;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DDSounds {
    private  DDSounds() {}

    public static SoundEvent CRACK = create("crack");

    public static SoundEvent CREEPY = create("creepy");

    public static SoundEvent DOOR_LOCKED = create("door_locked");

    public static SoundEvent DOOR_LOCK_REMOVED = create("door_lock_removed");

    public static SoundEvent KEY_LOCK = create("key");

    public static SoundEvent KEY_UNLOCKED = create("key_unlock");

    public static SoundEvent MONK = create("monk");

    public static SoundEvent RIFT = create("rift");

    public static SoundEvent RIFT_CLOSE = create("rift_close");

    public static SoundEvent RIFT_DOOR = create("rift_door");

    public static SoundEvent RIFT_END = create("rift_end");

    public static SoundEvent RIFT_START = create("rift_start");

    public static SoundEvent TEARING = create("tearing");

    private static SoundEvent create(String name) {
        ResourceLocation id = new ResourceLocation(DimDoors.MODID, name);
        SoundEvent sound = new SoundEvent(id).setRegistryName(name);
        // GameRegistry.register(sound);
        return sound;
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

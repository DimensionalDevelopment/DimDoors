package com.zixiken.dimdoors.server.sound;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DDSounds {
    private  DDSounds() {}

    public static final SoundEvent CRACK = create("crack");

    public static final SoundEvent CREEPY = create("creepy");

    public static final SoundEvent DOOR_LOCKED = create("doorLocked");

    public static final SoundEvent DOOR_LOCK_REMOVED = create("doorLockRemoved");

    public static final SoundEvent KEY_LOCK = create("key");

    public static final SoundEvent KEY_UNLOCKED = create("keyUnlock");

    public static final SoundEvent MONK = create("monk");

    public static final SoundEvent RIFT = create("rift");

    public static final SoundEvent RIFT_CLOSE = create("riftClose");

    public static final SoundEvent RIFT_DOOR = create("riftDoor");

    public static final SoundEvent RIFT_END = create("riftEnd");

    public static final SoundEvent RIFT_START = create("riftStart");

    public static final SoundEvent TEARING = create("tearing");

    private static final SoundEvent create(String name) {
        ResourceLocation id = new ResourceLocation(DimDoors.MODID, name);
        SoundEvent sound = new SoundEvent(id).setRegistryName(name);
        GameRegistry.register(sound);
        return sound;
    }
}

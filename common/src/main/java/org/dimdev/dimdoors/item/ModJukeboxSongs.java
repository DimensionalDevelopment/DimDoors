package org.dimdev.dimdoors.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class ModJukeboxSongs {
    public static final ResourceKey<JukeboxSong> CREEPY = DimensionalDoors.key(Registries.JUKEBOX_SONG, "creepy");
    public static final ResourceKey<JukeboxSong> WHITE_VOID = DimensionalDoors.key(Registries.JUKEBOX_SONG, "white_void");

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register(context, CREEPY, ModSoundEvents.CREEPY, 317);
        register(context, WHITE_VOID, ModSoundEvents.WHITE_VOID, 225);
    }

    private static void register(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, RegistrySupplier<SoundEvent> sound, int length) {

        context.register(key, new JukeboxSong(context.lookup(Registries.SOUND_EVENT).getOrThrow(sound.getKey()), Component.translatable(Util.makeDescriptionId("jukebox_song", key.location())), length, 10));
    }
}

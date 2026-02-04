package org.dimdev.dimdoors.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.sound.ModSoundEvents;

public class ModJukeboxSongs {
    public static final ResourceKey<JukeboxSong> CREEPY = ResourceKey.create(Registries.JUKEBOX_SONG, DimensionalDoors.id("creepy"));
    public static final ResourceKey<JukeboxSong> WHITE_VOID = ResourceKey.create(Registries.JUKEBOX_SONG, DimensionalDoors.id("white_void"));;
    public static final ResourceKey<JukeboxSong> THEY_STARE_BACK = ResourceKey.create(Registries.JUKEBOX_SONG, DimensionalDoors.id("they_stare_back"));

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        context.register(CREEPY, new JukeboxSong(ModSoundEvents.CREEPY, Component.translatable("item.dimdoors.creepy_record.desc"), 317, 10));
        context.register(WHITE_VOID, new JukeboxSong(ModSoundEvents.WHITE_VOID, Component.translatable("item.dimdoors.white_void_record.desc"), 225, 10));
        context.register(THEY_STARE_BACK, new JukeboxSong(ModSoundEvents.THEY_STARE_BACK, Component.translatable("item.dimdoors.they_stare_back_record.desc"), 226, 10));
    }
}

package org.dimdev.dimdoors.item;

import net.minecraft.core.registries.BuiltInRegistries;
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
}

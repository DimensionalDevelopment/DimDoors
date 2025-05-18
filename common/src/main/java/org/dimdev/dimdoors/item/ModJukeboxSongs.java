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

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        context.register(CREEPY, new JukeboxSong(ModSoundEvents.CREEPY, Component.empty(), 317, 10));
        context.register(WHITE_VOID, new JukeboxSong(ModSoundEvents.WHITE_VOID, Component.empty(), 225, 10));
    }
}

package org.dimdev.dimdoors.util.neoforge;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Map;

public class ServerUtilsImpl {
    public static void fireUnloadEvent(ServerLevel level) {
        NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
    }

    public static void markLevelsDirty(MinecraftServer server) {
        server.markWorldsDirty();
    }

    public static void fireLoadEvent(ServerLevel level) {
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
    }
}

package org.dimdev.dimdoors.util.fabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class ServerUtilsImpl {

    public static void fireUnloadEvent(ServerLevel level) {
        ServerWorldEvents.UNLOAD.invoker().onWorldUnload(level.getServer(), level);
    }

    public static void markLevelsDirty(MinecraftServer server) {
    }

    public static void fireLoadEvent(ServerLevel level) {
        ServerWorldEvents.LOAD.invoker().onWorldLoad(level.getServer(), level);
    }
}

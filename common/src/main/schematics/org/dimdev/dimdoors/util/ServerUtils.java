package org.dimdev.dimdoors.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ServerUtils {

    @ExpectPlatform
    public static void fireUnloadEvent(ServerLevel level) {
    }

    @ExpectPlatform
    public static void markLevelsDirty(MinecraftServer server) {

    }

    @ExpectPlatform
    public static void fireLoadEvent(ServerLevel newLevel) {

    }
}

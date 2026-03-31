package org.dimdev.dimdoors.mixin;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.dimdev.dimdoors.util.ServerExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements ServerExtensions {
    @Shadow
    @Final
    private Map<ResourceKey<net.minecraft.world.level.Level>, ServerLevel> levels;

    @Shadow
    @Final
    private LayeredRegistryAccess<RegistryLayer> registries;

    @Shadow
    @Final
    private ChunkProgressListenerFactory progressListenerFactory;

    @Shadow
    @Final
    private Executor executor;

    @Shadow
    @Final
    protected LevelStorageSource.LevelStorageAccess storageSource;

    @Override
    public Map<ResourceKey<Level>, ServerLevel> worldMap() {
        return levels;
    }

    @Override
    public LayeredRegistryAccess<RegistryLayer> getLayeredRegistryAccess() {
        return registries;
    }

    public ChunkProgressListenerFactory getProgressListenerFactory() {
        return progressListenerFactory;
    }

    public Executor getExecutor() {
        return executor;
    }

    public LevelStorageSource.LevelStorageAccess getLevelStorageAcess() {
        return storageSource;
    }
}

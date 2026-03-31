package org.dimdev.dimdoors.util;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.dimdev.dimdoors.api.block.entity.MutableBlockEntityType;

import java.util.Map;
import java.util.concurrent.Executor;

public interface ServerExtensions {
    Map<ResourceKey<Level>, ServerLevel> worldMap();

    LayeredRegistryAccess<RegistryLayer> getLayeredRegistryAccess();

    ChunkProgressListenerFactory getProgressListenerFactory();

    Executor getExecutor();

    LevelStorageSource.LevelStorageAccess getLevelStorageAcess();
}

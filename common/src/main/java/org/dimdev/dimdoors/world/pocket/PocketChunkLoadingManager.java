package org.dimdev.dimdoors.world.pocket;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.ChunkBounds;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.addon.ForceLoadedPocketAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.HashSet;
import java.util.Set;

public final class PocketChunkLoadingManager {
    private PocketChunkLoadingManager() {
    }

    public static void reconcileAll(MinecraftServer server) {
        DimensionalRegistry.forEachPocketDirectory((worldKey, directory) -> {
            ServerLevel level = server.getLevel(worldKey);
            if (level == null) return;

            for (Pocket<?, ?> pocket : getCanonicalPockets(directory)) {
                if (isForceLoaded(pocket)) {
                    setForced(level, pocket, true);
                }
            }
        });
    }

    public static boolean isForceLoaded(Pocket<?, ?> pocket) {
        return pocket.hasAddon(PocketAddon.PocketAddonType.FORCE_LOADED_ADDON);
    }

    public static int setForceLoaded(Pocket<?, ?> pocket, boolean forceLoaded) {
        boolean wasForceLoaded = isForceLoaded(pocket);
        if (forceLoaded && !wasForceLoaded) {
            pocket.addAddon(ForceLoadedPocketAddon.instance());
            DimensionalRegistry.setDirty();
        } else if (!forceLoaded && wasForceLoaded) {
            pocket.removeAddon(PocketAddon.PocketAddonType.FORCE_LOADED_ADDON);
            DimensionalRegistry.setDirty();
        } else if (!forceLoaded) {
            return 0;
        }

        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return 0;

        return setForced(level, pocket, forceLoaded);
    }

    public static int applyIfForceLoaded(Pocket<?, ?> pocket) {
        if (!isForceLoaded(pocket)) return 0;

        ServerLevel level = DimensionalDoors.getWorld(pocket.getWorld());
        if (level == null) return 0;

        return setForced(level, pocket, true);
    }

    public static int chunkCount(Pocket<?, ?> pocket) {
        ChunkBounds bounds = ChunkBounds.of(pocket);
        return bounds.width() * bounds.length();
    }

    private static int setForced(ServerLevel level, Pocket<?, ?> pocket, boolean forceLoaded) {
        ChunkBounds bounds = ChunkBounds.of(pocket);
        int count = 0;

        for (int cx = bounds.minX(); cx <= bounds.maxX(); cx++) {
            for (int cz = bounds.minZ(); cz <= bounds.maxZ(); cz++) {
                if (!forceLoaded && isChunkForceLoadedByOtherPocket(pocket.getWorld(), pocket.getId(), cx, cz)) {
                    continue;
                }

                level.setChunkForced(cx, cz, forceLoaded);
                count++;
            }
        }

        return count;
    }

    private static boolean isChunkForceLoadedByOtherPocket(ResourceKey<Level> world, int excludedPocketId, int chunkX, int chunkZ) {
        PocketDirectory directory = DimensionalRegistry.peekPocketDirectory(world);
        if (directory == null) return false;

        for (Pocket<?, ?> pocket : getCanonicalPockets(directory)) {
            if (pocket.getId() == excludedPocketId || !isForceLoaded(pocket)) {
                continue;
            }

            if (ChunkBounds.of(pocket).contains(chunkX, chunkZ)) {
                return true;
            }
        }

        return false;
    }

    private static Set<Pocket<?, ?>> getCanonicalPockets(PocketDirectory directory) {
        Set<Integer> seenIds = new HashSet<>();
        Set<Pocket<?, ?>> pockets = new HashSet<>();

        for (AbstractPocket<?, ?> entry : directory.getPockets().values()) {
            Pocket<?, ?> pocket = entry.getReferencedPocket(directory);
            if (pocket != null && seenIds.add(pocket.getId())) {
                pockets.add(pocket);
            }
        }

        return pockets;
    }

}

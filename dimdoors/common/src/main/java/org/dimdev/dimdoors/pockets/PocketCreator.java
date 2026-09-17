package org.dimdev.dimdoors.pockets;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.targets.LocationProvider;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.world.pocket.type.Pocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface PocketCreator {
    Pocket prepareAndPlacePocket(PocketGenerationContext parameters);

    Pocket prepareAndPlacePocket(PocketGenerationContext parameters, Boolean setupLoot);

    static Pocket<?, ?> create(PocketCreator creator, PocketGenerationContext parameters) {
        GenerationKey key = GenerationKey.from(parameters);
        if (!Guard.ACTIVE_GENERATIONS.add(key)) {
            Guard.LOGGER.warn("Skipping re-entrant pocket generation for {}.", key);
            return null;
        }

        try {
            return creator.prepareAndPlacePocket(parameters);
        } finally {
            Guard.ACTIVE_GENERATIONS.remove(key);
        }
    }

    class Guard {
        private static final Logger LOGGER = LogManager.getLogger();
        private static final Set<GenerationKey> ACTIVE_GENERATIONS = ConcurrentHashMap.newKeySet();
    }

    record GenerationKey(String type, ResourceKey<Level> world, BlockPos pos) {
        static GenerationKey from(PocketGenerationContext parameters) {
            if (parameters.linkTo() instanceof LocationProvider provider) {
                Location location = provider.getLocation();
                if (location != null) {
                    return new GenerationKey("source", location.getWorldId(), location.getBlockPos());
                }
            }

            VirtualLocation virtualLocation = parameters.sourceVirtualLocation();
            return new GenerationKey(
                    "virtual",
                    virtualLocation.getWorld(),
                    new BlockPos(virtualLocation.getX(), virtualLocation.getDepth(), virtualLocation.getZ())
            );
        }
    }
}

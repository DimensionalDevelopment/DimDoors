package org.dimdev.dimdoors.compat.jei.decay;

import net.minecraft.server.MinecraftServer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.compat.decay.DecayDisplayData;
import org.dimdev.dimdoors.world.decay.Decay;

import java.util.Collection;
import java.util.List;

public final class DecayRecipes {
    private DecayRecipes() {
    }

    public static List<DecayDisplayData> getDecays() {
        MinecraftServer server = DimensionalDoors.getServer();
        if (server == null) {
            return List.of();
        }

        return Decay.DecayLoader.getPatterns().values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .flatMap(pattern -> DecayDisplayData.list(pattern, server.registryAccess()))
                .filter(DecayJeiUtil::supports)
                .toList();
    }
}

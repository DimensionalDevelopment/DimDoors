package org.dimdev.dimdoors.pockets;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.Map;
import java.util.Optional;

public record PocketGenerationContext(ServerLevel world, VirtualLocation sourceVirtualLocation, VirtualTarget linkTo, LinkProperties linkProperties,
                      net.minecraft.core.HolderLookup.Provider provider) {
    public Map<String, Double> toVariableMap(Map<String, Double> stringDoubleMap) {
        stringDoubleMap.put("depth", (double) this.sourceVirtualLocation.getDepth());
        stringDoubleMap.put("public_size", (double) DimensionalDoors.getConfig().getPocketsConfig().publicPocketSize);
        stringDoubleMap.put("private_size", (double) DimensionalDoors.getConfig().getPocketsConfig().privatePocketSize);
        return stringDoubleMap;
    }


    public <T> Holder<T> lookupHolder(ResourceKey<T> id) {
        return provider.asGetterLookup().lookupOrThrow(id.registryKey()).getOrThrow(id);
    }

    public <T> Optional<Holder.Reference<T>> lookupHolderOptional(ResourceKey<T> id) {
        return provider.asGetterLookup().lookupOrThrow(id.registryKey()).get(id);
    }
}

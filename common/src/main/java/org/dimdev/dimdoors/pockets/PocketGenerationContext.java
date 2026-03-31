package org.dimdev.dimdoors.pockets;

import com.bedrockk.molang.runtime.value.DoubleValue;
import com.bedrockk.molang.runtime.value.MoValue;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.pockets.generator.PocketGenerator;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import java.util.Map;

public record PocketGenerationContext(VirtualLocation sourceVirtualLocation, VirtualTarget linkTo, LinkProperties linkProperties,
                                      net.minecraft.core.HolderLookup.Provider provider) {
	public Map<String, MoValue> toVariableMap(Map<String, MoValue> map) {
        map.put("depth", new DoubleValue(this.sourceVirtualLocation.depth()));
        map.put("public_size", new DoubleValue(DimensionalDoors.getConfig().getPocketsConfig().publicPocketSize));
		map.put("private_size", new DoubleValue(DimensionalDoors.getConfig().getPocketsConfig().privatePocketSize));
		return map;
	}

    public <T> T lookup(ResourceKey<T> key) {
        return provider.lookupOrThrow(key.registryKey()).getOrThrow(key).value();
    }

    public <T> Holder<T> lookupHolder(ResourceKey<T> key) {
        return provider.lookupOrThrow(key.registryKey()).getOrThrow(key);
    }
}
package org.dimdev.dimdoors.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public interface ImmutableRegistryAccessExtensions {
    void setRegistries(Map<ResourceKey<? extends Registry<?>>, Registry<?>> newRegistryMap);
}

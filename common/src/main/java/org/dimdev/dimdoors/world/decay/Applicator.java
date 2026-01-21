package org.dimdev.dimdoors.world.decay;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Set;
import java.util.stream.Stream;

public interface Applicator<T> {
    Stream<ResourceKey<T>> constructApplicable(RegistryAccess lookup);

    ResourceKey<? extends Registry<T>> registry();
}

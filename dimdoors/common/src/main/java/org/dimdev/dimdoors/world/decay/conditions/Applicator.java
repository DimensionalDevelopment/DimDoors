package org.dimdev.dimdoors.world.decay.conditions;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.stream.Stream;

public interface Applicator<T> {
    Stream<ResourceKey<T>> constructApplicable(RegistryAccess lookup);

    ResourceKey<? extends Registry<T>> registry();
}

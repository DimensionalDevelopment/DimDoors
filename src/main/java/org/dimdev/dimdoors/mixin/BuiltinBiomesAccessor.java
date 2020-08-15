package org.dimdev.dimdoors.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;

@Mixin(BuiltinBiomes.class)
public interface BuiltinBiomesAccessor {
    @Invoker
    static Biome invokeRegister(int rawId, RegistryKey<Biome> registryKey, Biome biome) {
        throw new AssertionError();
    }
}

package org.dimdev.dimdoors.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.util.ImmutableRegistryAccessExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RegistryAccess.ImmutableRegistryAccess.class)
public class ImmutableRegistryAccessMixin implements ImmutableRegistryAccessExtensions {
    @Mutable
    @Shadow
    @Final
    private Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

    @Override
    public void setRegistries(Map<ResourceKey<? extends Registry<?>>, Registry<?>> newRegistryMap) {
        this.registries = registries;
    }
}

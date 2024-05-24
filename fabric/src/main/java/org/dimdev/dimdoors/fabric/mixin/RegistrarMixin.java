package org.dimdev.dimdoors.fabric.mixin;

import dev.architectury.registry.registries.fabric.RegistrarManagerImpl;
import net.minecraft.core.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistrarManagerImpl.RegistrarImpl.class)
public interface RegistrarMixin extends RegistrarUtil.IRegistrar {
    @Accessor("delegate")
    public static Registry<?> getTagFields() {
        throw new AssertionError("Untransformed @Accessor");
    }
}

package org.dimdev.dimdoors.mixin;

import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import org.dimdev.dimdoors.util.LayeredRegistryAccessExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(LayeredRegistryAccess.class)
public abstract class LayeredRegistryAccessMixin<T> implements LayeredRegistryAccessExtensions<T> {
    @Shadow
    @Final
    private RegistryAccess.Frozen composite;

    @Mutable
    @Shadow
    @Final
    private List<RegistryAccess.Frozen> values;

    @Override
    public RegistryAccess.Frozen getComposite() {
        return composite;
    }

    @Override
    public void setValues(List<RegistryAccess.Frozen> values) {
        this.values = values;
    }
}

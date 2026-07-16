package org.dimdev.limlib.mixin.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.dimdev.limlib.client.IDimensionSpecialEffectExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin implements IDimensionSpecialEffectExtension {
}

package org.dimdev.dimcore.mixin.client;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.dimdev.dimcore.client.IDimensionSpecialEffectExtension;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin implements IDimensionSpecialEffectExtension {
}

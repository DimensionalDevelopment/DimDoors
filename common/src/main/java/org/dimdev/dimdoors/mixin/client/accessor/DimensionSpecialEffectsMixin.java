package org.dimdev.dimdoors.mixin.client.accessor;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin {

    @Accessor("EFFECTS")
    public static Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> getEffects() {
        throw new RuntimeException("You shouldn't be here.");
    }
}

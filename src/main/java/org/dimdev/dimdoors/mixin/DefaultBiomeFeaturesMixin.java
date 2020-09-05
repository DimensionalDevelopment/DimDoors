package org.dimdev.dimdoors.mixin;

import org.dimdev.dimdoors.world.feature.ModFeatures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

@Mixin(DefaultBiomeFeatures.class)
public class DefaultBiomeFeaturesMixin {
    @Inject(method = "addDesertLakes", at = @At("RETURN"), remap = false)
    private static void addGateway(GenerationSettings.Builder builder, CallbackInfo ci) {
        builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ModFeatures.SANDSTONE_PILLARS_FEATURE);
    }
}

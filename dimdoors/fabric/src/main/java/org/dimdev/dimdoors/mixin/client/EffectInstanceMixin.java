package org.dimdev.dimdoors.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"), allow = 1)
    private ResourceLocation dimdoors$namespacedEffect(String path, Operation<ResourceLocation> original) {
        return correcctLocation(path, original);
    }

    @WrapOperation(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"), allow = 1)
    private static ResourceLocation dimdoors$namespacedProgram(String path, Operation<ResourceLocation> original) {
        return correcctLocation(path, original);
    }

    private static ResourceLocation correcctLocation(String path, Operation<ResourceLocation> original) {
        int colon = path.indexOf(':');
        if (colon < 0) return original.call(path);
        int slash = path.lastIndexOf('/', colon) + 1;
        return ResourceLocation.fromNamespaceAndPath(path.substring(slash, colon), path.substring(0, slash) + path.substring(colon + 1));
    }
}

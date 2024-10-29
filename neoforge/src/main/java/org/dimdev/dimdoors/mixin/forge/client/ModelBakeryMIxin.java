package org.dimdev.dimdoors.mixin.forge.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.door.DimensionalDoorItem;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public class ModelBakeryMIxin {

    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
    private void onLoadModel(ResourceLocation id, CallbackInfo ci) {
        if(id instanceof ModelResourceLocation modelId) {
            if(modelId.getPath().contains(DimensionalDoorBlockRegistrar.PREFIX) || modelId.getPath().contains(DimensionalDoorItemRegistrar.PREFIX)) {
                ci.cancel();
            }
}
    }
}

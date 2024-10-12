package org.dimdev.dimdoors.mixin.forge.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public class ModelBakeryMIxin {

    //TODO: See if this works.

    @Inject(method = "registerModel", at = @At("HEAD"), cancellable = true)
    private void onLoadModel(ModelResourceLocation modelId, UnbakedModel model, CallbackInfo ci) {
        if(modelId.id().getPath().contains(DimensionalDoorBlockRegistrar.PREFIX) || modelId.id().getPath().contains(DimensionalDoorItemRegistrar.PREFIX)) {
            ci.cancel();
        }
    }


//    Keep for reference for now.
//    @Inject(method = "loadModel", at = @At("HEAD"), cancellable = true)
//    private void onLoadModel(ResourceLocation id, CallbackInfo ci) {
//        if(id instanceof ModelResourceLocation modelId) {
//            if(modelId.getPath().contains(DimensionalDoorBlockRegistrar.PREFIX) || modelId.getPath().contains(DimensionalDoorItemRegistrar.PREFIX)) {
//                ci.cancel();
//            }
//}
//    }
}

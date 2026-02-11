package org.dimdev.dimdoors.mixin.neoforge.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBakery.class)
public class ModelBakeryMIxin {

    @WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private void silenceModelNotFound(Logger instance, String s, Object[] objects, Operation<Void> original) {
        var resource = (ResourceLocation) objects[0];

        if(!(resource.getNamespace().equals("dimdoors") && resource.getPath().startsWith("item/item_ag_dim_"))) {
            original.call(instance, s, objects);
        }
    }
}

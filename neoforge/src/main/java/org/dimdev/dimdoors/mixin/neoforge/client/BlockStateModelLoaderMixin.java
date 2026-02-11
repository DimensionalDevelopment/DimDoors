package org.dimdev.dimdoors.mixin.neoforge.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {
    @WrapOperation(method = "method_61064", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;[Ljava/lang/Object;)V"))
    private static void silenceModelNotFound(Logger instance, String s, Object[] objects, Operation<Void> original) {
        var location = (ResourceLocation) objects[0];

        if (!(location.getNamespace().equals("dimdoors") && location.getPath().startsWith("blockstates/block_ag_dim_"))) {
            original.call(instance, s, objects);
        }
    }

    @WrapOperation(method = "method_61066", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", ordinal = 0))
    private static void silenceModelNotFound(Logger instance, String s, Object object, Object object1, Operation<Void> original) {
        var location = (ResourceLocation) object;
        if (!(location.getNamespace().equals("dimdoors") && location.getPath().startsWith("blockstates/block_ag_dim_"))) {
            original.call(instance, s, object, object1);
        }
    }
}

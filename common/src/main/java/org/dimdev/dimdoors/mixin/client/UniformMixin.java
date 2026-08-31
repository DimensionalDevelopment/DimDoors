package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import org.dimdev.dimdoors.api.client.UniformExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Uniform.class)
abstract public class UniformMixin implements UniformExt {
    @Shadow
    @Final
    private int type;

    @Shadow
    private int location;

    @Shadow
    @Final
    private FloatBuffer floatValues;

    @Inject(method = "getTypeFromString", at = @At("RETURN"), cancellable = true)
    private static void addVec(String typeName, CallbackInfoReturnable<Integer> cir) {
        var i = cir.getReturnValue();

        if(i == -1) {
            if(typeName.startsWith("vec")) {
                if(typeName.endsWith("2")) i = 11;
                if(typeName.endsWith("3")) i = 12;
                if(typeName.endsWith("4")) i = 13;
            }

            if(i != -1) cir.setReturnValue(i);
        }
    }

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"), cancellable = true)
    public void uploadVec(CallbackInfo ci) {
        if(this.type < UT_VEC2 || this.type > UT_VEC4) return;

        this.floatValues.rewind();

        switch (this.type) {
            case UT_VEC2 -> RenderSystem.glUniform2(this.location, this.floatValues);
            case UT_VEC3 -> RenderSystem.glUniform3(this.location, this.floatValues);
            case UT_VEC4 -> RenderSystem.glUniform4(this.location, this.floatValues);
        }

        ci.cancel();
    }
}

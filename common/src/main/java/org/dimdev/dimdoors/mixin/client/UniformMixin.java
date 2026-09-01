package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import foundry.imgui.impl.platform.ImGuiMCPlatform;
import org.dimdev.dimdoors.api.client.UniformExt;
import org.dimdev.dimdoors.api.util.math.MathUtil;
import org.slf4j.Logger;
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

    @Shadow
    @Final
    private IntBuffer intValues;

    @Shadow
    @Final
    private int count;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract void markDirty();

    @Inject(method = "getTypeFromString", at = @At("RETURN"), cancellable = true)
    private static void addVec(String typeName, CallbackInfoReturnable<Integer> cir) {
        var i = cir.getReturnValue();

        if (i == -1) {
            if (typeName.startsWith("vec")) {
                if (typeName.endsWith("2")) i = UT_VEC2;
                if (typeName.endsWith("3")) i = UT_VEC3;
                if (typeName.endsWith("4")) i = UT_VEC4;
            } else if (typeName.startsWith("ivec")) {
                if (typeName.endsWith("2")) i = UT_IVEC2;
                if (typeName.endsWith("3")) i = UT_IVEC3;
                if (typeName.endsWith("4")) i = UT_IVEC4;
            }

            if (i != -1) cir.setReturnValue(i);
        }
    }

    @Inject(method = "upload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"), cancellable = true)
    public void uploadVec(CallbackInfo ci) {

        if (MathUtil.between(this.type, UT_VEC2, UT_VEC4)) {
            this.floatValues.rewind();

            switch (this.type) {
                case UT_VEC2 -> RenderSystem.glUniform2(this.location, this.floatValues);
                case UT_VEC3 -> RenderSystem.glUniform3(this.location, this.floatValues);
                case UT_VEC4 -> RenderSystem.glUniform4(this.location, this.floatValues);
            }

            ci.cancel();
        } else if (MathUtil.between(this.type, UT_IVEC2, UT_IVEC4)) {
            this.intValues.rewind();

            switch (this.type) {
                case UT_IVEC2 -> RenderSystem.glUniform2(this.location, this.intValues);
                case UT_IVEC3 -> RenderSystem.glUniform3(this.location, this.intValues);
                case UT_IVEC4 -> RenderSystem.glUniform4(this.location, this.intValues);
            }

            ci.cancel();
        }
    }

    @Override
    public void dimensionalDoors$set(int[] valueArray) {
        if (valueArray.length < this.count) {
            LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, valueArray.length);
        } else {
            this.intValues.position(0);
            this.intValues.put(valueArray);
            this.intValues.position(0);
            this.markDirty();
        }
    }
}
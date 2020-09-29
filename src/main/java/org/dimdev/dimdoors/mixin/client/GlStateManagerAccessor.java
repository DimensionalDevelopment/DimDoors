package org.dimdev.dimdoors.mixin.client;

import java.nio.FloatBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
@Mixin(GlStateManager.class)
public interface GlStateManagerAccessor {
    @Invoker
    static FloatBuffer invokeGetBuffer(float a, float b, float c, float d) {
        throw new AssertionError(String.valueOf(a + b + c + d));
    }
}

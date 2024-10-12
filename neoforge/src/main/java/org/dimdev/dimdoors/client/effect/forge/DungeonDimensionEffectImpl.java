package org.dimdev.dimdoors.client.effect.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.joml.Matrix4f;

public class DungeonDimensionEffectImpl {
    public static void renderEffect(DimensionSpecialEffects effect, ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        effect.renderSky(level, ticks, partialTick, poseStack.last().pose(), camera, projectionMatrix, isFoggy, setupFog);
    }
}

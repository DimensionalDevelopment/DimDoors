package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface CloudRenderBuffer {
    void renderCloudBuffer(PoseStack poseStack, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick, int ticks, double camX, double camY, double camZ, float cloudHeight, Vec3 cloudColor);
}

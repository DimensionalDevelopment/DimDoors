package org.dimdev.dimdoors.client.effect.sky

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer
import org.dimdev.dimdoors.world.pocket.type.addon.environment.sky.EndSkyData
import org.joml.Matrix4f
import java.util.function.Supplier

object EndEnvironmentRendering {
    fun renderSky(
        data: EndSkyData,
        level: ClientLevel,
        poseStack: PoseStack,
        projectionMatrix: Matrix4f,
        partialTicks: Float,
        isFoggy: Boolean,
        setupFog: Runnable,
        camera: Camera
    ) {
        RenderSystem.enableBlend()
        RenderSystem.depthMask(false)
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader)
        RenderSystem.setShaderTexture(0, TheEndPortalRenderer.END_SKY_LOCATION)

        for (i in 0..5) {
            poseStack.pushPose()
            if (i == 1) {
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0f))
            }

            if (i == 2) {
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f))
            }

            if (i == 3) {
                poseStack.mulPose(Axis.XP.rotationDegrees(180.0f))
            }

            if (i == 4) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0f))
            }

            if (i == 5) {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0f))
            }

            val matrix4f = poseStack.last().pose()
            val bufferBuilder =
                Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
            bufferBuilder.addVertex(matrix4f, -100.0f, -100.0f, -100.0f).setUv(0.0f, 0.0f).setColor(40, 40, 40, 255)
            bufferBuilder.addVertex(matrix4f, -100.0f, -100.0f, 100.0f).setUv(0.0f, 16.0f).setColor(40, 40, 40, 255)
            bufferBuilder.addVertex(matrix4f, 100.0f, -100.0f, 100.0f).setUv(16.0f, 16.0f).setColor(40, 40, 40, 255)
            bufferBuilder.addVertex(matrix4f, 100.0f, -100.0f, -100.0f).setUv(16.0f, 0.0f).setColor(40, 40, 40, 255)
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
            poseStack.popPose()
        }

        RenderSystem.depthMask(true)
        RenderSystem.disableBlend()
    }
}

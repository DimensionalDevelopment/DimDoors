package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.mixin.client.GlStateManagerAccessor;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import static org.lwjgl.opengl.GL11.*;

public class MyRenderLayer extends RenderLayer {
    public MyRenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, i, j, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer CRACK = RenderLayer.of("crack", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, MultiPhaseParameters.builder()
            .cull(DISABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .texture(NO_TEXTURE)
            .transparency(new Transparency("crack_transparency", () -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
            }, () -> {
                RenderSystem.disableBlend();
                RenderSystem.defaultBlendFunc();
            }))
            .build(false));

    public static RenderLayer TESSERACT = RenderLayer.of("tesseract", VertexFormats.POSITION_COLOR_TEXTURE, GL11.GL_QUADS, 256, MultiPhaseParameters.builder()
            .cull(DISABLE_CULLING)
            .lightmap(RenderPhase.DISABLE_LIGHTMAP)
            .texture(new Texture(DetachedRiftBlockEntityRenderer.TESSERACT_PATH, false, false))
            .build(false));

    public static RenderLayer getDimensionalPortal(int phase, EntranceRiftBlockEntity blockEntity) {
        Texture tex = new Texture(DimensionalPortalRenderer.getWarpPath(), false, false);
        return of("dimensional_portal", VertexFormats.POSITION_COLOR, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(TRANSLUCENT_TRANSPARENCY).texture(tex).texturing(new DimensionalPortalTexturing(phase, blockEntity)).fog(FOG).build(false));
    }

    public static class DimensionalPortalTexturing extends RenderPhase.Texturing {
        public final int layer;

        public DimensionalPortalTexturing(int layer, EntranceRiftBlockEntity blockEntity) {
            super("dimensional_portal_texturing", () -> {
                Direction orientation = blockEntity.getOrientation();
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                BlockPos off = blockEntity.getPos();
                Vec3i pos = MinecraftClient.getInstance().player != null ? off.subtract(MinecraftClient.getInstance().player.getBlockPos()) : new Vec3i(0, 0, 0);
                RenderSystem.translatef(0.5F, 0.5F, 0.0F);
                RenderSystem.scalef(0.5F + (pos.getX()/100.0F) * 1.2F, 0.5F + (pos.getZ()/100.0F) * 1.2F, 1.0F - (pos.getY()/100.0F) * 1.2F);
                RenderSystem.translatef(17.0F / (float) layer, (2.0F + (float) layer / 1.5F) * ((float) (Util.getMeasuringTimeMs() % 800000L) / 800000.0F), 0.0F);
                RenderSystem.rotatef(((float) (layer * layer) * 4321.0F + (float) layer * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
                RenderSystem.scalef(4.5F - (float) layer / 4.0F, 4.5F - (float) layer / 4.0F, 1.0F);
                RenderSystem.mulTextureByProjModelView();
                RenderSystem.matrixMode(5888);

                GlStateManager.texGenMode(GlStateManager.TexCoord.S, GL_OBJECT_LINEAR);
                GlStateManager.texGenMode(GlStateManager.TexCoord.T, GL_OBJECT_LINEAR);
                GlStateManager.texGenMode(GlStateManager.TexCoord.R, GL_OBJECT_LINEAR);

                GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, GlStateManagerAccessor.invokeGetBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, GlStateManagerAccessor.invokeGetBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, GlStateManagerAccessor.invokeGetBuffer(0.0F, 0.0F, 0.0F, 1.0F));

                GlStateManager.enableTexGen(GlStateManager.TexCoord.S);
                GlStateManager.enableTexGen(GlStateManager.TexCoord.T);
                GlStateManager.enableTexGen(GlStateManager.TexCoord.R);
            }, () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(5888);
                RenderSystem.clearTexGen();
            });
            this.layer = layer;
        }
    }
}

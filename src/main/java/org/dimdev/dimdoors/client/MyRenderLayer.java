package org.dimdev.dimdoors.client;

import java.util.Arrays;
import java.util.Random;

import com.flowpowered.math.vector.VectorNi;
import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;
import org.dimdev.dimdoors.mixin.DirectionAccessor;
import org.dimdev.dimdoors.util.RGBA;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.enums.DoorHinge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import static org.lwjgl.opengl.GL11.GL_ZERO;

public class MyRenderLayer extends RenderLayer {
    public static final Identifier WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
    public static final BooleanProperty OPEN_PROPERTY = BooleanProperty.of("open");
    public static final EnumProperty<DoorHinge> HINGE_PROPERTY = EnumProperty.of("hinge", DoorHinge.class);
    public static final DirectionProperty FACING_PROPERTY = DirectionProperty.of("facing", Arrays.asList(DirectionAccessor.getHorizontal()));
    public static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);
    private static final Identifier KEY_PATH = new Identifier("dimdoors:textures/other/keyhole.png");
    private static final Identifier KEYHOLE_LIGHT = new Identifier("dimdoors:textures/other/keyhole_light.png");
    private static final Random RANDOM = new Random(31100L);

    public MyRenderLayer(String string, VertexFormat vertexFormat, int i, int j, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, i, j, bl, bl2, runnable, runnable2);
    }

    public static RenderLayer CRACK = RenderLayer.of("crack", VertexFormats.POSITION_COLOR, GL11.GL_TRIANGLES, 256, MultiPhaseParameters.builder()
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
            .alpha(Alpha.HALF_ALPHA)
            .build(false));
//
//    public static RenderLayer getDimensionalPortal(int phase, EntranceRiftBlockEntity blockEntity) {
//        Direction orientation = blockEntity.getOrientation();
//        Texture tex = new Texture(WARP_PATH, false, false);
//        Vec3d offset = new Vec3d(orientation.getOpposite().getUnitVector());
//        return of("dimensional_portal",
//                VertexFormats.POSITION_COLOR,
//                7, 256,
//                false,
//                true,
//                RenderLayer.MultiPhaseParameters.builder()
//                        .transparency(ADDITIVE_TRANSPARENCY)
//                        .texture(tex)
//                        .texturing(new DimensionalPortalTexturing(phase,
//                                blockEntity,
//                                blockEntity.getPos().getX() + offset.x,
//                                blockEntity.getPos().getY() + offset.y,
//                                blockEntity.getPos().getZ() + offset.z))
//                        .fog(BLACK_FOG).build(false));
//    }

    public static RenderLayer getPortal(int layer) {
        RenderPhase.Transparency transparency;
        RenderPhase.Texture texture;
        if (layer <= 1) {
            transparency = TRANSLUCENT_TRANSPARENCY;
            texture = new RenderPhase.Texture(WARP_PATH, false, false);
        } else {
            transparency = ADDITIVE_TRANSPARENCY;
            texture = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false);
        }

        return of("dimensional_portal", VertexFormats.POSITION_COLOR, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(transparency).texture(texture).texturing(new RenderPhase.PortalTexturing(layer)).fog(BLACK_FOG).build(false));
    }

//    public static class DimensionalPortalTexturing extends RenderPhase.Texturing {
//        public final int layer;
//
//        public DimensionalPortalTexturing(int layer, EntranceRiftBlockEntity blockEntity, double x, double y, double z) {
//            super("dimensional_portal_texturing", () -> {
//                float translationScale = 16 - layer;
//                float scale = 0.3625F;
//                float offset = Util.getMeasuringTimeNano() % 200000L / 200000.0F;
//                if (layer == 0) {
//                    translationScale = 25.0F;
//                    scale = 0.125F;
//                }
//                if (layer == 1) {
//                    scale = 0.5F;
//                }
//                RenderSystem.matrixMode(GL11.GL_TEXTURE);
//                RenderSystem.pushMatrix();
//                RenderSystem.loadIdentity();
//                RenderSystem.translatef(0.1F, offset * translationScale, 0.1F);
//                RenderSystem.scalef(scale, scale, scale);
//                RenderSystem.translatef(0.5F, 0.5F, 0.5F);
//                RenderSystem.rotatef((layer * layer * 4321 + layer) * 9 * 2.0F, 0.0F, 0.0F, 1.0F);
//                RenderSystem.scalef(4.5F - (float)layer / 4.0F, 4.5F - (float)layer / 4.0F, 1.0F);
//                RenderSystem.mulTextureByProjModelView();
//                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//                RenderSystem.setupEndPortalTexGen();
//            }, () -> {
//                RenderSystem.matrixMode(GL11.GL_TEXTURE);
//                RenderSystem.popMatrix();
//                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//                RenderSystem.clearTexGen();
//            });
//            this.layer = layer;
//        }
//    }
}

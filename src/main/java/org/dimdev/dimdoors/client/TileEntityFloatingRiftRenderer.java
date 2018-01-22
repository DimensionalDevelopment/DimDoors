package org.dimdev.dimdoors.client;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityFloatingRiftRenderer extends TileEntitySpecialRenderer<TileEntityFloatingRift> {
    private static final ResourceLocation tesseract_path = new ResourceLocation(DimDoors.MODID + ":textures/other/tesseract.png");

    private static final Vector4f[] tesseract = {
            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),

            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),

            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(-0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,-0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,-0.5f,-0.5f,0.5f),

            new Vector4f(-0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(-0.5f,0.5f,0.5f,0.5f),
            new Vector4f(-0.5f,0.5f,-0.5f,0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),

            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),

            new Vector4f(0.5f,-0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,-0.5f,0.5f,0.5f),
            new Vector4f(0.5f,-0.5f,-0.5f,0.5f),

            new Vector4f(0.5f,0.5f,-0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,-0.5f),
            new Vector4f(0.5f,0.5f,0.5f,0.5f),
            new Vector4f(0.5f,0.5f,-0.5f,0.5f)
    };

    /**
     * Renders the rift.
     */
    @Override
    public void render(TileEntityFloatingRift te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        double radian = update(te, partialTicks) * TrigMath.DEG_TO_RAD;
        RGBA color = te.getColor();
        if (color == null) color = new RGBA(1, 0.5f, 1, 1);

        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        bindTexture(tesseract_path);

        GlStateManager.translate(x+0.5,y+0.5,z+0.5);
        GlStateManager.scale(0.25,0.25,0.25);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        for (int i = 0; i < tesseract.length; i+=4) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            project(worldRenderer, rotation(tesseract[i], radian),0,0, color);
            project(worldRenderer, rotation(tesseract[i+1], radian),0,1, color);
            project(worldRenderer, rotation(tesseract[i+2], radian),1,1, color);
            project(worldRenderer, rotation(tesseract[i+3], radian),1,0, color);
            tessellator.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private double update(TileEntityFloatingRift te, float partialTicks) {
        te.renderAngle = (te.renderAngle + 1 * partialTicks) % 360;
        return te.renderAngle;
    }

    private Vector4f rotation(Vector4f v, double angle) {
        double x = v.getX();
        double y = v.getY();
        double z = v.getZ();
        double w = v.getW();

        return new Vector4f(
                x * TrigMath.cos(angle) - y * TrigMath.sin(angle),
                x * TrigMath.sin(angle) + y * TrigMath.cos(angle),
                z * TrigMath.cos(angle) - w * TrigMath.sin(angle),
                z * TrigMath.sin(angle) + w * TrigMath.cos(angle));
    }

    private void project(BufferBuilder buffer, Vector4f vector, int u, int v, RGBA color) {
        double scalar = 1d/(vector.getW()+1d);
        Vector3f vector1 = vector.toVector3().mul(scalar);

        buffer.pos(vector1.getX(), vector1.getY(), vector1.getZ()).tex(u,v).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }
}

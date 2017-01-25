package com.zixiken.dimdoors.client;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.items.ModItems;
import com.zixiken.dimdoors.shared.tileentities.TileEntityRift;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;


public class RenderRift extends TileEntitySpecialRenderer<TileEntityRift> {
    private static final EntityItem ITEM = new EntityItem(Minecraft.getMinecraft().world, 0,0,0, new ItemStack(ModItems.itemStableFabric));
    private static ResourceLocation tesseract_path = new ResourceLocation(DimDoors.MODID + ":textures/other/tesseract.png");

    private static Vector4f tesseract[] = {
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

    private static double angle = 0;

    /**
     * Renders the rift.
     */
    @Override
    public void renderTileEntityAt(TileEntityRift te, double x, double y, double z, float partialTicks, int destroyStage) {
        double radian = update(partialTicks) * TrigMath.DEG_TO_RAD;

        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        this.bindTexture(tesseract_path);

        GlStateManager.translate(x+0.5,y+0.5,z+0.5);
        GlStateManager.scale(0.25,0.25,0.25);

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldRenderer = tessellator.getBuffer();

        for (int i = 0; i < tesseract.length; i+=4) {
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            project(worldRenderer, rotation(tesseract[i], radian),0,0);
            project(worldRenderer, rotation(tesseract[i+1], radian),0,1);
            project(worldRenderer, rotation(tesseract[i+2], radian),1,1);
            project(worldRenderer, rotation(tesseract[i+3], radian),1,0);
            tessellator.draw();
        }

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private double update(float partialTicks) {
        return (angle = (angle + 3*partialTicks) % 360);
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

    private void project(VertexBuffer buffer, Vector4f vector, int u, int v) {
        double scalar = 1d/(vector.getW()+1d);
        Vector3f center = Vector3f.from(0.5f);
        Vector3f vector1 = vector.toVector3().mul(scalar);

        buffer.pos(vector1.getX(), vector1.getY(), vector1.getZ()).tex(u,v).color(1f,1f,1f,1f).endVertex();
    }
}

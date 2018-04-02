package org.dimdev.dimdoors.client;

import com.flowpowered.math.TrigMath;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.tileentities.TileEntityFloatingRift;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityFloatingRiftRenderer extends TileEntitySpecialRenderer<TileEntityFloatingRift> {
    private static final ResourceLocation tesseract_path = new ResourceLocation(DimDoors.MODID + ":textures/other/tesseract.png");

    private static final Tesseract tesseract = new Tesseract();

    @Override
    public void render(TileEntityFloatingRift rift, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (ModConfig.graphics.tesseractRifts) {
            renderTesseract(rift, x, y, z, partialTicks, destroyStage, alpha);
        } else {
            renderCrack(rift, x, y, z, partialTicks, destroyStage, alpha);
        }
    }

    private void renderCrack(TileEntityFloatingRift rift, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GL11.glPushMatrix();

        // Make the rift render on both sides, disable texture mapping and lighting
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        // TODO: This was commented out in the old code, what does it do?
        //GlStateManager.colorLogicOp(GlStateManager.LogicOp.INVERT);
        //GlStateManager.enableColorLogic();

        RiftCrackRenderer.drawCrack(rift.riftRotation, rift.getCurve(), rift.growth / 15, x, y, z);

        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();

        GL11.glPopMatrix();
    }

    private void renderTesseract(TileEntityFloatingRift rift, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        double radian = updateTesseractAngle(rift, partialTicks) * TrigMath.DEG_TO_RAD;
        RGBA color = rift.getColor();
        if (color == null) color = new RGBA(1, 0.5f, 1, 1);

        GlStateManager.enableLighting();
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();

        bindTexture(tesseract_path);

        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scale(0.25, 0.25, 0.25);

        tesseract.draw(color, radian);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private double updateTesseractAngle(TileEntityFloatingRift rift, float partialTicks) {
        rift.renderAngle = (rift.renderAngle + 5 * partialTicks) % 360;
        return rift.renderAngle;
    }
}

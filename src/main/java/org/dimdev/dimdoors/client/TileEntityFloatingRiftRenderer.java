package org.dimdev.dimdoors.client;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.client.tesseract.Tesseract;
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

    private static final Tesseract tesseract = new Tesseract();

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

        tesseract.draw(color, radian);

        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private double update(TileEntityFloatingRift te, float partialTicks) {
        te.renderAngle = (te.renderAngle + 5 * partialTicks) % 360;
        return te.renderAngle;
    }
}

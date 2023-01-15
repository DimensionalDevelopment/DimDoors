package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityEntranceRiftRenderer extends TileEntitySpecialRenderer<TileEntityEntranceRift> {

    private final ResourceLocation keyPath = new ResourceLocation(DimDoors.MODID + ":textures/other/keyhole.png");
    private final ResourceLocation keyholeLight = new ResourceLocation(DimDoors.MODID + ":textures/other/keyhole_light.png");

    private void renderKeyHole(TileEntityEntranceRift tile, double x, double y, double z, int i) {
        EnumFacing rotation = EnumFacing.byHorizontalIndex((tile.orientation.getHorizontalIndex() + 3) % 4);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - 90 * rotation.getHorizontalIndex(), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.007F, .25F, 0F);
        switch (rotation) {
            case SOUTH:
                GlStateManager.translate(0.5F, 0F, -0.03F);
                break;
            case WEST:
                GlStateManager.translate(-0.5F, 0, -0.03F);
                break;
            case NORTH:
                GlStateManager.translate(-.5F, 0F, .97F);
                break;
            case EAST:
                GlStateManager.translate(.5F, 0F, .97F);
                break;
        }
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        if (i == 1) {
            bindTexture(keyholeLight);
            GlStateManager.color(1, 1, 1, .7f);
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_COLOR);
        } else {
            bindTexture(keyPath);
            GlStateManager.blendFunc(GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA);
        }
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.00860625F, 0.00730625F, 0.0086625F);
        GlStateManager.translate(-65.0F, -107.0F, -3.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        byte b0 = 7;
        worldRenderer.pos(-b0, 128 + b0, 0.0D).tex(0.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos(128 + b0, 128 + b0, 0.0D).tex(1.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos(128 + b0, -b0, 0.0D).tex(1.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos(-b0, -b0, 0.0D).tex(0.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        tessellator.draw();
        GlStateManager.translate(0.0F, 0.0F, -1.0F);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileEntityEntranceRift entrance, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Vec3d offset = new Vec3d(entrance.orientation.getOpposite().getDirectionVec()).scale(
                entrance.orientation == EnumFacing.NORTH ||
                entrance.orientation == EnumFacing.WEST ||
                entrance.orientation == EnumFacing.UP ? entrance.pushIn : entrance.pushIn - 1);
        DimensionalPortalRenderer.renderDimensionalPortal(
                x + offset.x,
                y + offset.y,
                z + offset.z,
                //entrance.orientation.getHorizontalAngle(),
                //entrance.orientation.getDirectionVec().getY() * 90,
                entrance.orientation,
                entrance.extendLeft + entrance.extendRight,
                entrance.extendDown + entrance.extendUp,
                entrance.getColors(16));
        if (entrance.lockStatus >= 1) {
            for (int i = 0; i < 1 + entrance.lockStatus; i++) renderKeyHole(entrance, x, y, z, i);
        }
    }
}

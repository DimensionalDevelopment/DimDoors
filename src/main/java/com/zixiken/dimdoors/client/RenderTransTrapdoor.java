package com.zixiken.dimdoors.client;

import java.nio.FloatBuffer;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockTransTrapdoor;
import com.zixiken.dimdoors.shared.tileentities.TileEntityTransTrapdoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTransTrapdoor extends TileEntitySpecialRenderer<TileEntityTransTrapdoor> {

    private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private ResourceLocation riftPath = new ResourceLocation(DimDoors.MODID + ":textures/other/RIFT.png");
    private ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/WARP.png");

    /**
     * Renders the dimdoor.
     */
    public void renderTransTrapdoorTileEntity(TileEntityTransTrapdoor tile, double x, double y, double z, float partialTicks) {
        GlStateManager.disableLighting();
        Random random = new Random(31100L);
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());

        for (int count = 0; count < 16; ++count) {
            GlStateManager.pushMatrix();

            float var15 = 16 - count;
            float var16 = 0.2625F;
            float var17 = 1.0F / (var15 + 1.0F);

            if (count == 0) {
                this.bindTexture(riftPath);
                var17 = 0.1F;
                var15 = 25.0F;
                var16 = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (count == 1) {
                this.bindTexture(warpPath);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
                var16 = .5F;
            }

            GlStateManager.translate(Minecraft.getSystemTime() % 200000L / 200000.0F, 0, 0.0F);
            GlStateManager.translate(0, Minecraft.getSystemTime() % 200000L / 200000.0F, 0.0F);

            GlStateManager.translate(0, 0, Minecraft.getSystemTime() % 200000L / 200000.0F);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));

            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);

            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, Minecraft.getSystemTime() % 200000L / 200000.0F * var15, 0.0F);
            GlStateManager.scale(var16, var16, var16);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate((count * count * 4321 + count * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            float r = (random.nextFloat() * 0.5F + 0.1F) * var17;
            float g = (random.nextFloat() * 0.4F + 0.4F) * var17;
            float b = (random.nextFloat() * 0.6F + 0.5F) * var17;

            if (count == 0) {
                g = 1.0F;
                b = 1.0F;
            }

            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer worldrenderer = tessellator.getBuffer();
            worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            if (BlockTransTrapdoor.isTrapdoorSetLow(state)) {
                if (state.getValue(BlockTrapDoor.OPEN)) {
                    worldrenderer.pos(x, y + 0.2, z).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x, y + 0.2, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.2, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.2, z).color(r, g, b, 1.0F).endVertex();
                } else {
                    worldrenderer.pos(x, y + 0.15, z).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x, y + 0.15, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.15, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.15, z).color(r, g, b, 1.0F).endVertex();
                }
            } else {
                if (state.getValue(BlockTrapDoor.OPEN)) {
                    worldrenderer.pos(x, y + 0.95, z).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x, y + 0.95, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.95, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.95, z).color(r, g, b, 1.0F).endVertex();
                } else {
                    worldrenderer.pos(x, y + 0.85, z).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x, y + 0.85, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.85, z + 1).color(r, g, b, 1.0F).endVertex();
                    worldrenderer.pos(x + 1, y + 0.85, z).color(r, g, b, 1.0F).endVertex();
                }
            }

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.disableLighting();
    }

    private FloatBuffer getFloatBuffer(float par1, float par2, float par3, float par4) {
        this.buffer.clear();
        this.buffer.put(par1).put(par2).put(par3).put(par4);
        this.buffer.flip();
        return this.buffer;
    }

    @Override
    public void renderTileEntityAt(TileEntityTransTrapdoor te, double x, double y, double z, float partialTicks, int destroyStage) {
        this.renderTransTrapdoorTileEntity(te, x, y, z, partialTicks);
    }
}

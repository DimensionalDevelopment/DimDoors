package com.zixiken.dimdoors.client;

import java.nio.FloatBuffer;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityVerticalEntranceRift;
import com.zixiken.dimdoors.shared.util.RGBA;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class RenderVerticalEntranceRift extends TileEntitySpecialRenderer<TileEntityVerticalEntranceRift> {

    private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/warp.png");
    private ResourceLocation keyPath = new ResourceLocation(DimDoors.MODID + ":textures/other/keyhole.png");
    private ResourceLocation keyholeLight = new ResourceLocation(DimDoors.MODID + ":textures/other/keyhole_light.png");

    /**
     * Renders the dimdoor.
     */
    public void renderDimDoorTileEntity(TileEntityVerticalEntranceRift tile, double x, double y, double z) {
        GL11.glDisable(GL11.GL_LIGHTING);
        Random rand = new Random(31100L);

        EnumFacing orientation = tile.orientation;
        IBlockState state = tile.getWorld().getBlockState(tile.getPos());
        if (state.getBlock() instanceof BlockDoor && state.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER) y += 1;
        // TODO: option for non-doors in the TileEntityEntranceRift

        for (int count = 0; count < 16; ++count) {
            GlStateManager.pushMatrix();

            float var15 = 16 - count;
            float var16 = 0.2625F;
            float var17 = 1.0F / (var15 + .80F);

            bindTexture(warpPath);
            GlStateManager.enableBlend();

            if (count == 0) {
                var17 = 0.1F;
                var15 = 25.0F;
                var16 = 0.125F;

                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (count == 1) {
                var16 = .5F;

                GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            GlStateManager.translate(Minecraft.getSystemTime() % 200000L / 200000.0F, 0, 0.0F);
            GlStateManager.translate(0, Minecraft.getSystemTime() % 200000L / 200000.0F, 0.0F);
            GlStateManager.translate(0, 0, Minecraft.getSystemTime() % 200000L / 200000.0F);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_LINEAR);

            switch (orientation) {
                case SOUTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getFloatBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, getFloatBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
            }

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

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

            RGBA color = tile.getEntranceRenderColor(rand); // TODO: cache this since it's constant
            GlStateManager.color(color.getRed() * var17, color.getGreen() * var17, color.getBlue() * var17, color.getAlpha());

            switch (orientation) {
                case SOUTH:
                    worldRenderer.pos(x, y - 1, z + .99).endVertex();
                    worldRenderer.pos(x + 1, y - 1, z + .99).endVertex();
                    worldRenderer.pos(x + 1, y + 1, z + .99).endVertex();
                    worldRenderer.pos(x, y + 1, z + .99).endVertex();
                    break;
                case WEST:
                    worldRenderer.pos(x + .01, y - 1, z).endVertex();
                    worldRenderer.pos(x + .01, y - 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .01, y + 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .01, y + 1, z).endVertex();
                    break;
                case NORTH:
                    worldRenderer.pos(x, y + 1, z + .01).endVertex();
                    worldRenderer.pos(x + 1, y + 1, z + .01).endVertex();
                    worldRenderer.pos(x + 1, y - 1, z + .01).endVertex();
                    worldRenderer.pos(x, y - 1, z + .01).endVertex();
                    break;
                case EAST:
                    worldRenderer.pos(x + .99, y + 1, z).endVertex();
                    worldRenderer.pos(x + .99, y + 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .99, y - 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .99, y - 1, z).endVertex();
                    break;
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
        GlStateManager.enableLighting();
    }

    private FloatBuffer getFloatBuffer(float par1, float par2, float par3, float par4) {
        buffer.clear();
        buffer.put(par1).put(par2).put(par3).put(par4);
        buffer.flip();
        return buffer;
    }

    private void renderKeyHole(TileEntityVerticalEntranceRift tile, double x, double y, double z, int i) {
        EnumFacing rotation = EnumFacing.getHorizontal((tile.orientation.getHorizontalIndex() + 3) % 4);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        //x = ActiveRenderInfo.getPosition().xCoord;
        //y = ActiveRenderInfo.getPosition().yCoord;
        //z = ActiveRenderInfo.getPosition().zCoord;

        GlStateManager.rotate(180.0F - 90 * rotation.getHorizontalIndex(), 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef((float)(-90 * rotation), 0.0F, 0.0F, 1.0F);

        GlStateManager.translate(0.007F, .25F, 0F);

        switch (rotation) {
            case SOUTH:
                GL11.glTranslatef(0.5F, 0F, -0.03F);
                break;
            case WEST:
                GL11.glTranslatef(-0.5F, 0, -0.03F);
                break;
            case NORTH:
                GL11.glTranslatef(-.5F, 0F, .97F);
                break;
            case EAST:
                GL11.glTranslatef(.5F, 0F, .97F);
                break;
        }

        GL11.glDisable(GL_LIGHTING);

        GL11.glEnable(GL11.GL_BLEND);

        if (i == 1) {
            bindTexture(keyholeLight);
            GlStateManager.color(1, 1, 1, .7f);
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_DST_COLOR);
        } else {
            bindTexture(keyPath);
            GlStateManager.blendFunc(GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA);
        }

        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.scale(0.00860625F, 0.00730625F, 0.0086625F);
        GlStateManager.translate(-65.0F, -107.0F, -3.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
        byte b0 = 7;

        worldRenderer.pos((double) (0 - b0), (double) (128 + b0), 0.0D).tex(0.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos((double) (128 + b0), (double) (128 + b0), 0.0D).tex(1.0D, 1.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos((double) (128 + b0), (double) (0 - b0), 0.0D).tex(1.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        worldRenderer.pos((double) (0 - b0), (double) (0 - b0), 0.0D).tex(0.0D, 0.0D).normal(0.0F, 0.0F, -1.0F).endVertex();
        tessellator.draw();

        GlStateManager.translate(0.0F, 0.0F, -1.0F);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    @Override
    public void render(TileEntityVerticalEntranceRift te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        ((BlockDimDoorBase) world.getBlockState(pos).getBlock()).updateAttachedTile(world, pos);
        if (te.doorShouldRender) {
            renderDimDoorTileEntity(te, x, y, z);
            if (te.lockStatus >= 1) {
                for (int i = 0; i < 1 + te.lockStatus; i++) {
                    renderKeyHole(te, x, y, z, i);
                }
            }

        }
    }
}

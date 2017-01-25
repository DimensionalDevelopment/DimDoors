package com.zixiken.dimdoors.client;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_COLOR;
import java.nio.FloatBuffer;
import java.util.Random;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.blocks.BlockDimDoorBase;
import com.zixiken.dimdoors.shared.tileentities.TileEntityDimDoor;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

public class RenderDimDoor extends TileEntitySpecialRenderer<TileEntityDimDoor> {

    private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/WARP.png");
    private ResourceLocation keyPath = new ResourceLocation(DimDoors.MODID + ":textures/other/keyhole.png");
    private ResourceLocation KeyholeLight = new ResourceLocation(DimDoors.MODID + ":textures/other/keyholeLight.png");

    /**
     * Renders the dimdoor.
     */
    public void renderDimDoorTileEntity(TileEntityDimDoor tile, double x, double y, double z) {
        GL11.glDisable(GL11.GL_LIGHTING);
        Random rand = new Random(31100L);

        for (int count = 0; count < 16; ++count) {
            GlStateManager.pushMatrix();

            float var15 = 16 - count;
            float var16 = 0.2625F;
            float var17 = 1.0F / (var15 + .80F);

            this.bindTexture(warpPath);
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

            EnumFacing orientation = EnumFacing.getHorizontal((tile.orientation.getHorizontalIndex() % 4) + 4);

            switch (orientation) {
                case SOUTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case WEST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case EAST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_PLANE, this.getFloatBuffer(0.0F, 0.0F, 1.0F, -0.15F));
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
            VertexBuffer worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

            float[] color = tile.getRenderColor(rand);
            GlStateManager.color(color[0] * var17, color[1] * var17, color[2] * var17, color[3]);

            switch (tile.orientation) {
                case SOUTH:
                    worldRenderer.pos(x + .01, y - 1, z).endVertex();
                    worldRenderer.pos(x + .01, y - 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .01, y + 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .01, y + 1, z).endVertex();
                    break;
                case WEST:
                    worldRenderer.pos(x, y + 1, z + .01).endVertex();
                    worldRenderer.pos(x + 1, y + 1, z + .01).endVertex();
                    worldRenderer.pos(x + 1, y - 1, z + .01).endVertex();
                    worldRenderer.pos(x, y - 1, z + .01).endVertex();
                    break;
                case NORTH:
                    worldRenderer.pos(x + .99, y + 1, z).endVertex();
                    worldRenderer.pos(x + .99, y + 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .99, y - 1, z + 1.0D).endVertex();
                    worldRenderer.pos(x + .99, y - 1, z).endVertex();
                    break;
                case EAST:
                    worldRenderer.pos(x, y - 1, z + .99).endVertex();
                    worldRenderer.pos(x + 1, y - 1, z + .99).endVertex();
                    worldRenderer.pos(x + 1, y + 1, z + .99).endVertex();
                    worldRenderer.pos(x, y + 1, z + .99).endVertex();
                    break;
                /*case 4:
					GL11.glVertex3d(x + .15F, y - 1 , z);
					GL11.glVertex3d(x + .15, y - 1, z + 1.0D);
					GL11.glVertex3d(x + .15, y + 1, z + 1.0D);
					GL11.glVertex3d(x + .15, y + 1, z);
					break;
				case 5:
					GL11.glVertex3d(x, y + 1, z + .15);
					GL11.glVertex3d(x + 1, y + 1, z + .15);
					GL11.glVertex3d(x + 1, y - 1, z + .15);
					GL11.glVertex3d(x, y - 1, z + .15);
					break;
				case 6:
					GL11.glVertex3d(x + .85, y + 1, z);
					GL11.glVertex3d(x + .85, y + 1, z + 1.0D);
					GL11.glVertex3d(x + .85, y - 1, z + 1.0D);
					GL11.glVertex3d(x + .85, y - 1, z);
					break;
				case 7:
					GL11.glVertex3d(x, y - 1, z + .85);
					GL11.glVertex3d(x + 1, y - 1, z + .85);
					GL11.glVertex3d(x + 1, y + 1, z + .85);
					GL11.glVertex3d(x, y + 1, z + .85);
					break;*/
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

    private void renderKeyHole(TileEntityDimDoor tile, double x, double y, double z, int i) {
        EnumFacing rotation = EnumFacing.getHorizontal((tile.orientation.getHorizontalIndex() + 3) % 4);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        x = ActiveRenderInfo.getPosition().xCoord;
        y = ActiveRenderInfo.getPosition().yCoord;
        z = ActiveRenderInfo.getPosition().zCoord;

        GlStateManager.rotate(180.0F - 90 * rotation.getHorizontalIndex(), 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef((float)(-90 * rotation), 0.0F, 0.0F, 1.0F);

        GlStateManager.translate(0.007F, .25F, 0F);

        switch (rotation) {
            case SOUTH:
                GL11.glTranslatef(-0.5F, 0, -0.03F);
                break;
            case WEST:
                GL11.glTranslatef(-.5F, 0F, .97F);
                break;
            case NORTH:
                GL11.glTranslatef(.5F, 0F, .97F);
                break;
            case EAST:
                GL11.glTranslatef(0.5F, 0F, -0.03F);
        }

        GL11.glDisable(GL_LIGHTING);

        GL11.glEnable(GL11.GL_BLEND);

        if (i == 1) {
            bindTexture(KeyholeLight);
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
        VertexBuffer worldRenderer = tessellator.getBuffer();
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
    public void renderTileEntityAt(TileEntityDimDoor te, double x, double y, double z, float partialTicks, int destroyStage) {
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        ((BlockDimDoorBase) world.getBlockState(pos).getBlock()).updateAttachedTile(world, pos);
        if (te.doorIsOpen) {
            renderDimDoorTileEntity(te, x, y, z);
            if (te.lockStatus >= 1) {
                for (int i = 0; i < 1 + te.lockStatus; i++) {
                    this.renderKeyHole(te, x, y, z, i);
                }
            }

        }
    }
}

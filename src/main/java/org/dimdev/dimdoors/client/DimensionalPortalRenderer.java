package org.dimdev.dimdoors.client;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorQuartz;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public final class DimensionalPortalRenderer {

    private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/warp.png");
    private static final PropertyBool openProperty = PropertyBool.create("open");
    private static final PropertyEnum<BlockDoor.EnumHingePosition> hingeProperty = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);

    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final BlockModelShapes blockModelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    private static final ModelManager modelManager = blockModelShapes.getModelManager();
    private static final BlockStateMapper blockStateMapper = blockModelShapes.getBlockStateMapper();

    /**
     * Renders a dimensional portal, for use in various situations. Code is mostly based
     * on vanilla's TileEntityEndGatewayRenderer.
     *
     * @param x           The x coordinate of the wall's center.
     * @param y           The y coordinate of the wall's center.
     * @param z           The z coordinate of the wall's center.
     *                    //@param yaw    The yaw of the normal vector of the wall in degrees, relative to __.
     *                    //@param pitch  The pitch of the normal vector of the wall, relative to the xz plane.
     * @param orientation The orientation of the wall.
     * @param width       The width of the wall.
     * @param height      The height of the wall.
     * @param colors      An array containing the color to use on each pass. Its length determines the number of passes to do.
     */
    public static void renderDimensionalPortal(double x, double y, double z, EnumFacing orientation, double width, double height, RGBA[] colors) { // TODO: Make this work at any angle
        GlStateManager.disableLighting();
        GlStateManager.disableCull();

        for (int pass = 0; pass < 16; pass++) {
            GlStateManager.pushMatrix();

            float translationScale = 16 - pass;
            float scale = 0.2625F;
            float colorMultiplier = 1.0F / (translationScale + .80F);

            Minecraft.getMinecraft().getTextureManager().bindTexture(warpPath);
            GlStateManager.enableBlend();

            if (pass == 0) {
                colorMultiplier = 0.1F;
                translationScale = 25.0F;
                scale = 0.125F;

                GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                scale = 0.5F;
                GlStateManager.blendFunc(GL_ONE, GL_ONE);
            }

            double offset = Minecraft.getSystemTime() % 200000L / 200000.0F;
            GlStateManager.translate(offset, offset, offset);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL_OBJECT_LINEAR);

            if (orientation == EnumFacing.UP || orientation == EnumFacing.DOWN) {
                GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
            } else {
                GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_OBJECT_LINEAR);
            }

            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
                case SOUTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case UP:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
                case DOWN:
                    GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
            }

            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);

            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, offset * translationScale, 0.0F);
            GlStateManager.scale(scale, scale, scale);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            GlStateManager.rotate((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);

            RGBA color = colors[pass];
            GlStateManager.color(color.getRed() * colorMultiplier, color.getGreen() * colorMultiplier, color.getBlue() * colorMultiplier, color.getAlpha());

            switch (orientation) {
                case NORTH:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    worldRenderer.pos(x + width, y + height, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    break;
                case SOUTH:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    worldRenderer.pos(x + width, y + height, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    break;
                case WEST:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    worldRenderer.pos(x, y + height, z + width).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    break;
                case EAST:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y + height, z).endVertex();
                    worldRenderer.pos(x, y + height, z + width).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    break;
                case UP:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    worldRenderer.pos(x + width, y, z + width).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    break;
                case DOWN:
                    worldRenderer.pos(x, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z).endVertex();
                    worldRenderer.pos(x + width, y, z + width).endVertex();
                    worldRenderer.pos(x, y, z + width).endVertex();
                    break;
            }

            tessellator.draw();

            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL_MODELVIEW);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
    }

    //TODO Check to make sure block type is valid (and not air)
    @SuppressWarnings("Duplicates")
    public static void renderFoxWallAxis(TileEntityRift tileEntity, Vector3d pos, Vector3d offset, EnumFacing orientation, double width, double height, RGBA[] colors) {
        //System.out.println(orientation);
        if (orientation == null) return;
        switch (orientation) {
            case UP:
            case DOWN:
                return;
        }

        final float pathLength = 15;

        //System.out.println("RENDER");
        //System.out.println(height);
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableTexture2D();

        //GlStateManager.disableCull();
        //GlStateManager.disableDepth();


        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 0xff);
        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE);
        glStencilMask(0xFF);
        glClearStencil(0);
        glClear(GL_STENCIL_BUFFER_BIT);

        //System.out.println(glGetInteger(GL_STENCIL_BITS));

        final Vector3d merged = pos.add(offset);
        final Vector3d left, right, depth, up;
        switch (orientation) {
            case NORTH:
                left = merged.add(width, 0, 0);//new Vector3d(x + width, y, z);
                right = merged;
                depth = new Vector3d(0, 0, pathLength);
                break;
            case SOUTH:
                left = merged;
                right = merged.add(width, 0, 0);
                depth = new Vector3d(0, 0, -pathLength);
                break;
            case WEST:
                left = merged;
                right = merged.add(0, 0, width);
                depth = new Vector3d(pathLength, 0, 0);
                break;
            case EAST:
                left = merged.add(0, 0, width);
                right = merged;
                depth = new Vector3d(-pathLength, 0, 0);
                break;
            default:
                return;
        }

        up = new Vector3d(0, height, 0);

        Vector3d leftTop = left.add(up),
                rightTop = right.add(up),
                leftBack = left.add(depth),
                rightBack = right.add(depth),
                leftTopBack = leftTop.add(depth),
                rightTopBack = rightTop.add(depth);

        Block blockType = tileEntity.getBlockType();
        Vector3f voidColor;
        Vector3i pathColor;

        if(blockType instanceof BlockDimensionalDoorQuartz){
            voidColor = new Vector3f(1,1,1);
            pathColor = new Vector3i(255,255,0);
        } else {
            voidColor = new Vector3f(0,0,0);
            pathColor = new Vector3i(0,255,255);
        }


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        GlStateManager.color(voidColor.getX(), voidColor.getY(), voidColor.getZ(), 1f);

        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left.getX(), left.getY(), left.getZ()).endVertex();
        worldRenderer.pos(right.getX(), right.getY(), right.getZ()).endVertex();
        worldRenderer.pos(rightTop.getX(), rightTop.getY(), rightTop.getZ()).endVertex();
        worldRenderer.pos(leftTop.getX(), leftTop.getY(), leftTop.getZ()).endVertex();
        tessellator.draw();

        glStencilMask(0);
        glStencilFunc(GL_EQUAL, 1, 0xff);
        GlStateManager.disableDepth();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int oldShadeModel = GlStateManager.glGetInteger(GL_SHADE_MODEL);
        GlStateManager.shadeModel(GL_SMOOTH);


        worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(left.getX(), left.getY(), left.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 200)
                .endVertex();
        worldRenderer.pos(right.getX(), right.getY(), right.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 200)
                .endVertex();
        worldRenderer.pos(rightBack.getX(), rightBack.getY(), rightBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 0)
                .endVertex();
        worldRenderer.pos(leftBack.getX(), leftBack.getY(), leftBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(),0)
                .endVertex();
        tessellator.draw();

        worldRenderer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(leftTop.getX(), leftTop.getY(), leftTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 200).endVertex();
        worldRenderer.pos(leftTopBack.getX(), leftTopBack.getY(), leftTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(),0).endVertex();
        worldRenderer.pos(rightTop.getX(), rightTop.getY(), rightTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 200).endVertex();
        worldRenderer.pos(rightTopBack.getX(), rightTopBack.getY(), rightTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(),0).endVertex();
        tessellator.draw();

        if (blockType instanceof BlockDoor) {
            //GlStateManager.disableDepth();
            //glDisable(GL_STENCIL_TEST);
            GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.pushMatrix();

            GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();

            Map<IBlockState, ModelResourceLocation> map = blockStateMapper.getVariants(blockType);
            IBlockState doorBottomState = tileEntity.getWorld().getBlockState(tileEntity.getPos());
            IBlockState doorTopState = tileEntity.getWorld().getBlockState(tileEntity.getPos().up());
            //System.out.println(tileEntity.getWorld().getBlockState(tileEntity.getPos().up(3)));
            if (doorBottomState.getBlock() instanceof BlockDoor && doorTopState.getBlock() instanceof BlockDoor) {
                doorBottomState = doorBottomState.withProperty(hingeProperty, doorTopState.getValue(hingeProperty));
                doorTopState = doorTopState.withProperty(openProperty, doorBottomState.getValue(openProperty));
                //System.out.println();
                //System.out.println(doorBottomState);
                //System.out.println(doorBottomState.getValue(PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class)));
                if (doorBottomState.getValue(openProperty)) {
                    drawState(tessellator, worldRenderer, map, doorBottomState, null);
                    GlStateManager.translate(0, 1, 0);
                    drawState(tessellator, worldRenderer, map, doorTopState, null);
                    GlStateManager.translate(0, -1, 0);
                }

                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.disableLighting();

                Vector3d doorDepth = depth.mul(1.2);
                GlStateManager.translate(doorDepth.getX(), doorDepth.getY(), doorDepth.getZ());
                drawState(tessellator, worldRenderer, map, doorBottomState.withProperty(openProperty, false), orientation);
                GlStateManager.translate(0, 1, 0);
                drawState(tessellator, worldRenderer, map, doorTopState.withProperty(openProperty, false), orientation);

            }

            GlStateManager.popMatrix();



        }


        glStencilMask(0xff);
        glDisable(GL_STENCIL_TEST);

        GlStateManager.shadeModel(oldShadeModel);
        GlStateManager.disableBlend();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.enableCull();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }

    private static void drawState(Tessellator tessellator, BufferBuilder worldRenderer, Map<IBlockState, ModelResourceLocation> map,
                                  IBlockState doorBottomState, EnumFacing side) {
        ModelResourceLocation location;
        IBakedModel model;
        List<BakedQuad> quads;
        location = map.get(doorBottomState);
        model = modelManager.getModel(location);
        quads = model.getQuads(null, side, 0);
        if (!quads.isEmpty()) {
            worldRenderer.begin(GL_QUADS, quads.get(0).getFormat());
            //System.out.println(quads.size());
            for (BakedQuad quad : quads) {
                worldRenderer.addVertexData(quad.getVertexData());
                /*for (int i = 1; i <5 ; i++) {
                    worldRenderer.putColorMultiplier(1, 0, 0, i);
                }*/
            }
            tessellator.draw();
        }
    }

    private static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        buffer.clear();
        buffer.put(f1).put(f2).put(f3).put(f4);
        buffer.flip();
        return buffer;
    }
}

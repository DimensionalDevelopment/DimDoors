package org.dimdev.dimdoors.client;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.VectorNi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
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
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoorQuartz;
import org.dimdev.dimdoors.shared.tileentities.TileEntityRift;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public final class DimensionalPortalRenderer {

    private static final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final ResourceLocation warpPath = new ResourceLocation(DimDoors.MODID + ":textures/other/warp.png");
    private static final PropertyBool openProperty = PropertyBool.create("open");
    private static final PropertyEnum<BlockDoor.EnumHingePosition> hingeProperty = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
    private static final PropertyDirection facingProperty = PropertyDirection.create("facing", Arrays.asList(EnumFacing.HORIZONTALS));

    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static final BlockModelShapes blockModelShapes = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes();
    private static final ModelManager modelManager = blockModelShapes.getModelManager();
    private static final BlockStateMapper blockStateMapper = blockModelShapes.getBlockStateMapper();

    private static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);

    // TODO: any render angle

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

        final float pathLength = 10;
        final float doorDistanceMul = 1.5f;
        final int endOpacity = 0;

        // incoming depth function is GL_LEQUAL
        // this means that you can redraw geometry multiple times as long as you don't translate.

        /*int glDepthFunc = glGetInteger(GL_DEPTH_FUNC);
        switch (glDepthFunc) {
            case GL_LESS:
                System.out.println("less");
                break;
            case GL_LEQUAL:
                System.out.println("lequal");
                break;
            case GL_GREATER:
                System.out.println("greater");
                break;
            case GL_GEQUAL:
                System.out.println("gequal");
                break;
        }*/

        //System.out.println("RENDER");
        //System.out.println(height);
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableTexture2D();

        GL30.glGenFramebuffers();

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
        final Vector3d left, right, back, depth, up;
        switch (orientation) {
            case NORTH:
                left = merged.add(width, 0, 0);//new Vector3d(x + width, y, z);
                right = merged;
                back = new Vector3d(0, 0, 1);
                break;
            case SOUTH:
                left = merged;
                right = merged.add(width, 0, 0);
                back = new Vector3d(0, 0, -1);
                break;
            case WEST:
                left = merged;
                right = merged.add(0, 0, width);
                back = new Vector3d(1, 0, 0);
                break;
            case EAST:
                left = merged.add(0, 0, width);
                right = merged;
                back = new Vector3d(-1, 0, 0);
                break;
            default:
                return;
        }
        depth = back.mul(pathLength);

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
        boolean personal = blockType instanceof BlockDimensionalDoorQuartz;


        if (personal) {
            final float brightness = 0.99f;
            voidColor = new Vector3f(brightness, brightness, brightness);
            pathColor = new Vector3i(255, 210, 0);
        } else {
            voidColor = new Vector3f(0, 0, 0);
            pathColor = new Vector3i(50, 255, 255);
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
        //TODO put blendfunc back to original state
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int oldShadeModel = GlStateManager.glGetInteger(GL_SHADE_MODEL);
        GlStateManager.shadeModel(GL_SMOOTH);

        GlStateManager.disableAlpha();
        /*worldRenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(left.getX(), left.getY(), left.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 127)
                .endVertex();
        worldRenderer.pos(right.getX(), right.getY(), right.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 127)
                .endVertex();
        worldRenderer.pos(rightBack.getX(), rightBack.getY(), rightBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity)
                .endVertex();
        worldRenderer.pos(leftBack.getX(), leftBack.getY(), leftBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity)
                .endVertex();
        tessellator.draw();*/

        // Modelled path ===============================================================================================

        // GlStateManager.blendFunc();

        GlStateManager.enableTexture2D();
        //GlStateManager.disableDepth();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        Block stonebrick = Blocks.STONEBRICK;
        IBlockState stonebrickState = stonebrick.getDefaultState();
        Map<IBlockState, ModelResourceLocation> stonebrickMap = blockStateMapper.getVariants(stonebrick);
        GlStateManager.matrixMode(GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.translate(pos.getX(), pos.getY() - 1, pos.getZ());
        VectorNi pathColorVec = createColorVec(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        int numPathTiles = 10;
        float deltaOp = 255f / numPathTiles;
        for (int i = 0; i < numPathTiles; i++) {
            int nearOp = 255 - (int) (i * deltaOp);
            int farOp = (int) (nearOp - deltaOp);
            VectorNi colorVec = new VectorNi(pathColorVec);
            setOpacity(colorVec, nearOp, nearOp, farOp, farOp);
            pathColorVec = rotateTopFaceColor(colorVec, orientation);
            drawState(tessellator, worldRenderer, stonebrickMap, stonebrickState, EnumFacing.UP, pathColorVec);
            GlStateManager.translate(back.getX(), 0, back.getZ());
        }


        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();

        worldRenderer.begin(GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(leftTop.getX(), leftTop.getY(), leftTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255).endVertex();
        worldRenderer.pos(leftTopBack.getX(), leftTopBack.getY(), leftTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity).endVertex();
        worldRenderer.pos(rightTop.getX(), rightTop.getY(), rightTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255).endVertex();
        worldRenderer.pos(rightTopBack.getX(), rightTopBack.getY(), rightTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity).endVertex();

        worldRenderer.pos(left.getX(), left.getY(), left.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255).endVertex();
        worldRenderer.pos(leftBack.getX(), leftBack.getY(), leftBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity).endVertex();
        worldRenderer.pos(right.getX(), right.getY(), right.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255).endVertex();
        worldRenderer.pos(rightBack.getX(), rightBack.getY(), rightBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity).endVertex();
        tessellator.draw();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (blockType instanceof BlockDoor) {
            //GlStateManager.disableDepth();
            //glDisable(GL_STENCIL_TEST);
            //GlStateManager.matrixMode(GL_MODELVIEW);
            GlStateManager.pushMatrix();

            GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());

            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);


            Map<IBlockState, ModelResourceLocation> doorMap = blockStateMapper.getVariants(blockType);
            IBlockState doorBottomState = tileEntity.getWorld().getBlockState(tileEntity.getPos());
            IBlockState doorTopState = tileEntity.getWorld().getBlockState(tileEntity.getPos().up());
            if (doorBottomState.getBlock() instanceof BlockDoor && doorTopState.getBlock() instanceof BlockDoor) {
                doorBottomState = doorBottomState.withProperty(hingeProperty, doorTopState.getValue(hingeProperty));
//                System.out.println();
//                System.out.println("---------------------------------------------");
//                System.out.println(doorBottomState.getProperties());
//                System.out.println("---------------------------------------------");
                doorTopState = doorTopState.withProperty(openProperty, doorBottomState.getValue(openProperty))
                        .withProperty(facingProperty, doorBottomState.getValue(facingProperty));
                //System.out.println(doorTopState);
                //System.out.println(doorBottomState);
                //System.out.println(doorBottomState.getProperties());

                //System.out.println();
                //System.out.println(doorBottomState.getValue(PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class)));
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.enableTexture2D();
                if (doorBottomState.getValue(openProperty)) {
                    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                    if (!personal) {
                        GlStateManager.enableTexture2D();
                        GlStateManager.enableLighting();
                    }

                    drawState(tessellator, worldRenderer, doorMap, doorBottomState, null);
                    GlStateManager.translate(0, 1, 0);
                    drawState(tessellator, worldRenderer, doorMap, doorTopState, null);
                    GlStateManager.translate(0, -1, 0);

                    GlStateManager.disableTexture2D();
                    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                    GlStateManager.disableLighting();
                }

                Vector3d doorDepth = depth.mul(doorDistanceMul);
                GlStateManager.translate(doorDepth.getX(), doorDepth.getY(), doorDepth.getZ());
                drawState(tessellator, worldRenderer, doorMap, doorBottomState.withProperty(openProperty, false), orientation);
                GlStateManager.translate(0, 1, 0);
                drawState(tessellator, worldRenderer, doorMap, doorTopState.withProperty(openProperty, false), orientation);

            }

            GlStateManager.popMatrix();


        }

        //GlStateManager.rotate(Quaternion);

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
                                  IBlockState blockState, EnumFacing side) {
        drawState(tessellator, worldRenderer, map, blockState, side, COLORLESS);
    }

    private static void drawState(Tessellator tessellator, BufferBuilder worldRenderer, Map<IBlockState, ModelResourceLocation> map,
                                  IBlockState blockState, EnumFacing side, VectorNi colors) {
        if (colors.size() < 16) colors = COLORLESS;
        ModelResourceLocation location;
        IBakedModel model;
        List<BakedQuad> quads;
        location = map.get(blockState);
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
                /*worldRenderer.putColorRGB_F(1, 0, 0, 4);
                worldRenderer.putColorRGB_F(1, 1, 0, 3);
                worldRenderer.putColorRGB_F(0, 1, 0, 2);
                worldRenderer.putColorRGB_F(0, 0.3f, 1, 1);*/

                for (int i = 0, j = 4; i < 16; i += 4, j--) {
                    int realIndex = worldRenderer.getColorIndex(j);
                    worldRenderer.putColorRGBA(realIndex, colors.get(i), colors.get(i + 1), colors.get(i + 2), colors.get(i + 3));
                }
            }
            tessellator.draw();
        }
    }


    private static VectorNi rotateTopFaceColor(VectorNi vec, EnumFacing facing) {
        VectorNi ret = new VectorNi(vec);
        if (facing == null) return ret;
        int shiftAmount;
        switch (facing) {
            case DOWN:
            case UP:
                return ret;
            case NORTH:
                shiftAmount = 4;
                break;
            case EAST:
                shiftAmount = 8;
                break;
            case SOUTH:
                shiftAmount = 12;
                break;
            case WEST:
                shiftAmount = 0;
                break;
            default:
                return ret;
        }
        for (int i = 0; i < 16; i++) {
            ret.set(i, vec.get((i + shiftAmount) & 15));
        }
        return ret;
    }

    private static void setOpacity(VectorNi vec, int op1, int op2, int op3, int op4) {
        if (vec.size() < 16) return;
        vec.set(3, op1);
        vec.set(7, op2);
        vec.set(11, op3);
        vec.set(15, op4);
    }

    private static VectorNi createColorVec(int red, int green, int blue, int alpha) {
        return new VectorNi(red, green, blue, alpha, red, green, blue, alpha, red, green, blue, alpha, red, green, blue, alpha);
    }

    private static FloatBuffer getBuffer(float f1, float f2, float f3, float f4) {
        buffer.clear();
        buffer.put(f1).put(f2).put(f3).put(f4);
        buffer.flip();
        return buffer;
    }
}

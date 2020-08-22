package org.dimdev.dimdoors.client;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.flowpowered.math.vector.VectorNi;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.mixin.DirectionAccessor;
import org.dimdev.dimdoors.util.RGBA;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import static org.lwjgl.opengl.GL11.*;

@Environment(EnvType.CLIENT)
public final class DimensionalPortalRenderer {
    private static final FloatBuffer BUFFER = GlAllocationUtils.allocateFloatBuffer(16);
    private static final Identifier WARP_PATH = new Identifier("dimdoors:textures/other/warp.png");
    private static final BooleanProperty OPEN_PROPERTY = BooleanProperty.of("open");
    private static final EnumProperty<DoorHinge> HINGE_PROPERTY = EnumProperty.of("hinge", DoorHinge.class);
    private static final DirectionProperty FACING_PROPERTY = DirectionProperty.of("facing", Arrays.asList(DirectionAccessor.getHorizontal()));

    private static final TextureManager TEXTURE_MANAGER = MinecraftClient.getInstance().getTextureManager();
    private static final BlockModels BLOCK_MODELS = MinecraftClient.getInstance().getBlockRenderManager().getModels();
    private static final BakedModelManager MODEL_MANAGER = BLOCK_MODELS.getModelManager();

    private static final VectorNi COLORLESS = new VectorNi(255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255);

    public static Identifier getWarpPath() {
        return WARP_PATH;
    }

    // TODO: any render angle

    /**
     * Renders a dimensional portal, for use in various situations. Code is mostly based
     * on vanilla's EndGatewayBlockEntityRenderer.
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
    public static void renderDimensionalPortal(double x, double y, double z, Direction orientation, double width, double height, RGBA[] colors) { // TODO: Make this work at any angle
        RenderSystem.disableLighting();
        RenderSystem.disableCull();

        for (int pass = 0; pass < 4; pass++) {
            RenderSystem.pushMatrix();

            float translationScale = 16 - pass;
            float scale = 0.2625F;
            float colorMultiplier = 1.0F / (translationScale + .80F);

            MinecraftClient.getInstance().getTextureManager().bindTexture(WARP_PATH);
            RenderSystem.enableBlend();

            if (pass == 0) {
                colorMultiplier = 0.1F;
                translationScale = 25.0F;
                scale = 0.125F;

                RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }

            if (pass == 1) {
                scale = 0.5F;
                RenderSystem.blendFunc(GL_ONE, GL_ONE);
            }

            double offset = Util.getMeasuringTimeNano() % 200000L / 200000.0F;
            RenderSystem.translated(offset, offset, offset);

            GlStateManager.texGenMode(GlStateManager.TexCoord.S, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.T, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.R, GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL_OBJECT_LINEAR);

            if (orientation == Direction.UP || orientation == Direction.DOWN) {
                GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL11.GL_EYE_LINEAR);
            } else {
                GlStateManager.texGenMode(GlStateManager.TexCoord.Q, GL11.GL_OBJECT_LINEAR);
            }

            switch (orientation) { // TODO: Why 0.15F? Is that a door's thickness? If yes, don't hardcode that.
                case SOUTH:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, -0.15F));
                    break;
                case WEST:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.15F));
                    break;
                case NORTH:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.15F));
                    break;
                case EAST:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, -0.15F));
                    break;
                case UP:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
                case DOWN:
                    GlStateManager.texGenParam(GlStateManager.TexCoord.S, GL11.GL_OBJECT_PLANE, getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.T, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.R, GL11.GL_OBJECT_PLANE, getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                    GlStateManager.texGenParam(GlStateManager.TexCoord.Q, GL11.GL_EYE_PLANE, getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
                    break;
            }

            GlStateManager.enableTexGen(GlStateManager.TexCoord.S);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.T);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.R);
            GlStateManager.enableTexGen(GlStateManager.TexCoord.Q);

            RenderSystem.popMatrix();

            RenderSystem.matrixMode(GL_TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translated(0.0F, offset * translationScale, 0.0F);
            RenderSystem.scaled(scale, scale, scale);
            RenderSystem.translatef(0.5F, 0.5F, 0.5F);
            RenderSystem.rotatef((pass * pass * 4321 + pass * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(0.5F, 0.5F, 0.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder worldRenderer = tessellator.getBuffer();
            worldRenderer.begin(GL_QUADS, VertexFormats.POSITION);

            RGBA color = colors[pass];
            RenderSystem.color4f(color.getRed() * colorMultiplier, color.getGreen() * colorMultiplier, color.getBlue() * colorMultiplier, color.getAlpha());

            switch (orientation) {
                case NORTH:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y + height, z);
                    worldRenderer.vertex(x + width, y + height, z);
                    worldRenderer.vertex(x + width, y, z);
                    break;
                case SOUTH:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x + width, y, z);
                    worldRenderer.vertex(x + width, y + height, z);
                    worldRenderer.vertex(x, y + height, z);
                    break;
                case WEST:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y, z + width);
                    worldRenderer.vertex(x, y + height, z + width);
                    worldRenderer.vertex(x, y + height, z);
                    break;
                case EAST:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y + height, z);
                    worldRenderer.vertex(x, y + height, z + width);
                    worldRenderer.vertex(x, y, z + width);
                    break;
                case UP:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x, y, z + width);
                    worldRenderer.vertex(x + width, y, z + width);
                    worldRenderer.vertex(x + width, y, z);
                    break;
                case DOWN:
                    worldRenderer.vertex(x, y, z);
                    worldRenderer.vertex(x + width, y, z);
                    worldRenderer.vertex(x + width, y, z + width);
                    worldRenderer.vertex(x, y, z + width);
                    break;
            }

            tessellator.draw();

            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL_MODELVIEW);
        }

        RenderSystem.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexCoord.S);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.T);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.R);
        GlStateManager.disableTexGen(GlStateManager.TexCoord.Q);
        RenderSystem.enableCull();
        RenderSystem.enableLighting();
    }

    //TODO Check to make sure block type is valid (and not air)
    @SuppressWarnings("Duplicates")
    public static void renderFoxWallAxis(RiftBlockEntity blockEntity, Vector3d pos, Vector3d offset, Direction orientation, double width, double height, RGBA[] colors) {
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
        RenderSystem.disableLighting();
        //RenderSystem.activeTexture(OpenGlHelper.lightmapTexUnit);
        RenderSystem.disableTexture();
        //RenderSystem.activeTexture(OpenGlHelper.defaultTexUnit);
        RenderSystem.disableTexture();

        GlStateManager.genFramebuffers();

        //RenderSystem.disableCull();
        //RenderSystem.disableDepth();


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

        Vector3f voidColor;
        Vector3i pathColor;
//        boolean personal = blockType instanceof BlockDimensionalDoorQuartz;


        final float brightness = 0.99f;
        voidColor = new Vector3f(brightness, brightness, brightness);
        pathColor = new Vector3i(255, 210, 0);

//        if (personal) {
//        } else {
//            voidColor = new Vector3f(0, 0, 0);
//            pathColor = new Vector3i(50, 255, 255);
//        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();
        RenderSystem.color4f(voidColor.getX(), voidColor.getY(), voidColor.getZ(), 1f);

        worldRenderer.begin(GL_QUADS, VertexFormats.POSITION);
        worldRenderer.vertex(left.getX(), left.getY(), left.getZ());
        worldRenderer.vertex(right.getX(), right.getY(), right.getZ());
        worldRenderer.vertex(rightTop.getX(), rightTop.getY(), rightTop.getZ());
        worldRenderer.vertex(leftTop.getX(), leftTop.getY(), leftTop.getZ());
        tessellator.draw();

        glStencilMask(0);
        glStencilFunc(GL_EQUAL, 1, 0xff);
        RenderSystem.disableDepthTest();

        RenderSystem.enableBlend();
        //TODO put blendfunc back to original state
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int oldShadeModel = GlStateManager.getInteger(GL_SHADE_MODEL);
        RenderSystem.shadeModel(GL_SMOOTH);

        RenderSystem.disableAlphaTest();
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

        // RenderSystem.blendFunc();

        RenderSystem.enableTexture();
        //RenderSystem.disableDepth();
        TEXTURE_MANAGER.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Block stonebrick = Blocks.STONE_BRICKS;
        BlockState stonebrickState = stonebrick.getDefaultState();
        Map<BlockState, ModelIdentifier> stonebrickMap = Maps.newHashMap();
        stonebrickMap.put(Blocks.STONE_BRICKS.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(Blocks.STONE_BRICKS).toString()));
        stonebrickMap.put(Blocks.CRACKED_STONE_BRICKS.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(Blocks.CRACKED_STONE_BRICKS).toString()));
        stonebrickMap.put(Blocks.CHISELED_STONE_BRICKS.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(Blocks.CHISELED_STONE_BRICKS).toString()));
        RenderSystem.matrixMode(GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.translated(pos.getX(), pos.getY() - 1, pos.getZ());
        VectorNi pathColorVec = createColorVec(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        int numPathTiles = 10;
        float deltaOp = 255f / numPathTiles;
        for (int i = 0; i < numPathTiles; i++) {
            int nearOp = 255 - (int) (i * deltaOp);
            int farOp = (int) (nearOp - deltaOp);
            VectorNi colorVec = new VectorNi(pathColorVec);
            setOpacity(colorVec, nearOp, nearOp, farOp, farOp);
            pathColorVec = rotateTopFaceColor(colorVec, orientation);
            drawState(tessellator, worldRenderer, stonebrickMap, stonebrickState, Direction.UP, pathColorVec);
            RenderSystem.translated(back.getX(), 0, back.getZ());
        }


        RenderSystem.popMatrix();
        RenderSystem.disableTexture();

        worldRenderer.begin(GL_LINES, VertexFormats.POSITION_COLOR);
        worldRenderer.vertex(leftTop.getX(), leftTop.getY(), leftTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        worldRenderer.vertex(leftTopBack.getX(), leftTopBack.getY(), leftTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity);
        worldRenderer.vertex(rightTop.getX(), rightTop.getY(), rightTop.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        worldRenderer.vertex(rightTopBack.getX(), rightTopBack.getY(), rightTopBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity);

        worldRenderer.vertex(left.getX(), left.getY(), left.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        worldRenderer.vertex(leftBack.getX(), leftBack.getY(), leftBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity);
        worldRenderer.vertex(right.getX(), right.getY(), right.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), 255);
        worldRenderer.vertex(rightBack.getX(), rightBack.getY(), rightBack.getZ())
                .color(pathColor.getX(), pathColor.getY(), pathColor.getZ(), endOpacity);
        tessellator.draw();

        RenderSystem.enableAlphaTest();
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        {
            //RenderSystem.disableDepth();
            //glDisable(GL_STENCIL_TEST);
            //RenderSystem.matrixMode(GL_MODELVIEW);
            RenderSystem.pushMatrix();

            RenderSystem.translated(pos.getX(), pos.getY(), pos.getZ());

            TEXTURE_MANAGER.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);


            Map<BlockState, ModelIdentifier> doorMap = Maps.newHashMap();
            doorMap.put(ModBlocks.GOLD_DIMENSIONAL_DOOR.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(ModBlocks.GOLD_DIMENSIONAL_DOOR).toString()));
            doorMap.put(ModBlocks.QUARTZ_DIMENSIONAL_DOOR.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(ModBlocks.QUARTZ_DIMENSIONAL_DOOR).toString()));
            doorMap.put(ModBlocks.OAK_DIMENSIONAL_DOOR.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(ModBlocks.OAK_DIMENSIONAL_DOOR).toString()));
            doorMap.put(ModBlocks.GOLD_DIMENSIONAL_DOOR.getDefaultState(), new ModelIdentifier(Registry.BLOCK.getId(ModBlocks.GOLD_DIMENSIONAL_DOOR).toString()));
            BlockState doorBottomState = blockEntity.getWorld().getBlockState(blockEntity.getPos());
            BlockState doorTopState = blockEntity.getWorld().getBlockState(blockEntity.getPos().up());
            if (doorBottomState.getBlock() instanceof DoorBlock && doorTopState.getBlock() instanceof DoorBlock) {
                doorBottomState = doorBottomState.with(HINGE_PROPERTY, doorTopState.get(HINGE_PROPERTY));
//                System.out.println();
//                System.out.println("---------------------------------------------");
//                System.out.println(doorBottomState.getProperties());
//                System.out.println("---------------------------------------------");
                doorTopState = doorTopState.with(OPEN_PROPERTY, doorBottomState.get(OPEN_PROPERTY))
                        .with(FACING_PROPERTY, doorBottomState.get(FACING_PROPERTY));
                //System.out.println(doorTopState);
                //System.out.println(doorBottomState);
                //System.out.println(doorBottomState.getProperties());

                //System.out.println();
                //System.out.println(doorBottomState.getValue(PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class)));
                //RenderSystem.activeTexture(OpenGlHelper.defaultTexUnit);
                RenderSystem.enableTexture();
                if (doorBottomState.get(OPEN_PROPERTY)) {
                    //RenderSystem.activeTexture(OpenGlHelper.lightmapTexUnit);
//                    if (!personal) {
//                        RenderSystem.enableTexture2D();
//                        RenderSystem.enableLighting();
//                    }

                    drawState(tessellator, worldRenderer, doorMap, doorBottomState, null);
                    RenderSystem.translatef(0, 1, 0);
                    drawState(tessellator, worldRenderer, doorMap, doorTopState, null);
                    RenderSystem.translatef(0, -1, 0);

                    RenderSystem.disableTexture();
                    //RenderSystem.activeTexture(OpenGlHelper.defaultTexUnit);
                    RenderSystem.disableLighting();
                }

                Vector3d doorDepth = depth.mul(doorDistanceMul);
                RenderSystem.translated(doorDepth.getX(), doorDepth.getY(), doorDepth.getZ());
                drawState(tessellator, worldRenderer, doorMap, doorBottomState.with(OPEN_PROPERTY, false), orientation);
                RenderSystem.translated(0, 1, 0);
                drawState(tessellator, worldRenderer, doorMap, doorTopState.with(OPEN_PROPERTY, false), orientation);

            }

            RenderSystem.popMatrix();


        }

        //RenderSystem.rotate(Quaternion);

        glStencilMask(0xff);
        glDisable(GL_STENCIL_TEST);

        RenderSystem.shadeModel(oldShadeModel);
        RenderSystem.disableBlend();
        //RenderSystem.activeTexture(OpenGlHelper.defaultTexUnit);
        RenderSystem.enableTexture();
        //RenderSystem.activeTexture(OpenGlHelper.lightmapTexUnit);
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
    }

    private static void drawState(Tessellator tessellator, BufferBuilder worldRenderer, Map<BlockState, ModelIdentifier> map,
                                  BlockState blockState, Direction side) {
        drawState(tessellator, worldRenderer, map, blockState, side, COLORLESS);
    }

    private static void drawState(Tessellator tessellator, BufferBuilder bufferBuilder, Map<BlockState, ModelIdentifier> map,
                                  BlockState blockState, Direction side, VectorNi colors) {
        if (colors.size() < 16) colors = COLORLESS;
        ModelIdentifier location;
        BakedModel model;
        List<BakedQuad> quads;
        location = map.get(blockState);
        model = MODEL_MANAGER.getModel(location);
        quads = model.getQuads(null, side, new Random(1));
        if (!quads.isEmpty()) {
            bufferBuilder.begin(GL_QUADS, VertexFormats.POSITION);
            //System.out.println(quads.size());
            for (BakedQuad quad : quads) {
                //worldRenderer.(quad.getVertexData());
                /*for (int i = 1; i <5 ; i++) {
                    worldRenderer.putColorMultiplier(1, 0, 0, i);
                }*/
                /*worldRenderer.putColorRGB_F(1, 0, 0, 4);
                worldRenderer.putColorRGB_F(1, 1, 0, 3);
                worldRenderer.putColorRGB_F(0, 1, 0, 2);
                worldRenderer.putColorRGB_F(0, 0.3f, 1, 1);*/

                for (int i = 0, j = 4; i < 16; i += 4, j--) {
                    bufferBuilder.color(colors.get(i), colors.get(i + 1), colors.get(i + 2), colors.get(i + 3));
                }
            }
            tessellator.draw();
        }
    }


    private static VectorNi rotateTopFaceColor(VectorNi vec, Direction facing) {
        VectorNi ret = new VectorNi(vec);
        if (facing == null) return ret;
        int shiftAmount;
        switch (facing) {
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
        BUFFER.clear();
        BUFFER.put(f1).put(f2).put(f3).put(f4);
        BUFFER.flip();
        return BUFFER;
    }
}
package org.dimdev.dimdoors.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.limlib.client.FabricClientSided;
import org.dimdev.limlib.client.IDimensionSpecialEffectExtension;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;


public class DimensionalDoorsClientFabric extends FabricClientSided<DimensionalDoorsClientFabric, DimensionalDoorsClient> implements IDimDoorsClientSided<DimensionalDoorsClientFabric> {
    public DimensionalDoorsClientFabric() {
        super(DimensionalDoorsClient.INSTANCE);
    }

    @Override
    public void onInitializeClient() {
        super.onInitializeClient();

        var binding = KeyBindingHelper.registerKeyBinding(new KeyMapping("box_test_toggle", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UP, "derp"));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (binding.consumeClick()) {
                DimensionalDoorsClient.INSTANCE.boxDebug = !DimensionalDoorsClient.INSTANCE.boxDebug;

                client.player.displayClientMessage(Component.literal("Key 1 was pressed!"), false);
            }
        });
        WorldRenderEvents.LAST.register(DimensionalDoorsClientFabric::renderPocketFogTestBox);

        RecipeBookManager.init();
        checkCompat();
    }

    private static void renderPocketFogTestBox(WorldRenderContext context) {
        PoseStack matrices = context.matrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        var area = ClientPacketListener.getArea();
        if (matrices == null) {
            return;
        }

        if (minecraft.level == null) {
            return;
        }

        if (area == null) return;

        Vec3 camera = context.camera().getPosition();
        AABB box = AABB.of(area)
                .inflate(0.02D)
                .move(-camera.x, -camera.y, -camera.z);

        float oldLineWidth = RenderSystem.getShaderLineWidth();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(3.0F);

        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        renderLineBox(matrices, buffer, box, 255, 64, 32, 255);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.lineWidth(oldLineWidth);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    private static void renderLineBox(PoseStack matrices, BufferBuilder buffer, AABB box, int red, int green, int blue, int alpha) {
        addLine(matrices, buffer, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
        addLine(matrices, buffer, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    private static void addLine(PoseStack matrices, BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2, int red, int green, int blue, int alpha) {
        buffer.addVertex(matrices.last().pose(), (float) x1, (float) y1, (float) z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrices.last().pose(), (float) x2, (float) y2, (float) z2).setColor(red, green, blue, alpha);
    }

    @Override
    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return () -> ClassTinkerers.getEnum(RecipeBookCategories.class, name);
    }

    @Override
    public VoidDimensionSpecialEffects createVoidEffect(DimensionEffect effect) {
        return new FabricVoidDimensionSpecialEffects(effect);
    }

    private static class FabricVoidDimensionSpecialEffects extends VoidDimensionSpecialEffects implements IDimensionSpecialEffectExtension {
        private final DimensionEffect effect;

        public FabricVoidDimensionSpecialEffects(DimensionEffect effect) {
            super();
            this.effect = effect;
        }

        @Override
        public boolean extRenderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
            return effect.renderSky(level, ticks, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
        }

        @Override
        public boolean extRenderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
            return effect.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix);
        }

        @Override
        public boolean extRenderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return effect.renderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        }
    }
}

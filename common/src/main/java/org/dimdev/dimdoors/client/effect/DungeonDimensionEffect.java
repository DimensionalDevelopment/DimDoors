package org.dimdev.dimdoors.client.effect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.pocket.type.addon.SkyAddon;
import org.joml.Matrix4f;

import java.util.List;

public class DungeonDimensionEffect extends DimensionSpecialEffects implements DimensionSpecialEffectsExtensions {
    public static DungeonDimensionEffect INSTANCE = new DungeonDimensionEffect();
    private DungeonDimensionEffect() {
        super(-30, false, SkyType.NONE, false, true);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 fogColor, float brightness) {
        return fogColor;
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        ClientLevel world = level;
        List<SkyAddon> skyAddons = PocketListenerUtil.applicableAddonsClient(SkyAddon.class, world, camera.getBlockPosition());
        SkyAddon skyAddon = null;
        if (skyAddons.size() > 0) {
            // There should really only be one of these.
            // If anyone needs to use multiple SkyAddons then go ahead and change this.
            skyAddon = skyAddons.get(0);
        }

        if (skyAddon != null) {
            ResourceKey<Level> key = skyAddon.getWorld();

//            DimensionRenderingRegistry.SkyRenderer skyRenderer = DimensionRenderingRegistry.getSkyRenderer(key);

//            if (skyRenderer != null) {
//                skyRenderer.render(context);
//            } else {

//                if (key.equals(Level.END)) {
//                    Minecraft.getInstance().gameRenderer.getMinecraft().levelRenderer.renderEndSky(poseStack);
//                }
//            }
        }

        return true;
    }

    @Override
    public boolean renderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix) {
        return true;
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        return true;
    }
}

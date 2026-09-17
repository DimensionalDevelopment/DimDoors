package org.dimdev.dimdoors.client.effect;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.biome.Biome;
import org.dimdev.dimdoors.client.CloudRenderBuffer;
import org.lwjgl.system.NonnullDefault;

public interface LevelRendererExtension extends CloudRenderBuffer {
    void renderWeather(LightTexture lightTexture, float partialTick, int ticks, double camX, double camY, double camZ, @NonnullDefault Biome.Precipitation precipitation, float rainStrength);

    boolean isMobEffectBlockingSky(Camera camera);

    int getTicks();
}

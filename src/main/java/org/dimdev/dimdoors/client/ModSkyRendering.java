package org.dimdev.dimdoors.client;

import io.github.waterpicker.openworlds.OpenWorlds;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.util.math.Vec3i;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModSkyRendering {
    public static void initClient() {
        OpenWorlds.registerSkyRenderer(ModDimensions.POCKET_TYPE_KEY, new CustomSkyProvider(null, null, new Vec3i(0, 0, 0)));
        OpenWorlds.registerSkyRenderer(ModDimensions.LIMBO_TYPE_KEY, new LimboSkyProvider());
        OpenWorlds.registerCloudRenderer(ModDimensions.LIMBO_TYPE_KEY, (minecraftClient, matrixStack, v, v1, v2, v3) -> {
        });
        OpenWorlds.registerSkyProperty(ModDimensions.LIMBO_TYPE_KEY, new LimboSkyProperties());
    }
}

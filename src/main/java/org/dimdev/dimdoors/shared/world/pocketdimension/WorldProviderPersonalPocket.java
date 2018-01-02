package org.dimdev.dimdoors.shared.world.pocketdimension;

import net.minecraft.world.DimensionType;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.ModBiomes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPersonalPocket extends WorldProviderPocket {

    @Override
    public void init() {
        super.init();
        biomeProvider = new BiomeProviderSingle(ModBiomes.WHITE_VOID);
    }

    // TODO: disable this to allow dark places in public pockets
    @Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            lightBrightnessTable[i] = 1;
        }
    }

    @Override public DimensionType getDimensionType() {
        return ModDimensions.PRIVATE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return new Vec3d(1, 1, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return new Vec3d(1, 1, 1);
    }
}

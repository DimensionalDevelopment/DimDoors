package org.dimdev.dimdoors.world.pocketdimension;

import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.sound.ModSoundEvents;
import org.dimdev.dimdoors.world.ModBiomes;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.pocketlib.PocketWorldDimension;

public class PersonalPocketDimension extends PocketWorldDimension {
    @SideOnly(Side.CLIENT)
    private static MusicTicker.MusicType music;

    static {
        if (DimDoors.proxy.isClient()) {
            music = EnumHelper.addEnum(MusicTicker.MusicType.class, "limbo", new Class<?>[]{SoundEvent.class, int.class, int.class}, ModSoundEvents.WHITE_VOID, 0, 0);
        }
    }

    public PersonalPocketDimension(World world, DimensionType dimensionType) {

    }

    @Override
    public void init() {
        super.init();
        hasSkyLight = false; // TODO: figure out why relighting takes so long with private pockets only...
        biomeProvider = new BiomeProviderSingle(ModBiomes.WHITE_VOID);
    }

    // TODO: disable this to allow dark places in public pockets
    @Override
    protected void generateLightBrightnessTable() {
        for (int i = 0; i <= 15; ++i) {
            lightBrightnessTable[i] = 1;
        }
    }

    @Override
    public DimensionType getDimensionType() {
        return ModDimensions.PERSONAL;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        return new Vec3d(0.99, 0.99, 0.99); // https://bugs.mojang.com/projects/MC/issues/MC-123703
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return new Vec3d(0.99, 0.99, 0.99); // https://bugs.mojang.com/projects/MC/issues/MC-123703
    }

    @Override
    @SideOnly(Side.CLIENT)
    public MusicTicker.MusicType getMusicType() {
        return music;
    }
}

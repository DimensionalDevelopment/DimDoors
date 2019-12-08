package org.dimdev.dimdoors.world.pocketdimension;

import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.util.render.CloudRenderBlank;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.ModBiomes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.BiomeProviderSingle;
import org.dimdev.pocketlib.PocketWorldDimension;

public class DungeonPocketDimension extends PocketWorldDimension {
    public DungeonPocketDimension(World world, DimensionType dimensionType) {

    }

    @Override
    public void init() {
        super.init();
        biomeProvider = new BiomeProviderSingle(ModBiomes.DANGEROUS_BLACK_VOID);
    }

    @Override
    public int getRespawnDimension(EntityPlayerMP player) {
        return ModDimensions.LIMBO.getId();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Vec3d getSkyColor(Entity cameraEntity, float partialTicks) {
        setCloudRenderer(new CloudRenderBlank());
        return Vec3d.ZERO;
    }

    @Override public DimensionType getDimensionType() {
        return ModDimensions.DUNGEON;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(float celestialAngle, float partialTicks) {
        return Vec3d.ZERO;
    }
}

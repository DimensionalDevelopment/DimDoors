package org.dimdev.pocketlib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.util.Location;

public class VirtualLocation {
    @Saved public final World world;
    @Saved public final int x;
    @Saved public final int z;
    @Saved public final int depth;

    public VirtualLocation(World world, int x, int z, int depth) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.depth = depth;
    }

    public void fromTag(CompoundTag nbt) {
        AnnotatedNbt.load(this, nbt);
    }

    public CompoundTag toTag(CompoundTag nbt) {
        AnnotatedNbt.save(this, nbt);
        return nbt;
    }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;

        if (location.world.dimension instanceof PocketWorldDimension) {
            Pocket pocket = PocketRegistry.instance(location.world).getPocketAt(location.pos);
            if (pocket != null) {
                virtualLocation = pocket.virtualLocation; // TODO: pockets-relative coordinates
            } else {
                virtualLocation = null; // TODO: door was placed in a pockets dim but outside of a pockets...
            }
        } else if (location.world.dimension instanceof LimboDimension) { // TODO: convert to interface on worldprovider
            virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), ModConfig.DUNGEONS.maxDungeonDepth);
        } // TODO: nether coordinate transform

        if (virtualLocation == null) {
            return new VirtualLocation(location.world.getServer().getWorld(DimensionType.OVERWORLD), location.getX(), location.getZ(), 5);
        }

        return virtualLocation;
    }

    public Location projectToWorld(boolean acceptLimbo) {
        World world = this.world;

        if (!acceptLimbo && world.dimension instanceof LimboDimension) {
            world = world.getServer().getWorld(DimensionType.OVERWORLD);
        }

        float spread = ModConfig.GENERAL.depthSpreadFactor * depth;
        int newX = (int) (x + spread * 2 * (Math.random() - 0.5));
        int newZ = (int) (z + spread * 2 * (Math.random() - 0.5));
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 0, newZ));
        return new Location(world, pos);
    }
}

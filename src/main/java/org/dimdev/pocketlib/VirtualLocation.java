package org.dimdev.pocketlib;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.ModConfig;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.util.Location;

import static net.minecraft.world.World.OVERWORLD;

public class VirtualLocation {
    @Saved public final ServerWorld world;
    @Saved public final int x;
    @Saved public final int z;
    @Saved public final int depth;

    public VirtualLocation(ServerWorld world, int x, int z, int depth) {
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

        if (ModDimensions.isDimDoorsPocketDimension(location.world)) {
            Pocket pocket = PocketRegistry.instance(location.world).getPocketAt(location.pos);
            if (pocket != null) {
                virtualLocation = pocket.virtualLocation; // TODO: pockets-relative coordinates
            } else {
                virtualLocation = null; // TODO: door was placed in a pockets dim but outside of a pockets...
            }
        } else if (ModDimensions.isLimboDimension(location.world)) { // TODO: convert to interface on worldprovider
            virtualLocation = new VirtualLocation(location.world, location.getX(), location.getZ(), ModConfig.DUNGEONS.maxDungeonDepth);
        } // TODO: nether coordinate transform

        if (virtualLocation == null) {
            return new VirtualLocation(location.world.getServer().getWorld(OVERWORLD), location.getX(), location.getZ(), 5);
        }

        return virtualLocation;
    }

    public Location projectToWorld(boolean acceptLimbo) {
        ServerWorld world = this.world;

        if (!acceptLimbo && ModDimensions.isLimboDimension(world)) {
            world = world.getServer().getWorld(OVERWORLD);
        }

        float spread = ModConfig.GENERAL.depthSpreadFactor * depth;
        int newX = (int) (x + spread * 2 * (Math.random() - 0.5));
        int newZ = (int) (z + spread * 2 * (Math.random() - 0.5));
        BlockPos pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(newX, 0, newZ));
        return new Location(world, pos);
    }
}

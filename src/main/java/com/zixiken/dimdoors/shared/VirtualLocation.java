package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import ddutils.Location;
import ddutils.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

@Value @ToString @AllArgsConstructor @Builder(toBuilder = true)
public class VirtualLocation { // TODO: use BlockPos/Location
    Location location;
    int depth;

    public VirtualLocation(int dim, BlockPos pos, int depth) {
        this(new Location(dim, pos), depth);
    }

    public VirtualLocation(int dim, int x, int y, int z, int depth) {
        this(new Location(dim, x, y, z), depth);
    }

    public int getDim() { return location.getDim(); }
    public BlockPos getPos() { return location.getPos(); }
    public int getX() { return location.getX(); }
    public int getY() { return location.getY(); }
    public int getZ() { return location.getZ(); }

    public static VirtualLocation readFromNBT(NBTTagCompound nbt) {
        int dim = nbt.getInteger("dim");
        int x = nbt.getInteger("x");
        int y = nbt.getInteger("y");
        int z = nbt.getInteger("z");
        int depth = nbt.getInteger("depth");
        return new VirtualLocation(dim, x, y, z, depth);
    }

    public NBTTagCompound writeToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("dim", location.getDim());
        nbt.setInteger("x", location.getPos().getX());
        nbt.setInteger("y", location.getPos().getY());
        nbt.setInteger("z", location.getPos().getZ());
        nbt.setInteger("depth", depth);
        return nbt;
    }

    public static VirtualLocation fromLocation(Location location) { // TODO: reverse function too
        VirtualLocation virtualLocation = null;
        if (DimDoorDimensions.isPocketDimension(location.getDim())) {
            Pocket pocket = PocketRegistry.getForDim(location.getDim()).getPocketFromLocation(location.getPos().getY(), location.getPos().getY(), location.getPos().getZ());
            if (pocket != null) {
                virtualLocation = pocket.getVirtualLocation();
            } else {
                virtualLocation = new VirtualLocation(0, 0, 0, 0, 0); // TODO: door was placed in a pocket dim but outside of a pocket...
            }
        }
        if (virtualLocation == null) {
            virtualLocation = new VirtualLocation(WorldUtils.getDim(location.getWorld()), location.getPos().getX(), location.getPos().getY(), location.getPos().getZ(), 0);
        }
        return virtualLocation;
    }

    public VirtualLocation transformDepth(int depth) { // TODO: Config option for block ratio between depths (see video of removed features)
        Random random = new Random();
        int depthDiff = Math.abs(this.depth - depth);
        int base = DDConfig.getOwCoordinateOffsetBase();
        double power = DDConfig.getOwCoordinateOffsetPower();
        int xOffset = random.nextInt((int) Math.pow(base * depthDiff, power)) * (random.nextBoolean() ? 1 : -1);
        int zOffset = random.nextInt((int) Math.pow(base * depthDiff, power)) * (random.nextBoolean() ? 1 : -1);
        return new VirtualLocation(getDim(), getPos().offset(EnumFacing.EAST, xOffset).offset(EnumFacing.SOUTH, zOffset), depth);
    }

    public Location projectToWorld() {
        return transformDepth(0).getLocation();
    }
}

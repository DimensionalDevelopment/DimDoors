package org.dimdev.dimdoors.shared;

import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.world.DimDoorDimensions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;

import java.util.Random;

@Value @ToString @AllArgsConstructor @Builder(toBuilder = true)
public class VirtualLocation {
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

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;
        if (DimDoorDimensions.isPocketDimension(location.getDim())) {
            Pocket pocket = PocketRegistry.getForDim(location.getDim()).getPocketAt(location.getPos());
            if (pocket != null) {
                virtualLocation = pocket.getVirtualLocation(); // TODO: pocket-relative coordinates
            } else {
                virtualLocation = new VirtualLocation(0, 0, 0, 0, 0); // TODO: door was placed in a pocket dim but outside of a pocket...
            }
        } else if (location.getWorld().provider instanceof WorldProviderLimbo) {
            virtualLocation = new VirtualLocation(location, DDConfig.getMaxDungeonDepth());
        }
        if (virtualLocation == null) {
            virtualLocation = new VirtualLocation(location, 0);
        }
        return virtualLocation;
    }

    // TODO: world-seed based transformations and pocket selections
    public VirtualLocation transformDepth(int depth) { // TODO: Config option for block ratio between depths (see video of removed features)
        Random random = new Random();
        int depthDiff = Math.abs(this.depth - depth);
        int base = DDConfig.getOwCoordinateOffsetBase();
        double power = DDConfig.getOwCoordinateOffsetPower();
        int xOffset = random.nextInt((int) Math.pow(base * (depthDiff + 1), power)) * (random.nextBoolean() ? 1 : -1);
        int zOffset = random.nextInt((int) Math.pow(base * (depthDiff + 1), power)) * (random.nextBoolean() ? 1 : -1);
        return new VirtualLocation(getDim(), getPos().offset(EnumFacing.EAST, xOffset).offset(EnumFacing.SOUTH, zOffset), depth);
    }

    public VirtualLocation randomTransformDepth() {
        float r = new Random().nextFloat();
        int newDepth;
        if (r > 0.9) { // TODO: per-rift probabilities
            newDepth = depth - 1;
        } else if (r > 0.75) {
            newDepth = depth;
        } else {
            newDepth = depth + 1;
        }
        if (newDepth < 1) newDepth = 1;
        return transformDepth(newDepth);
    }

    public Location projectToWorld() {
        return transformDepth(0).location;
    }
}

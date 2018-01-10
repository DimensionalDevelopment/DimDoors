package org.dimdev.dimdoors.shared;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.ddutils.Location;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import org.dimdev.dimdoors.shared.world.limbodimension.WorldProviderLimbo;

/*@Value*/ @ToString @AllArgsConstructor @NoArgsConstructor @Builder(toBuilder = true)
@NBTSerializable public class VirtualLocation implements INBTStorable { // TODO: fix AnnotatedNBT and rename this class back to VirtualLocation
    @Saved @Getter protected int dim;
    @Saved @Getter protected int x;
    @Saved @Getter protected int z;
    @Saved @Getter protected int depth; // TODO: convert to doubles

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;
        if (ModDimensions.isDimDoorsPocketDimension(location.getDim())) {
            Pocket pocket = PocketRegistry.getForDim(location.getDim()).getPocketAt(location.getPos());
            if (pocket != null) {
                virtualLocation = pocket.getVirtualLocation(); // TODO: pocket-relative coordinates
            } else {
                virtualLocation = new VirtualLocation(0, 0, 0, 0); // TODO: door was placed in a pocket dim but outside of a pocket...
            }
        } else if (location.getWorld().provider instanceof WorldProviderLimbo) {
            virtualLocation = new VirtualLocation(location.getDim(), location.getX(), location.getZ(), Config.getMaxDungeonDepth());
        }
        if (virtualLocation == null) {
            virtualLocation = new VirtualLocation(location.getDim(), location.getX(), location.getZ(), 0);
        }
        return virtualLocation;
    }

    /*// TODO: world-seed based transformations and pocket selections
    public VirtualLocation transformDepth(int depth) { // TODO: Config option for block ratio between depths (see video of removed features)
        Random random = new Random();
        int depthDiff = Math.abs(this.depth - depth);
        int base = Config.getOwCoordinateOffsetBase();
        double power = Config.getOwCoordinateOffsetPower();
        int xOffset = random.nextInt((int) Math.pow(base * (depthDiff + 1), power)) * (random.nextBoolean() ? 1 : -1);
        int zOffset = random.nextInt((int) Math.pow(base * (depthDiff + 1), power)) * (random.nextBoolean() ? 1 : -1);
        return new VirtualLocation(getDim(), getPos().offset(EnumFacing.EAST, xOffset).offset(EnumFacing.SOUTH, zOffset), depth);
    }*/
}

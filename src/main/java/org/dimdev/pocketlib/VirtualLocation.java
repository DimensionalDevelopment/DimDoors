package org.dimdev.pocketlib;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.shared.ModConfig;
import org.dimdev.dimdoors.shared.world.limbo.WorldProviderLimbo;

/*@Value*/ @ToString @AllArgsConstructor @NoArgsConstructor @Builder(toBuilder = true)
@NBTSerializable public class VirtualLocation implements INBTStorable {
    @Saved @Getter protected int dim;
    @Saved @Getter protected int x;
    @Saved @Getter protected int z;
    @Saved @Getter protected int depth; // TODO: convert to doubles

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    public static VirtualLocation fromLocation(Location location) {
        VirtualLocation virtualLocation = null;
        if (location.getWorld().provider instanceof WorldProviderPocket) {
            Pocket pocket = PocketRegistry.instance(location.getDim()).getPocketAt(location.getPos());
            if (pocket != null) {
                virtualLocation = pocket.getVirtualLocation(); // TODO: pockets-relative coordinates
            } else {
                virtualLocation = null; // TODO: door was placed in a pockets dim but outside of a pockets...
            }
        } else if (location.getWorld().provider instanceof WorldProviderLimbo) { // TODO: convert to interface on worldprovider
            virtualLocation = new VirtualLocation(location.getDim(), location.getX(), location.getZ(), ModConfig.dungeons.maxDungeonDepth);
        } // TODO: nether coordinate transform
        if (virtualLocation == null) {
            virtualLocation = new VirtualLocation(0, location.getX(), location.getZ(), 5); // TODO
        }
        return virtualLocation;
    }

    public Location projectToWorld(boolean limboConsideredWorld) {
        World world = WorldUtils.getWorld(dim);
        if (!limboConsideredWorld && world.provider instanceof WorldProviderLimbo) {
            world = WorldUtils.getWorld(0);
        }
        float spread = ModConfig.general.depthSpreadFactor * depth; // TODO: gaussian spread, handle air-filled/pocket world
        int newX = (int) (x + spread * 2 * (Math.random() - 0.5));
        int newZ = (int) (z + spread * 2 * (Math.random() - 0.5));
        BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(newX, 0, newZ));
        return new Location(world, pos);
    }
}

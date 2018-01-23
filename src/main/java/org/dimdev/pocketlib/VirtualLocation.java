package org.dimdev.pocketlib;

import lombok.*;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
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
                virtualLocation = pocket.getVirtualLocation(); // TODO: pocket-relative coordinates
            } else {
                virtualLocation = null; // TODO: door was placed in a pocket dim but outside of a pocket...
            }
        } else if (location.getWorld().provider instanceof WorldProviderLimbo) { // TODO: convert to interface on worldprovider
            virtualLocation = new VirtualLocation(location.getDim(), location.getX(), location.getZ(), ModConfig.dungeon.maxDungeonDepth);
        } // TODO: nether coordinate transform
        if (virtualLocation == null) {
            virtualLocation = new VirtualLocation(0, location.getX(), location.getZ(), 5); // TODO
        }
        return virtualLocation;
    }
}

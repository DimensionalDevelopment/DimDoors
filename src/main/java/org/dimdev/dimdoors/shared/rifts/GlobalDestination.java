package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class GlobalDestination extends RiftDestination { // TODO: location directly in nbt like minecraft?
    private Location loc;

    public GlobalDestination() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        loc = Location.readFromNBT(nbt.getCompoundTag("loc"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        nbt.setTag("loc", Location.writeToNBT(loc));
        return nbt;
    }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        ((TileEntityRift) loc.getTileEntity()).teleportTo(entity);
        return true;
    }

    @Override
    public Location getReferencedRift(Location rift) {
        return loc;
    }
}

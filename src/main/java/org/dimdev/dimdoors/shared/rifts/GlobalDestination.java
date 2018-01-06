package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@SavedToNBT public class GlobalDestination extends RiftDestination { // TODO: location directly in nbt like minecraft?
    @SavedToNBT @Getter protected Location loc;

    public GlobalDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

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

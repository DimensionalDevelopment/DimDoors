package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.NBTUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class LocalDestination extends RiftDestination { // TODO: use BlockPos
    @Saved protected BlockPos pos;

    public LocalDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        ((TileEntityRift) rift.getWorld().getTileEntity(pos)).teleportTo(entity);
        return true;
    }

    @Override
    public Location getReferencedRift(Location rift) {
        return new Location(rift.getDim(), pos);
    }
}

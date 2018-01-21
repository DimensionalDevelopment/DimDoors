package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RotatedLocation;
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
    @Saved protected BlockPos target;

    public LocalDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        ((TileEntityRift) loc.getLocation().getWorld().getTileEntity(target)).teleportTo(entity, loc.getYaw(), loc.getPitch());
        return true;
    }

    @Override
    public Location getFixedTarget(Location location) {
        return new Location(location.getDim(), target);
    }
}

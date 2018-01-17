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
import net.minecraft.util.math.Vec3i;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class RelativeDestination extends RiftDestination { // TODO: use Vec3i
    @Saved protected Vec3i offset;

    public RelativeDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        ((TileEntityRift) loc.getLocation().getWorld().getTileEntity(loc.getLocation().getPos().add(offset))).teleportTo(entity, loc.getPitch(), loc.getYaw());
        return true;
    }

    @Override
    public Location getFixedTarget(Location location) {
        return new Location(location.getDim(), location.getPos().add(offset));
    }
}

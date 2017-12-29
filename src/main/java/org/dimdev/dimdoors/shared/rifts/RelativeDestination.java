package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.nbt.NBTUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3i;
import org.dimdev.ddutils.nbt.SavedToNBT;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@SavedToNBT public class RelativeDestination extends RiftDestination { // TODO: use Vec3i
    @SavedToNBT /*package-private*/ Vec3i offset;

    public RelativeDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        rift.getWorld().getTileEntity(rift.getPos().add(offset));
        return true;
    }

    @Override
    public Location getReferencedRift(Location rift) {
        return new Location(rift.getDim(), rift.getPos().add(offset));
    }
}

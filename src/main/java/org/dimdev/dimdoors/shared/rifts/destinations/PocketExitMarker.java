package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.dimdoors.DimDoors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PocketExitMarker extends RiftDestination {
    //public PocketExitMarker() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return nbt;
    }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        DimDoors.chat(entity, "The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug.");
        return false;
    }
}

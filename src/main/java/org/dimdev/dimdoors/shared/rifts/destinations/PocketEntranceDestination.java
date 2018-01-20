package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.dimdoors.DimDoors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class PocketEntranceDestination extends RiftDestination { // TODO: not exactly a destination
    @Builder.Default @Saved protected float weight = 1;
    /*@Saved*/ protected RiftDestination ifDestination;
    /*@Saved*/ protected RiftDestination otherwiseDestination;

    public PocketEntranceDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        ifDestination = nbt.hasKey("ifDestination") ? RiftDestination.readDestinationNBT(nbt.getCompoundTag("ifDestination")) : null;
        otherwiseDestination = nbt.hasKey("otherwiseDestination") ? RiftDestination.readDestinationNBT(nbt.getCompoundTag("otherwiseDestination")) : null;
        NBTUtils.readFromNBT(this, nbt);
    }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if (ifDestination != null) nbt.setTag("ifDestination", ifDestination.writeToNBT(new NBTTagCompound()));
        if (otherwiseDestination != null) nbt.setTag("otherwiseDestination", otherwiseDestination.writeToNBT(new NBTTagCompound()));
        return NBTUtils.writeToNBT(this, nbt);
    }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        if (entity instanceof EntityPlayer) DimDoors.chat(entity, "The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug.");
        return false;
    }
}

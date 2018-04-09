package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class PocketEntranceMarker extends VirtualTarget implements IEntityTarget {
    @Builder.Default @Saved protected float weight = 1;
    /*@Saved*/ protected VirtualTarget ifDestination;
    /*@Saved*/ protected VirtualTarget otherwiseDestination;

    public PocketEntranceMarker() {}

    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        ifDestination = nbt.hasKey("ifDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompoundTag("ifDestination")) : null;
        otherwiseDestination = nbt.hasKey("otherwiseDestination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompoundTag("otherwiseDestination")) : null;
        NBTUtils.readFromNBT(this, nbt);
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if (ifDestination != null) nbt.setTag("ifDestination", ifDestination.writeToNBT(new NBTTagCompound()));
        if (otherwiseDestination != null) nbt.setTag("otherwiseDestination", otherwiseDestination.writeToNBT(new NBTTagCompound()));
        return NBTUtils.writeToNBT(this, nbt);
    }

    @Override public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        DimDoors.chat(entity, "The entrance of this dungeon has not been converted. If this is a normally generated pocket, please report this bug.");
        return false;
    }
}

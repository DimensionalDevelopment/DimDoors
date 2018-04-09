package org.dimdev.dimdoors.shared.rifts.targets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.dimdoors.DimDoors;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PocketExitMarker extends VirtualTarget implements IEntityTarget {
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

    @Override public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        DimDoors.chat(entity, "The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug.");
        return false;
    }
}

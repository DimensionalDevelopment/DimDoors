package org.dimdev.dimdoors.shared.rifts.destinations;

import org.dimdev.ddutils.RotatedLocation;
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
public class PocketExitDestination extends RiftDestination { // TODO: not exactly a destination
    //public PocketExitDestination() {}

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
        if (entity instanceof EntityPlayer) DimDoors.sendMessage(entity, "The exit of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
        return false;
    }
}

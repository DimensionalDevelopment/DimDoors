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
    @Saved protected RiftDestination ifDestination;
    @Saved protected RiftDestination otherwiseDestination;
    @Saved boolean hasBeenChosen;

    public PocketEntranceDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(RotatedLocation loc, Entity entity) {
        if (entity instanceof EntityPlayer) DimDoors.chat(entity, "The entrances of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
        return false;
    }
}

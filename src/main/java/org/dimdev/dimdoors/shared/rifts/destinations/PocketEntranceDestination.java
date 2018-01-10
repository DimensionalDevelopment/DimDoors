package org.dimdev.dimdoors.shared.rifts.destinations;

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
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.WeightedRiftDestination;

import java.util.LinkedList;
import java.util.List;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@NBTSerializable public class PocketEntranceDestination extends RiftDestination {
    @Saved protected float weight;
    @Saved @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default protected List<WeightedRiftDestination> ifDestinations = new LinkedList<>(); // TODO addIfDestination method in builder
    @Saved @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default protected List<WeightedRiftDestination> otherwiseDestinations = new LinkedList<>(); // TODO addOtherwiseDestination method in builder

    public PocketEntranceDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        if (entity instanceof EntityPlayer) DimDoors.chat(entity, "The entrances of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
        return false;
    }
}

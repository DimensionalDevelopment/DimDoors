package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;
import org.dimdev.dimdoors.DimDoors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedList;
import java.util.List;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@SavedToNBT public class PocketEntranceDestination extends RiftDestination {
    @SavedToNBT /*package-private*/ float weight;
    @SavedToNBT @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default /*package-private*/ List<WeightedRiftDestination> ifDestinations = new LinkedList<>(); // TODO addIfDestination method in builder
    @SavedToNBT @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default /*package-private*/ List<WeightedRiftDestination> otherwiseDestinations = new LinkedList<>(); // TODO addOtherwiseDestination method in builder

    public PocketEntranceDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        if (entity instanceof EntityPlayer) DimDoors.chat(entity, "The entrances of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
        return false;
    }
}

package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.dimdoors.DimDoors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedList;
import java.util.List;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
public class PocketEntranceDestination extends RiftDestination {
    private float weight;
    @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default private List<WeightedRiftDestination> ifDestinations = new LinkedList<>(); // TODO addIfDestination method in builder
    @SuppressWarnings({"UnusedAssignment", "RedundantSuppression"}) @Builder.Default private List<WeightedRiftDestination> otherwiseDestinations = new LinkedList<>(); // TODO addOtherwiseDestination method in builder

    public PocketEntranceDestination() {}

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        weight = nbt.getFloat("weight");

        ifDestinations = new LinkedList<>();
        NBTTagList ifDestinationsNBT = (NBTTagList) nbt.getTag("ifDestinations");
        for (NBTBase ifDestinationNBT : ifDestinationsNBT) {
            WeightedRiftDestination ifDestination = new WeightedRiftDestination();
            ifDestination.readFromNBT((NBTTagCompound) ifDestinationNBT);
            ifDestinations.add(ifDestination);
        }

        otherwiseDestinations = new LinkedList<>();
        NBTTagList otherwiseDestinationsNBT = (NBTTagList) nbt.getTag("otherwiseDestinations");
        for (NBTBase otherwiseDestinationNBT : otherwiseDestinationsNBT) {
            WeightedRiftDestination otherwiseDestination = new WeightedRiftDestination();
            otherwiseDestination.readFromNBT((NBTTagCompound) otherwiseDestinationNBT);
            otherwiseDestinations.add(otherwiseDestination);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        nbt.setFloat("weight", weight);

        NBTTagList ifDestinationsNBT = new NBTTagList();
        for (WeightedRiftDestination ifDestination : ifDestinations) {
            ifDestinationsNBT.appendTag(ifDestination.writeToNBT(new NBTTagCompound()));
        }
        nbt.setTag("ifDestinations", ifDestinationsNBT);

        NBTTagList otherwiseDestinationsNBT = new NBTTagList();
        for (WeightedRiftDestination otherwiseDestination : otherwiseDestinations) {
            otherwiseDestinationsNBT.appendTag(otherwiseDestination.writeToNBT(new NBTTagCompound()));
        }
        nbt.setTag("otherwiseDestinations", otherwiseDestinationsNBT);

        return nbt;
    }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        if (entity instanceof EntityPlayer) DimDoors.chat((EntityPlayer) entity, "The entrances of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
        return false;
    }
}

package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.math.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

@Getter @AllArgsConstructor @Builder(toBuilder = true) @ToString
@SavedToNBT public class AvailableLinkDestination extends RiftDestination { // TODO
    @SavedToNBT protected float newDungeonRiftProbability;
    @SavedToNBT protected float depthPenalization; // TODO: these make the equation assymetric
    @SavedToNBT protected float distancePenalization;
    @SavedToNBT protected float closenessPenalization;

    @SavedToNBT protected boolean dungeonRiftsOnly;
    @SavedToNBT protected boolean overworldRifts;
    @SavedToNBT protected boolean unstable;
    @SavedToNBT protected float nonFloatingRiftWeight;
    @SavedToNBT protected float floatingRiftWeight;

    @SavedToNBT protected boolean noLinkBack;
    // private int maxLinks;

    @Builder.Default private UUID uuid = UUID.randomUUID();
    // TODO: add a "safe" option to link only to a rift destination that has a non-zero weight

    AvailableLinkDestination() {}

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

    @Override
    public boolean teleport(TileEntityRift rift, Entity entity) {
        Map<RiftRegistry.RiftInfo.AvailableLinkInfo, Float> possibleDestWeightMap = new HashMap<>();

        for (RiftRegistry.RiftInfo.AvailableLinkInfo link : RiftRegistry.getAvailableLinks()) {
            VirtualLocation otherVLoc = link.getVirtualLocation();
            float weight2 = link.getWeight();
            if (weight2 == 0) continue;
            double depthDiff = Math.abs(rift.virtualLocation.getDepth() - otherVLoc.getDepth());
            double distanceSq = new BlockPos(rift.virtualLocation.getX(), rift.virtualLocation.getY(), rift.virtualLocation.getZ())
                    .distanceSq(new BlockPos(otherVLoc.getX(), otherVLoc.getY(), otherVLoc.getZ()));
            float distanceExponent = distancePenalization;
            float depthExponent = depthPenalization;
            float closenessExponent = closenessPenalization;
            float weight = (float) Math.abs(weight2/(Math.pow(depthDiff, depthExponent) * Math.pow(distanceSq, 0.5 * distanceExponent))); // TODO: fix formula
            float currentWeight = possibleDestWeightMap.get(link);
            possibleDestWeightMap.put(link, currentWeight + weight);
        }

        RiftRegistry.RiftInfo.AvailableLinkInfo selectedLink = MathUtils.weightedRandom(possibleDestWeightMap);
        Location destLoc = selectedLink.getLocation();
        if (!unstable) rift.makeDestinationPermanent(weightedDestination, destLoc);

        TileEntityRift destRift = (TileEntityRift) destLoc.getWorld().getTileEntity(destLoc.getPos()); // Link the other rift back if necessary
        ListIterator<WeightedRiftDestination> wdestIterator = destRift.destinations.listIterator();
        WeightedRiftDestination selectedWDest = null;
        while (wdestIterator.hasNext()) {
            WeightedRiftDestination wdest = wdestIterator.next();
            RiftDestination otherDest = wdest.getDestination();
            if (otherDest instanceof AvailableLinkDestination && ((AvailableLinkDestination) otherDest).uuid == selectedLink.getUuid()) {
                selectedWDest = wdest;
                wdestIterator.remove();
                break;
            }
        }
        AvailableLinkDestination selectedAvailableLinkDest = (AvailableLinkDestination) selectedWDest.getDestination();
        if (!selectedAvailableLinkDest.noLinkBack) {
            destRift.makeDestinationPermanent(selectedWDest, rift.getLocation());
        }
        ((TileEntityRift) destLoc.getTileEntity()).teleportTo(entity);
        return true;
    }

    @Override
    public void register(TileEntityRift rift) {
        RiftRegistry.RiftInfo.AvailableLinkInfo linkInfo = RiftRegistry.RiftInfo.AvailableLinkInfo.builder()
                .weight(rift.isFloating() ? floatingRiftWeight : nonFloatingRiftWeight)
                .virtualLocation(rift.virtualLocation)
                .uuid(uuid)
                .build();
        RiftRegistry.addAvailableLink(rift.getLocation(), linkInfo);
    }

    @Override
    public void unregister(TileEntityRift rift) {
        RiftRegistry.removeAvailableLinkByUUID(rift.getLocation(), uuid);
    }
}

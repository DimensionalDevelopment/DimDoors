package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.TeleporterDimDoors;
import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.pockets.Pocket;
import com.zixiken.dimdoors.shared.pockets.PocketGenerator;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.rifts.RiftDestination.*;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry.RiftInfo.AvailableLinkInfo;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.util.MathUtils;
import com.zixiken.dimdoors.shared.util.WorldUtils;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class TileEntityRift extends TileEntity implements ITickable { // TODO: implement ITeleportSource and ITeleportDestination

    @Getter protected VirtualLocation virtualLocation;
    @Nonnull @Getter protected List<WeightedRiftDestination> destinations;
    @Getter protected boolean makeDestinationPermanent;
    @Getter protected boolean preserveRotation;
    @Getter protected float yaw;
    @Getter protected float pitch;
    @Getter protected boolean alwaysDelete; // Delete the rift when an entrance rift is broken even if the state was changed or destinations link there.
    @Getter protected float chaosWeight;
    // TODO: option to convert to door on teleportTo?

    protected boolean riftStateChanged; // not saved

    public TileEntityRift() {
        destinations = new ArrayList<>();
        makeDestinationPermanent = true;
        preserveRotation = true;
        pitch = 0;
        alwaysDelete = false;
        chaosWeight = 1;
    }

    public void copyFrom(TileEntityRift oldRift) {
        virtualLocation = oldRift.virtualLocation;
        destinations = oldRift.destinations;
        makeDestinationPermanent = oldRift.makeDestinationPermanent;
        preserveRotation = oldRift.preserveRotation;
        yaw = oldRift.yaw;
        pitch = oldRift.pitch;

        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("virtualLocation")) virtualLocation = VirtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));

        NBTTagList destinationsNBT = (NBTTagList) nbt.getTag("destinations");
        destinations = new ArrayList<>();
        if (destinationsNBT != null) for (NBTBase destinationNBT : destinationsNBT) {
            WeightedRiftDestination destination = new WeightedRiftDestination();
            destination.readFromNBT((NBTTagCompound) destinationNBT);
            destinations.add(destination);
        }

        makeDestinationPermanent = nbt.getBoolean("makeDestinationPermanent");
        preserveRotation = nbt.getBoolean("preserveRotation");
        yaw = nbt.getFloat("yaw");
        pitch = nbt.getFloat("pitch");
        alwaysDelete = nbt.getBoolean("alwaysDelete");
        chaosWeight = nbt.getFloat("chaosWeight");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (virtualLocation != null) nbt.setTag("virtualLocation", virtualLocation.writeToNBT());

        NBTTagList destinationsNBT = new NBTTagList();
        for (WeightedRiftDestination destination : destinations) {
            destinationsNBT.appendTag(destination.writeToNBT(new NBTTagCompound()));
        }
        nbt.setTag("destinations", destinationsNBT);

        nbt.setBoolean("makeDestinationPermanent", makeDestinationPermanent);
        nbt.setBoolean("preserveRotation", preserveRotation);
        nbt.setFloat("yaw", yaw);
        nbt.setFloat("pitch", pitch);
        nbt.setBoolean("alwaysDelete", alwaysDelete);
        nbt.setFloat("chaosWeight", chaosWeight);

        return nbt;
    }

    public void setVirtualLocation(VirtualLocation virtualLocation) {
        this.virtualLocation = virtualLocation;
        updateAvailableLinks();
        // TODO: update available link virtual locations
        markDirty();
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        markDirty();
    }

    public void addDestination(RiftDestination destination, float weight, int group) {
        riftStateChanged = true;
        destinations.add(new WeightedRiftDestination(destination, weight, group));
        registerDest(destination);
        markDirty();
    }

    public void removeDestination(int index) {
        riftStateChanged = true;
        unregisterDest(destinations.remove(index).getDestination());
        markDirty();
    }

    public void removeDestinationLoc(Location location) {
        Iterator<WeightedRiftDestination> destinationIterator = destinations.iterator();
        while (destinationIterator.hasNext()) {
            RiftDestination dest = destinationIterator.next().getDestination();
            if (destinationToLocation(dest).equals(location)) {
                destinationIterator.remove();
                unregisterDest(dest);
            }
        }
        markDirty();
    }

    public void clearDestinations() {
        for (WeightedRiftDestination wdest : destinations) {
            unregisterDest(wdest.getDestination());
        }
        destinations.clear();
        markDirty();
    }

    public void setSingleDestination(RiftDestination destination) {
        clearDestinations();
        addDestination(destination, 1, 0);
    }

    public Location destinationToLocation(RiftDestination dest) {
        switch (dest.getType()) { // TODO: these need a superclass and a single translation method
            case RELATIVE:
                RelativeDestination relativeDest = (RelativeDestination) dest;
                return new Location(world, pos.add(relativeDest.getOffset()));
            case LOCAL:
                LocalDestination localDest = (LocalDestination) dest;
                return new Location(world, localDest.getPos());
            case GLOBAL:
                GlobalDestination globalDest = (GlobalDestination) dest;
                return globalDest.getLoc();
        }
        return null;
    }

    public void registerDest(RiftDestination dest) {
        if (!isRegistered()) return;
        Location destLoc = destinationToLocation(dest);
        if (destLoc != null) RiftRegistry.addLink(new Location(world, pos), destLoc);
        if (dest.getType() == EnumType.AVAILABLE_LINK) {
            AvailableLinkDestination linkDest = (AvailableLinkDestination) dest;
            AvailableLinkInfo linkInfo = AvailableLinkInfo.builder()
                    .weight(isEntrance() ? linkDest.getEntranceLinkWeight() : linkDest.getFloatingRiftWeight())
                    .virtualLocation(virtualLocation)
                    .uuid(linkDest.getUuid())
                    .build();
            RiftRegistry.addAvailableLink(new Location(world, pos), linkInfo);
        }
    }

    public void updateAvailableLinks() {
        if (!isRegistered()) return;
        RiftRegistry.clearAvailableLinks(new Location(world, pos));
        for (WeightedRiftDestination wdest : destinations) {
            RiftDestination dest = wdest.getDestination();
            if (dest.getType() == EnumType.AVAILABLE_LINK) {
                AvailableLinkDestination linkDest = (AvailableLinkDestination) dest;
                AvailableLinkInfo linkInfo = AvailableLinkInfo.builder()
                        .weight(isEntrance() ? linkDest.getEntranceLinkWeight() : linkDest.getFloatingRiftWeight())
                        .virtualLocation(virtualLocation)
                        .uuid(linkDest.getUuid())
                        .build();
                RiftRegistry.addAvailableLink(new Location(world, pos), linkInfo);
            }
        }
    }

    public void unregisterDest(RiftDestination dest) {
        if (!isRegistered()) return;
        Location destLoc = destinationToLocation(dest);
        if (destLoc != null) RiftRegistry.removeLink(new Location(world, pos), destLoc);
    }

    public boolean isRegistered() {
        return world != null && RiftRegistry.getRiftInfo(new Location(world, pos)) != null;
    }

    // Make sure virtualLocation != null before calling!
    public void register() { // registers or reregisters the rift TODO: what if it's already registered?
        Location loc = new Location(world, pos);
        RiftRegistry.addRift(loc);
        for (WeightedRiftDestination weightedDest : destinations) {
            registerDest(weightedDest.getDestination());
        }
    }

    public void unregister() {
        if (!isRegistered()) return;
        RiftRegistry.removeRift(new Location(world, pos));
        // TODO: inform pocket that entrance was destroyed (we'll probably need an isPrivate field on the pocket)
    }

    public void teleportTo(Entity entity) { // TODO: new velocity angle if !preserveRotation?
        float newYaw = entity.rotationYaw;
        float newPitch = entity.rotationYaw;
        if (!preserveRotation) {
            newYaw = yaw;
            newPitch = pitch;
        }
        TeleporterDimDoors.instance().teleport(entity, new Location(world, pos));
        entity.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), newYaw, newPitch);

        int dim = WorldUtils.getDim(world);
        if (entity instanceof EntityPlayer && DimDoorDimensions.isPocketDimension(dim)) { // TODO
            PocketRegistry.getForDim(dim).allowPlayerAtLocation((EntityPlayer) entity, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public boolean teleport(Entity entity) { try { // TODO: return failiure message string rather than boolean
        riftStateChanged = false;
        if (destinations.size() == 0) return false;

        Map<WeightedRiftDestination, Float> weightMap = new HashMap<>(); // TODO: cache this, faster implementation of single rift
        for (WeightedRiftDestination destination : destinations) {
            weightMap.put(destination, destination.getWeight());
        }
        WeightedRiftDestination weightedDestination = MathUtils.weightedRandom(weightMap);
        int group = weightedDestination.getGroup();
        if(makeDestinationPermanent) {
            destinations.removeIf(wdest -> wdest.getGroup() != group);
            markDirty();
        }

        RiftDestination dest = weightedDestination.getDestination();
        Location destLoc;
        GlobalDestination destHere = new GlobalDestination(new Location(world, pos)); // TODO: local if possible
        switch (dest.getType()) {
            case RELATIVE:
            case LOCAL:
            case GLOBAL:
                destLoc = destinationToLocation(dest);
                break;
            case NEW_PUBLIC:
                Pocket publicPocket = PocketGenerator.generatePublicPocket(virtualLocation != null ? virtualLocation.toBuilder().depth(-1).build() : null); // TODO: random transform
                publicPocket.setup();
                publicPocket.linkPocketTo(destHere);
                destLoc = publicPocket.getEntrance();
                if (destLoc != null) makeDestinationPermanent(weightedDestination, destLoc);
                break;
            case PRIVATE: // TODO: move logic to PrivatePocketTeleportDestination
                String uuid = null;
                if (entity instanceof EntityPlayer) uuid = entity.getUniqueID().toString();
                if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) uuid = ((IEntityOwnable) entity).getOwnerId().toString();
                if (uuid != null) {
                    PocketRegistry privatePocketRegistry = PocketRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
                    RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
                    Pocket privatePocket = privatePocketRegistry.getPocket(privatePocketRegistry.getPrivatePocketID(uuid));
                    if (privatePocket == null) { // generate the private pocket and get its entrance
                        privatePocket = PocketGenerator.generatePrivatePocket(virtualLocation != null ? virtualLocation.toBuilder().depth(-2).build() : null); // set to where the pocket was first created TODO: private pocket deletion
                        privatePocket.setup();
                        privatePocketRegistry.setPrivatePocketID(uuid, privatePocket.getId());
                        destLoc = privatePocket.getEntrance();
                    } else {
                        destLoc = privateRiftRegistry.getPrivatePocketEntrance(uuid); // get the last used entrance
                        if (destLoc == null) destLoc = privatePocket.getEntrance(); // if there's none, then set the target to the main entrance
                    }
                    privateRiftRegistry.setPrivatePocketEntrance(uuid, null); // forget the last entered entrance
                } else {
                    return false; // TODO: There should be a way to get other entities into your private pocket, though. Add API for other mods.
                }
                break;
            case ESCAPE:
            case PRIVATE_POCKET_EXIT:
                /*String*/ uuid = null;
                if (entity instanceof EntityPlayer) uuid = entity.getUniqueID().toString();
                if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) uuid = ((IEntityOwnable) entity).getOwnerId().toString();
                if (uuid != null) {
                    RiftRegistry privateRiftRegistry = RiftRegistry.getForDim(DimDoorDimensions.getPrivateDimID());
                    destLoc = RiftRegistry.getEscapeRift(uuid);
                    RiftRegistry.setEscapeRift(uuid, null); // forget the last used escape rift
                    if (dest.getType() == EnumType.PRIVATE_POCKET_EXIT) {
                        privateRiftRegistry.setPrivatePocketEntrance(uuid, new Location(world, pos)); // Remember which exit was used for next time the pocket is entered
                    } else if (dest.getType() == EnumType.ESCAPE) {
                        // TODO: teleport the player to random coordinates based on depth around destLoc
                        return true;
                    }
                    if (destLoc == null) return false; // TODO: The player probably teleported into the dungeon/private pocket and is now trying to escape... What should we do? Limbo?
                } else {
                    return false; // Non-player/owned entity tried to escape/leave private pocket
                }
                break;
            case LIMBO: // TODO: move logic to LimboTeleportDestination
                throw new RuntimeException("Not yet implemented!"); // TODO: random coordinates based on VirtualLocation
            case AVAILABLE_LINK: // TODO: chaos door
                AvailableLinkDestination linkDest = (AvailableLinkDestination) dest;
                Map<AvailableLinkInfo, Float> possibleDestWeightMap = new HashMap<>();

                for (AvailableLinkInfo link : RiftRegistry.getAvailableLinks()) {
                    VirtualLocation otherVLoc = link.getVirtualLocation();
                    float weight2 = link.getWeight();
                    if (weight2 == 0) continue;
                    double depthDiff = Math.abs(virtualLocation.getDepth() - otherVLoc.getDepth());
                    double distanceSq = new BlockPos(virtualLocation.getX(), virtualLocation.getY(), virtualLocation.getZ())
                            .distanceSq(new BlockPos(otherVLoc.getX(), otherVLoc.getY(), otherVLoc.getZ()));
                    float distanceExponent = linkDest.getDistancePenalization();
                    float depthExponent = linkDest.getDepthPenalization();
                    float closenessExponent = linkDest.getClosenessPenalization();
                    float weight = (float) Math.abs(weight2/(Math.pow(depthDiff, depthExponent) * Math.pow(distanceSq, 0.5 * distanceExponent))); // TODO: fix formula
                    float currentWeight = possibleDestWeightMap.get(link);
                    possibleDestWeightMap.put(link, currentWeight + weight);
                }

                AvailableLinkInfo selectedLink = MathUtils.weightedRandom(possibleDestWeightMap);
                destLoc = selectedLink.getLocation();
                if (!linkDest.isUnstable()) makeDestinationPermanent(weightedDestination, destLoc);

                TileEntityRift destRift = (TileEntityRift) destLoc.getWorld().getTileEntity(destLoc.getPos()); // Link the other rift back if necessary
                ListIterator<WeightedRiftDestination> wdestIterator = destRift.destinations.listIterator();
                WeightedRiftDestination selectedWDest = null;
                while (wdestIterator.hasNext()) {
                    WeightedRiftDestination wdest = wdestIterator.next();
                    RiftDestination otherDest = wdest.getDestination();
                    if (otherDest.getType() == EnumType.AVAILABLE_LINK && ((AvailableLinkDestination) otherDest).getUuid() == selectedLink.getUuid()) {
                        selectedWDest = wdest;
                        wdestIterator.remove();
                        break;
                    }
                }
                AvailableLinkDestination selectedAvailableLinkDest = (AvailableLinkDestination) selectedWDest.getDestination();
                if (!selectedAvailableLinkDest.isNoLinkBack()) {
                    destRift.makeDestinationPermanent(selectedWDest, new Location(world, pos));
                }
                break;
            case POCKET_ENTRANCE:
            case POCKET_EXIT:
                if (entity instanceof EntityPlayer) {
                    DimDoors.chat((EntityPlayer) entity, "The entrance/exit of this dungeon has not been linked. Either this is a bug or you are in dungeon-building mode.");
                }
                return false;
            default:
                throw new RuntimeException("That rift type is not implemented in TileRiftEntity.teleport, this is a bug.");
        }
        if (destLoc == null) {
            if (entity instanceof EntityPlayer) DimDoors.chat((EntityPlayer) entity, "The destination was null. Either this is a bug in TileEntityRift.java, or the pocket does not have an entrance!");
            return false;
        }
        TileEntity tileEntityAtLoc = destLoc.getWorld().getTileEntity(destLoc.getPos());
        if (!(tileEntityAtLoc instanceof TileEntityRift)) throw new RuntimeException("The rift referenced by this rift does not exist, this is a bug.");
        TileEntityRift destRift = (TileEntityRift) tileEntityAtLoc;
        if (entity instanceof EntityPlayer && !DimDoorDimensions.isPocketDimension(WorldUtils.getDim(world))) { // TODO: What about player-owned entities? We should store their exit rift separately to avoid having problems if they enter different rifts
            String uuid = entity.getUniqueID().toString(); // TODO: More configuration on which worlds should be considered normal worlds. Other mods might add mostly void worlds, causing problems with random coordinates
            RiftRegistry.setEscapeRift(uuid, new Location(world, pos));
        }
        destRift.teleportTo(entity);
        return true;

        } catch (Exception e) {
            if (entity instanceof EntityPlayer) DimDoors.chat((EntityPlayer) entity, "There was an exception while teleporting!");
            e.printStackTrace();
            return false;
        }
    }

    private void makeDestinationPermanent(WeightedRiftDestination weightedDestination, Location destLoc) {
        riftStateChanged = true;
        RiftDestination newDest;
        if (WorldUtils.getDim(world) == destLoc.getDimID()) {
            newDest = new LocalDestination(destLoc.getPos()); // TODO: RelativeDestination instead?
        } else {
            newDest = new GlobalDestination(destLoc);
        }
        newDest = newDest.withOldDestination(weightedDestination.getDestination());
        destinations.remove(weightedDestination);
        destinations.add(new WeightedRiftDestination(newDest, weightedDestination.getWeight(), weightedDestination.getGroup()));
        markDirty();
    }

    public void destinationGone(Location loc) {
        ListIterator<WeightedRiftDestination> wdestIterator = destinations.listIterator();
        while (wdestIterator.hasNext()) {
            WeightedRiftDestination wdest = wdestIterator.next();
            RiftDestination dest = wdest.getDestination();
            if (loc.equals(destinationToLocation(dest))) {
                wdestIterator.remove();
                RiftDestination oldDest = dest.getOldDestination();
                if (oldDest != null) {
                    wdestIterator.add(new WeightedRiftDestination(oldDest, wdest.getWeight(), wdest.getGroup()));
                }
            }
        }
        destinations.removeIf(weightedRiftDestination -> loc.equals(destinationToLocation(weightedRiftDestination.getDestination())));
    }

    public void allSourcesGone() {
        // TODO
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        deserializeNBT(tag);
    }

    public void notifyStateChanged() {
        riftStateChanged = true;
        markDirty();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, serializeNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        deserializeNBT(pkt.getNbtCompound());
    }

    public void setChaosWeight(int chaosWeight) {
        this.chaosWeight = chaosWeight;
        markDirty();
    }

    public abstract boolean isEntrance(); // TODO: replace with chooseWeight function

}

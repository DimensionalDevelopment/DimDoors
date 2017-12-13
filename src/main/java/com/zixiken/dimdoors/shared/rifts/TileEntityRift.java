package com.zixiken.dimdoors.shared.rifts;

import com.zixiken.dimdoors.DimDoors;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public abstract class TileEntityRift extends TileEntity implements ITickable { // TODO: implement ITeleportSource and ITeleportDestination

    @Getter protected VirtualLocation virtualLocation; // location can be null
    @Getter protected List<WeightedRiftDestination> destinations; // must be non-null //
    @Getter protected boolean makeDestinationPermanent; //
    @Getter protected int freeLinks;
    @Getter protected boolean preserveRotation; //
    @Getter protected float yaw; //
    @Getter protected float pitch; //
    @Getter protected boolean alwaysDelete; //
    // TODO: option to convert to door on teleportTo?
    // TODO: chaos door link weights?

    protected boolean riftStateChanged; // not saved


    public TileEntityRift() {
        destinations = new ArrayList<>();
        makeDestinationPermanent = true;
        freeLinks = 1;
        preserveRotation = true;
        pitch = 0;
        alwaysDelete = false;
    }

    public void copyFrom(TileEntityRift oldRift) {
        virtualLocation = oldRift.virtualLocation;
        destinations = oldRift.destinations;
        makeDestinationPermanent = oldRift.makeDestinationPermanent;
        freeLinks = oldRift.freeLinks;
        preserveRotation = oldRift.preserveRotation;
        yaw = oldRift.yaw;
        pitch = oldRift.pitch;

        markDirty();
    }

    public boolean isEntrance() {
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("virtualLocation")) virtualLocation = VirtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));

        NBTTagList destinationsNBT = (NBTTagList) nbt.getTag("destinations");
        destinations = new ArrayList<>();
        for (NBTBase destinationNBT : destinationsNBT) {
            WeightedRiftDestination destination = new WeightedRiftDestination();
            destination.readFromNBT((NBTTagCompound) destinationNBT);
            destinations.add(destination);
        }

        makeDestinationPermanent = nbt.getBoolean("makeDestinationPermanent");
        preserveRotation = nbt.getBoolean("preserveRotation");
        yaw = nbt.getFloat("yaw");
        pitch = nbt.getFloat("pitch");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setTag("virtualLocation", virtualLocation.writeToNBT());

        NBTTagList destinationsNBT = new NBTTagList();
        for (WeightedRiftDestination destination : destinations) {
            destinationsNBT.appendTag(destination.writeToNBT(nbt));
        }

        nbt.setBoolean("makeDestinationPermanent", makeDestinationPermanent);
        nbt.setBoolean("preserveRotation", preserveRotation);
        nbt.setFloat("yaw", yaw);
        nbt.setFloat("pitch", pitch);

        return nbt;
    }

    public void setVirtualLocation(VirtualLocation virtualLocation) {
        this.virtualLocation = virtualLocation;
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
            if (dest.getType() == DestinationType.GLOBAL) {
                GlobalDestination globalDest = (GlobalDestination) dest;
                if (globalDest.getDim() == location.getDimID()
                 && globalDest.getX() == location.getPos().getX()
                 && globalDest.getY() == location.getPos().getY()
                 && globalDest.getZ() == location.getPos().getZ()) {
                    unregisterDest(dest);
                    destinationIterator.remove();
                }
                if (location.getDimID() == WorldUtils.getDim(world)) {
                    if (dest.getType() == DestinationType.LOCAL) {
                        LocalDestination localDest = (LocalDestination) dest;
                        if (localDest.getX() == location.getPos().getX()
                         && localDest.getY() == location.getPos().getY()
                         && localDest.getZ() == location.getPos().getZ()) {
                            unregisterDest(dest);
                            destinationIterator.remove();
                        }
                    } else if (dest.getType() == DestinationType.RELATIVE) {
                        RelativeDestination relativeDest = (RelativeDestination) dest;
                        if (location.getPos().equals(pos.add(relativeDest.getXOffset(), relativeDest.getYOffset(), relativeDest.getZOffset()))) {
                            unregisterDest(dest);
                            destinationIterator.remove();
                        }
                    }
                }
            }
        }
        markDirty();
    }

    public void clearDestinations() {
        for (WeightedRiftDestination wdest : destinations) {
            unregisterDest(wdest.getDestination());
        }
        destinations = new ArrayList<>();
        markDirty();
    }

    public void setSingleDestination(RiftDestination destination) {
        clearDestinations();
        addDestination(destination, 1, 0);
    }

    public Location translateDestCoordinates(RiftDestination dest) {
        switch (dest.getType()) { // TODO: these need a superclass and a single translation method
            case RELATIVE:
                RelativeDestination relativeDest = (RelativeDestination) dest;
                return new Location(world, pos.add(relativeDest.getXOffset(), relativeDest.getYOffset(), relativeDest.getZOffset()));
            case LOCAL:
                LocalDestination localDest = (LocalDestination) dest;
                return new Location(world, new BlockPos(localDest.getX(), localDest.getY(), localDest.getZ()));
            case GLOBAL:
                GlobalDestination globalDest = (GlobalDestination) dest;
                return new Location(globalDest.getDim(), new BlockPos(globalDest.getX(), globalDest.getY(), globalDest.getZ()));
        }
        return null;
    }

    public void registerDest(RiftDestination dest) {
        Location destLoc = translateDestCoordinates(dest);
        if (destLoc != null) RiftRegistry.registerNewLink(new Location(world, pos), destLoc);
    }

    public void unregisterDest(RiftDestination dest) {
        Location destLoc = translateDestCoordinates(dest);
        if (destLoc != null) RiftRegistry.deleteLink(new Location(world, pos), destLoc);
    }

    public void register() { // registers or reregisters the rift
        Location loc = new Location(world, pos);
        RiftRegistry.addRift(loc);
        for (WeightedRiftDestination weightedDest : destinations) {
            registerDest(weightedDest.getDestination());
        }
    }

    public void destroyRift() {
        RiftRegistry.deleteRift(new Location(world, pos));
        // TODO: inform pocket that entrance was destroyed (we'll probably need an isPrivate field on the pocket)
    }

    public void teleportTo(Entity entity) { // TODO: new velocity angle if !preserveRotation?
        entity.setWorld(world);
        if (preserveRotation) {
            entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        } else {
            entity.setPositionAndRotation(pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
        }

        int dim = WorldUtils.getDim(world);
        if (entity instanceof EntityPlayer && DimDoorDimensions.isPocketDimension(dim)) { // TODO
            PocketRegistry.getForDim(dim).allowPlayerAtLocation((EntityPlayer) entity, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public boolean teleport(Entity entity) {
        riftStateChanged = false;
        if (destinations.size() == 0) return false;

        Map<WeightedRiftDestination, Float> weightMap = new HashMap<>(); // TODO: cache this, faster implementation of single rift
        for (WeightedRiftDestination destination : destinations) {
            weightMap.put(destination, destination.getWeight());
        }
        WeightedRiftDestination weightedDestination = MathUtils.weightedRandom(weightMap);
        int group = weightedDestination.getGroup();
        if(makeDestinationPermanent) {
            List<WeightedRiftDestination> list = new ArrayList<>();
            for (WeightedRiftDestination otherDestination : destinations) {
                if (otherDestination.getGroup() == group) {
                    list.add(otherDestination);
                }
            }
            destinations = list;
            markDirty();
        }
        RiftDestination dest = weightedDestination.getDestination();
        Location destLoc;
        GlobalDestination destHere = new GlobalDestination(WorldUtils.getDim(world), pos.getX(), pos.getY(), pos.getZ());
        switch (dest.getType()) {
            case RELATIVE:
                RelativeDestination relativeDest = (RelativeDestination) dest;
                destLoc = new Location(world, pos.add(relativeDest.getXOffset(), relativeDest.getYOffset(), relativeDest.getZOffset()));
                break;
            case LOCAL:
                LocalDestination localDest = (LocalDestination) dest;
                destLoc = new Location(world, localDest.getX(), localDest.getY(), localDest.getZ());
                break;
            case GLOBAL:
                GlobalDestination globalDest = (GlobalDestination) dest;
                destLoc = new Location(globalDest.getDim(), globalDest.getX(), globalDest.getY(), globalDest.getZ());
                break;
            case NEW_PUBLIC:
                Pocket publicPocket = PocketGenerator.generatePublicPocket(virtualLocation.toBuilder().depth(-1).build()); // TODO: random transform
                publicPocket.selectEntrance();
                publicPocket.linkPocketTo(destHere);
                destLoc = publicPocket.getEntrance();
                makeDestinationPermanent(weightedDestination, destLoc);
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
                        privatePocket = PocketGenerator.generatePrivatePocket(virtualLocation.toBuilder().depth(-2).build()); // set to where the pocket was first created TODO: private pocket deletion
                        privatePocket.selectEntrance();
                        destLoc = privatePocket.getEntrance();
                    } else {
                        destLoc = privateRiftRegistry.getPrivatePocketEntrance(uuid); // get the last used entrance
                        if (destLoc == null) destLoc = privatePocket.getEntrance(); // if there's none, then set the target to the main entrance
                    }
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
                    if (dest.getType() == DestinationType.PRIVATE_POCKET_EXIT) {
                        privateRiftRegistry.setPrivatePocketEntrance(uuid, new Location(world, pos)); // Remember which exit was used for next time the pocket is entered
                    }
                    if (destLoc == null) return false; // TODO: The player probably teleported into the dungeon/private pocket and is now trying to escape... What should we do? Limbo?
                } else {
                    return false; // Non-player/owned entity tried to escape/leave private pocket
                }
                break;
            case LIMBO: // TODO: move logic to LimboTeleportDestination
                throw new RuntimeException("Not yet implemented!"); // TODO: random coordinates based on VirtualLocation
            case RANDOM_RIFT_LINK: // TODO: chaos door
                RandomRiftLinkDestination randomDest = (RandomRiftLinkDestination) dest;
                Map<Location, Float> possibleDestWeightMap = new HashMap<>();

                for (AvailableLinkInfo link : RiftRegistry.getAvailableLinks()) {
                    VirtualLocation otherVLoc = link.getVirtualLocation();
                    double depthDiff = Math.abs(virtualLocation.getDepth() - otherVLoc.getDepth());
                    double distanceSq = new BlockPos(virtualLocation.getX(), virtualLocation.getY(), virtualLocation.getZ())
                            .distanceSq(new BlockPos(otherVLoc.getX(), otherVLoc.getY(), otherVLoc.getZ()));
                    float distanceExponent = randomDest.getDistancePenalization();
                    float depthExponent = randomDest.getDepthPenalization();
                    float closenessExponent = randomDest.getClosenessPenalization();
                    float weight2 = link.getWeight();
                    float weight = (float) Math.abs(weight2/(Math.pow(depthDiff, depthExponent) * Math.pow(distanceSq, 0.5 * distanceExponent))); // TODO: fix formula
                    float currentWeight = possibleDestWeightMap.get(link.getLocation());
                    possibleDestWeightMap.put(link.getLocation(), currentWeight + weight);
                }

                destLoc = MathUtils.weightedRandom(possibleDestWeightMap);
                if (!randomDest.isUnstable()) makeDestinationPermanent(weightedDestination, destLoc);
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
        TileEntity tileEntityAtLoc = destLoc.getWorld().getTileEntity(destLoc.getPos());
        if (!(tileEntityAtLoc instanceof TileEntityRift)) throw new RuntimeException("The rift referenced by this rift does not exist, this is a bug.");
        TileEntityRift destRift = (TileEntityRift) tileEntityAtLoc;
        destRift.teleportTo(entity);
        return true;
    }

    private void makeDestinationPermanent(WeightedRiftDestination weightedDestination, Location destLoc) {
        riftStateChanged = true;
        GlobalDestination newDest = new GlobalDestination(destLoc.getDimID(), destLoc.getPos().getX(), destLoc.getPos().getY(), destLoc.getPos().getZ()); // TODO: RelativeDestination instead?
        destinations.add(new WeightedRiftDestination(newDest, weightedDestination.getWeight(), weightedDestination.getGroup()));
        destinations.remove(weightedDestination);
        markDirty();
    }

    public void destinationGone(Location loc) {
        destinations.removeIf(weightedRiftDestination -> loc.equals(translateDestCoordinates(weightedRiftDestination.getDestination())));
    }

    public void checkIfNeeded() {
        // TODO
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }
}

package org.dimdev.dimdoors.shared.rifts;

import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;
import org.dimdev.ddutils.RGBA;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.pockets.Pocket;
import org.dimdev.dimdoors.shared.pockets.PocketRegistry;
import org.dimdev.ddutils.EntityUtils;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.TeleportUtils;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.dimdoors.shared.world.ModDimensions;
import lombok.Getter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;

@SavedToNBT public abstract class TileEntityRift extends TileEntity implements ITickable { // TODO: implement ITeleportSource and ITeleportDestination

    @SavedToNBT@Getter protected VirtualLocation virtualLocation;
    @SavedToNBT @Nonnull @Getter protected List<WeightedRiftDestination> destinations; // Not using a set because we can have duplicate destinations. Maybe use Multiset from Guava?
    @SavedToNBT @Getter protected boolean makeDestinationPermanent;
    @SavedToNBT @Getter protected boolean preserveRotation;
    @SavedToNBT @Getter protected float yaw;
    @SavedToNBT @Getter protected float pitch;
    @SavedToNBT @Getter protected boolean alwaysDelete; // Delete the rift when an entrances rift is broken even if the state was changed or destinations link there.
    @SavedToNBT @Getter protected float chaosWeight;
    @SavedToNBT @Getter protected boolean forcedColor;
    @SavedToNBT @Getter protected RGBA color = null; // TODO: update AnnotatedNBT to be able to save these
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
        chaosWeight = oldRift.chaosWeight;
        if (oldRift.isFloating() != isFloating()) updateAvailableLinks();

        markDirty();
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        return NBTUtils.writeToNBT(this, nbt);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        deserializeNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, serializeNBT());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        deserializeNBT(pkt.getNbtCompound());
    }


    // Use vanilla behavior of refreshing only when block changes, not state (otherwise, opening the door would destroy the tile entity)
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    // Modification functions
    public void setVirtualLocation(VirtualLocation virtualLocation) {
        this.virtualLocation = virtualLocation;
        updateAvailableLinks();
        // TODO: update available link virtual locations
        markDirty();
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        preserveRotation = false;
        markDirty();
    }

    public void clearRotation() {
        preserveRotation = true;
    }

    public void addDestination(RiftDestination destination, float weight, int group) {
        riftStateChanged = true;
        destinations.add(new WeightedRiftDestination(destination, weight, group));
        if (isRegistered()) destination.register(this);
        markDirty();
    }

    public void addDestination(RiftDestination destination, float weight, int group, RiftDestination oldDestination) {
        riftStateChanged = true;
        destinations.add(new WeightedRiftDestination(destination, weight, group, oldDestination));
        if (isRegistered()) destination.register(this);
        markDirty();
    }

    public void removeDestination(WeightedRiftDestination dest) {
        riftStateChanged = true;
        destinations.remove(dest);
        if (isRegistered()) dest.getDestination().unregister(this);
        markDirty();
    }

    public void clearDestinations() {
        if (isRegistered()) for (WeightedRiftDestination wdest : destinations) {
            wdest.getDestination().unregister(this);
        }
        destinations.clear();
        markDirty();
    }

    public void setSingleDestination(RiftDestination destination) {
        clearDestinations();
        addDestination(destination, 1, 0);
    }

    public void setChaosWeight(float chaosWeight) {
        this.chaosWeight = chaosWeight;
        markDirty();
    }

    public void setColor(RGBA color) {
        forcedColor = color != null;
        this.color = color;
        markDirty();
    }

    public void markStateChanged() {
        riftStateChanged = true;
        markDirty();
    }

    public void makeDestinationPermanent(WeightedRiftDestination weightedDestination, Location destLoc) {
        riftStateChanged = true;
        RiftDestination newDest;
        if (WorldUtils.getDim(world) == destLoc.getDim()) {
            newDest = new LocalDestination(destLoc.getPos()); // TODO: RelativeDestination instead?
        } else {
            newDest = new GlobalDestination(destLoc);
        }
        removeDestination(weightedDestination);
        addDestination(newDest, weightedDestination.getWeight(), weightedDestination.getGroup(), weightedDestination.getDestination());
        markDirty();
    }

    // Registry
    public boolean isRegistered() {
        return world != null && RiftRegistry.getRiftInfo(new Location(world, pos)) != null;
    }

    public void register() {
        if (isRegistered()) return;
        Location loc = new Location(world, pos);
        RiftRegistry.addRift(loc);
        for (WeightedRiftDestination weightedDest : destinations) {
            weightedDest.getDestination().register(this);
        }
        updateColor();
    }

    public void unregister() {
        if (!isRegistered()) return;
        RiftRegistry.removeRift(new Location(world, pos)); // TODO: unregister destinations
        if (ModDimensions.isDimDoorsPocketDimension(world)) {
            PocketRegistry pocketRegistry = PocketRegistry.getForDim(WorldUtils.getDim(world));
            Pocket pocket = pocketRegistry.getPocketAt(pos);
            if (pocket != null && pocket.getEntrance() != null && pocket.getEntrance().getPos().equals(pos)) {
                pocket.setEntrance(null);
                pocketRegistry.markDirty();
            }
        }
        // TODO: inform pocket that entrances was destroyed (we'll probably need an isPrivate field on the pocket)
    }

    public void updateAvailableLinks() { // Update available link info on rift type change or on virtualLocation change
        if (!isRegistered()) return;
        RiftRegistry.clearAvailableLinks(new Location(world, pos));
        for (WeightedRiftDestination wdest : destinations) {
            RiftDestination dest = wdest.getDestination();
            if (dest instanceof AvailableLinkDestination) {
                dest.register(this);
            }
        }
    }

    public void destinationGone(Location loc) {
        ListIterator<WeightedRiftDestination> wdestIterator = destinations.listIterator();
        while (wdestIterator.hasNext()) {
            WeightedRiftDestination wdest = wdestIterator.next();
            RiftDestination dest = wdest.getDestination();
            if (loc.equals(dest.getReferencedRift(getLocation()))) {
                wdestIterator.remove(); // TODO: unregister*
                RiftDestination oldDest = wdest.getOldDestination();
                if (oldDest != null) {
                    wdestIterator.add(new WeightedRiftDestination(oldDest, wdest.getWeight(), wdest.getGroup()));
                    if (isRegistered()) oldDest.register(this);
                }
            }
        }
        destinations.removeIf(weightedRiftDestination -> loc.equals(weightedRiftDestination.getDestination().getReferencedRift(getLocation())));
        markDirty();
    }

    // Teleport logic
    public boolean teleport(Entity entity) {
        riftStateChanged = false;

        // Check that the rift has destinations
        if (destinations.size() == 0) {
            DimDoors.chat(entity, "This rift has no destinations!");
            return false;
        }

        // Get a random destination based on the weights
        Map<WeightedRiftDestination, Float> weightMap = new HashMap<>(); // TODO: cache this, faster implementation of single rift
        for (WeightedRiftDestination destination : destinations) {
            weightMap.put(destination, destination.getWeight());
        }
        WeightedRiftDestination weightedDestination = MathUtils.weightedRandom(weightMap);

        // Remove destinations from other groups if makeDestinationPermanent is true
        if(makeDestinationPermanent) {
            destinations.removeIf(wdest -> wdest.getGroup() != weightedDestination.getGroup());
            markDirty();
        }

        // Attempt a teleport
        try {
            if (weightedDestination.getDestination().teleport(this, entity)) {
                // Set last used rift if necessary
                // TODO: What about player-owned entities? We should store their exit rift separately to avoid having problems if they enter different rifts
                // TODO: use entity UUID rather than player UUID!
                if (entity instanceof EntityPlayer && !ModDimensions.isDimDoorsPocketDimension(WorldUtils.getDim(world))) {
                    RiftRegistry.setOverworldRift(EntityUtils.getEntityOwnerUUID(entity), new Location(world, pos));
                }
                return true;
            }
        } catch (Exception e) {
            DimDoors.chat(entity, "There was an exception while teleporting!");
            DimDoors.log.error("Teleporting failed with the following exception: ", e);
        }
        return false;
    }

    public void teleportTo(Entity entity) { // TODO: new velocity angle if !preserveRotation?
        float newYaw = entity.rotationYaw;
        float newPitch = entity.rotationYaw;
        if (!preserveRotation) {
            newYaw = yaw;
            newPitch = pitch;
        }
        TeleportUtils.teleport(entity, new Location(world, pos), newPitch, newYaw);
    }

    public void updateColor() { // TODO: have the registry call this method too
        if (!isRegistered()) {
            color = new RGBA(0, 0, 0, 1);
            return;
        }
        if (destinations.size() == 0) {
            color = new RGBA(0.7f, 0.7f, 0.7f, 1);
            return;
        }
        boolean safe = true;
        for (WeightedRiftDestination weightedDestination : destinations) {
            boolean destSafe = false;
            RiftDestination destination = weightedDestination.getDestination();
            if (destination instanceof PrivateDestination
                || destination instanceof PocketExitDestination
                || destination instanceof PrivatePocketExitDestination) destSafe = true;

            if (!destSafe && destination.getReferencedRift(getLocation()) != null) {
                RiftRegistry.RiftInfo riftInfo = RiftRegistry.getRiftInfo(destination.getReferencedRift(getLocation()));
                destSafe = riftInfo != null
                    && riftInfo.destinations.size() == 1
                    && riftInfo.destinations.iterator().next().equals(getLocation());
            }
            safe &= destSafe;
        }
        if (safe) {
            color = new RGBA(0, 1, 0, 1);
        } else {
            color = new RGBA(1, 0, 0, 1);
        }
    }

    @Override
    public void markDirty() {
        if (!forcedColor) updateColor();
        super.markDirty();
    }

    // Info
    protected abstract boolean isFloating(); // TODO: make non-abstract?

    public Location getLocation() {
        return new Location(world, pos);
    }
}

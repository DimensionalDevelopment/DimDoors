package org.dimdev.dimdoors.shared.tileentities;

import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.RGBA;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.Rift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.rifts.targets.*;
import org.dimdev.pocketlib.VirtualLocation;

import javax.annotation.Nonnull;
import org.dimdev.dimdoors.shared.pockets.PocketTemplate;

@NBTSerializable public abstract class TileEntityRift extends TileEntity implements ITarget, IEntityTarget {

    /*@Saved*/ @Nonnull @Getter /*protected*/ VirtualTarget destination; // How the rift acts as a source
    @Saved @Getter protected LinkProperties properties; // How the rift acts as a target, and properties that affect how it can link to other rifts
    @Saved @Getter protected boolean relativeRotation;
    @Saved @Getter protected boolean alwaysDelete; // Delete the rift when an entrances rift is broken even if the state was changed or destinations link there.
    @Saved @Getter protected boolean forcedColor;
    @Saved @Getter protected RGBA color = null;

    protected boolean riftStateChanged; // not saved

    public TileEntityRift() {
        relativeRotation = true;
        alwaysDelete = false;
    }

    public void copyFrom(TileEntityRift oldRift) {
        relativeRotation = oldRift.relativeRotation;
        properties = oldRift.properties;
        destination = oldRift.destination;
        if (oldRift.isFloating() != isFloating()) updateType();

        markDirty();
    }

    // NBT
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTUtils.readFromNBT(this, nbt);
        destination = nbt.hasKey("destination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompoundTag("destination")) : null;
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if (destination != null) nbt.setTag("destination", destination.writeToNBT(new NBTTagCompound()));
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

    // Modification functions

    public void setRelativeRotation(boolean relativeRotation) {
        this.relativeRotation = relativeRotation;
        markDirty();
    }

    public void setDestination(VirtualTarget destination) {
        if (this.destination != null && isRegistered()) {
            this.destination.unregister();
        }
        this.destination = destination;
        if (destination != null) {
            if (world != null && pos != null) {
                destination.setLocation(new Location(world, pos));
            }
            if (isRegistered()) destination.register();
        }
        riftStateChanged = true;
        markDirty();
        updateColor();
    }

    public void setColor(RGBA color) {
        forcedColor = color != null;
        this.color = color;
        markDirty();
    }

    public void setProperties(LinkProperties properties) {
        this.properties = properties;
        updateProperties();
        markDirty();
    }

    public void markStateChanged() {
        riftStateChanged = true;
        markDirty();
    }

    // Registry TODO: merge most of these into one single updateRegistry() method

    public boolean isRegistered() {
        // The DimensionManager.getWorld(0) != null check is to be able to run this without having to start minecraft
        // (for GeneratePocketSchematics, for example)
        return DimensionManager.getWorld(0) != null && !PocketTemplate.isReplacingPlaceholders() && RiftRegistry.instance().isRiftAt(new Location(world, pos));
    }

    public void register() {
        if (isRegistered()) return;
        Location loc = new Location(world, pos);
        RiftRegistry.instance().addRift(loc);
        if (destination != null) destination.register();
        updateProperties();
        updateColor();
    }

    public void updateProperties() {
        if (isRegistered()) RiftRegistry.instance().setProperties(new Location(world, pos), properties);
        markDirty();
    }

    public void unregister() {
        if (isRegistered()) {
            RiftRegistry.instance().removeRift(new Location(world, pos));
        }
    }

    public void updateType() {
        if (!isRegistered()) return;
        Location loc = new Location(world, pos);
        if(RiftRegistry.instance().isRiftAt(loc)) {
            DimDoors.log.error("No rift at location "+loc+" to update!");
            return;
        }
        Rift rift = RiftRegistry.instance().getRift(loc);
        rift.isFloating = isFloating();
        rift.markDirty();
    }

    public void targetGone(Location location) {
        if (destination.shouldInvalidate(location)) {
            destination = null;
            markDirty();
        }
        updateColor();
    }

    public void sourceGone(Location loc) {
        updateColor();
    }

    // Teleport logic

    public ITarget getTarget() {
        if (destination == null) {
            return new MessageTarget("rifts.unlinked");
        } else {
            destination.setLocation(new Location(world, pos));
            return destination;
        }
    }

    public boolean teleport(Entity entity) {
        riftStateChanged = false;

        // Attempt a teleport
        try {
            IEntityTarget target = getTarget().as(Targets.ENTITY);

            if (target.receiveEntity(entity, getSourceYaw(entity.rotationYaw), getSourcePitch(entity.rotationPitch))) {
                VirtualLocation vloc = VirtualLocation.fromLocation(new Location(entity.world, entity.getPosition()));
                DimDoors.sendTranslatedMessage(entity, "You are at x = " + vloc.getX() + ", y = ?, z = " + vloc.getZ() + ", w = " + vloc.getDepth());
                return true;
            }
        } catch (Exception e) {
            DimDoors.chat(entity, "Something went wrong while trying to teleport you, please report this bug.");
            DimDoors.log.error("Teleporting failed with the following exception: ", e);
        }
        return false;
    }

    public void updateColor() {
        //DimDoors.log.info("Updating color of rift at " + new Location(world, pos));
        if (forcedColor) return;
        if (!isRegistered()) {
            color = new RGBA(0, 0, 0, 1);
        } else if (destination == null) {
            color = new RGBA(0.7f, 0.7f, 0.7f, 1);
        } else {
            destination.setLocation(new Location(world, pos));
            RGBA newColor = destination.getColor();
            if (color == null && newColor != null || !color.equals(newColor)) {
                color = newColor;
                markDirty();
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            }
        }
    }

    // Info
    protected abstract boolean isFloating();

    public abstract float getSourceYaw(float entityYaw);
    public abstract float getSourcePitch(float entityPitch); // Not used for players, but needed for arrows and other entities

    public abstract float getDestinationYaw(float entityYaw);
    public abstract float getDestinationPitch(float entityPitch);
}

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
import org.dimdev.dimdoors.shared.pockets.PocketTemplate;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.Rift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.rifts.targets.*;
import org.dimdev.pocketlib.VirtualLocation;

import java.util.Objects;

import static org.dimdev.dimdoors.DimDoors.log;
import static org.dimdev.dimdoors.shared.rifts.targets.Targets.ENTITY;

@NBTSerializable public abstract class TileEntityRift extends TileEntity implements ITarget, IEntityTarget {

    /*@Saved*/ @Getter /*protected*/ VirtualTarget destination; // How the rift acts as a source
    @Saved @Getter protected LinkProperties properties; // How the rift acts as a target, and properties that affect how it can link to other rifts
    @Saved @Getter protected boolean relativeRotation;
    @Saved @Getter protected boolean alwaysDelete; // Delete the rift when an entrances rift is broken even if the state was changed or destinations link there.
    @Saved @Getter protected boolean forcedColor;
    @Saved @Getter protected RGBA color = null;

    protected boolean riftStateChanged; // not saved

    public TileEntityRift() {
        this.relativeRotation = true;
        this.alwaysDelete = false;
    }
    
    protected Location location() {
        return new Location(this.world,this.pos);
    }

    public void copyFrom(TileEntityRift oldRift) {
        this.relativeRotation = oldRift.relativeRotation;
        this.properties = oldRift.properties;
        this.destination = oldRift.destination;
        if(oldRift.isFloating()!=isFloating()) updateType();
        markDirty();
    }

    // NBT
    @Override public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTUtils.readFromNBT(this, nbt);
        this.destination = nbt.hasKey("destination") ?
                VirtualTarget.readVirtualTargetNBT(nbt.getCompoundTag("destination")) : null;
    }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt = super.writeToNBT(nbt);
        if(Objects.nonNull(this.destination)) nbt.setTag("destination",this.destination.writeToNBT(new NBTTagCompound()));
        return NBTUtils.writeToNBT(this,nbt);
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
        return new SPacketUpdateTileEntity(getPos(),1,serializeNBT());
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
        if(Objects.nonNull(this.destination) && isRegistered()) this.destination.unregister();
        this.destination = destination;
        if(Objects.nonNull(destination)) {
            if(Objects.nonNull(this.world) && Objects.nonNull(this.pos)) destination.setLocation(location());
            if(isRegistered()) destination.register();
        }
        this.riftStateChanged = true;
        markDirty();
        updateColor();
    }

    public void setColor(RGBA color) {
        this.forcedColor = color != null;
        this.color = color;
        markDirty();
    }

    public void setProperties(LinkProperties properties) {
        this.properties = properties;
        updateProperties();
        markDirty();
    }

    public void markStateChanged() {
        this.riftStateChanged = true;
        markDirty();
    }

    // Registry TODO: merge most of these into one single updateRegistry() method

    public boolean isRegistered() {
        // The DimensionManager.getWorld(0) != null check is to be able to run this without having to start minecraft
        // (for GeneratePocketSchematics, for example)
        return Objects.nonNull(DimensionManager.getWorld(0)) &&
               !PocketTemplate.isReplacingPlaceholders() && RiftRegistry.instance().isRiftAt(location());
    }

    public void register() {
        if(isRegistered()) return;
        Location loc = location();
        RiftRegistry.instance().addRift(loc);
        if(Objects.nonNull(this.destination)) this.destination.register();
        updateProperties();
        updateColor();
    }

    public void updateProperties() {
        if(isRegistered()) RiftRegistry.instance().setProperties(location(),this.properties);
        markDirty();
    }

    public void unregister() {
        if(isRegistered()) RiftRegistry.instance().removeRift(location());
    }

    public void updateType() {
        if (!isRegistered()) return;
        Location loc = location();
        if(RiftRegistry.instance().isRiftAt(loc)) {
            log.error("No rift at location {} to update!",loc);
            return;
        }
        Rift rift = RiftRegistry.instance().getRift(loc);
        rift.isFloating = isFloating();
        rift.markDirty();
    }

    public void targetGone(Location location) {
        if(this.destination.shouldInvalidate(location)) {
            this.destination = null;
            markDirty();
        }
        updateColor();
    }

    public void sourceGone(Location loc) {
        updateColor();
    }

    // Teleport logic

    public ITarget getTarget() {
        if(Objects.isNull(this.destination)) return new MessageTarget("rifts.unlinked");
        else {
            this.destination.setLocation(location());
            return this.destination;
        }
    }

    public boolean teleport(Entity entity) {
        this.riftStateChanged = false;
        // Attempt a teleport
        try {
            IEntityTarget target = getTarget().as(ENTITY);
            if(target.receiveEntity(entity, getSourceYaw(entity.rotationYaw), getSourcePitch(entity.rotationPitch))) {
                VirtualLocation vloc = VirtualLocation.fromLocation(new Location(entity.world,entity.getPosition()));
                DimDoors.sendTranslatedMessage(entity,"You are at x = "+vloc.getX()+", y = ?, z = "+vloc.getZ()+
                                                      ", w = "+vloc.getDepth());
                return true;
            }
        } catch (Exception e) {
            DimDoors.chat(entity, "Something went wrong while trying to teleport you, please report this bug.");
            log.error("Teleporting failed with the following exception: ",e);
        }
        return false;
    }

    public void updateColor() {
        //DimDoors.log.info("Updating color of rift at " + new Location(world, pos));
        if(this.forcedColor) return;
        if(!isRegistered()) this.color = new RGBA(0, 0, 0, 1);
        else if(Objects.isNull(this.destination)) this.color = new RGBA(0.7f, 0.7f, 0.7f, 1);
        else {
            this.destination.setLocation(location());
            RGBA newColor = this.destination.getColor();
            if((Objects.isNull(this.color) && Objects.nonNull(newColor)) || !this.color.equals(newColor)) {
                this.color = newColor;
                markDirty();
                this.world.notifyBlockUpdate(this.pos,this.world.getBlockState(this.pos),this.world.getBlockState(this.pos),2);
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

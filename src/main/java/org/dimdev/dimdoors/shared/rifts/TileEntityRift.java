package org.dimdev.dimdoors.shared.rifts;

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
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.*;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.BlockDimensionalDoor;
import org.dimdev.dimdoors.shared.blocks.BlockFloatingRift;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.Rift;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.world.ModDimensions;

import javax.annotation.Nonnull;

@NBTSerializable public abstract class TileEntityRift extends TileEntity implements ITickable { // TODO: implement ITeleportSource and ITeleportDestination

    @Saved @Nonnull @Getter protected RiftDestination destination;
    @Saved @Getter protected boolean relativeRotation;
    @Saved @Getter protected float yaw;
    @Saved @Getter protected float pitch;
    @Saved @Getter protected boolean alwaysDelete; // Delete the rift when an entrances rift is broken even if the state was changed or destinations link there.
    @Saved @Getter protected boolean forcedColor;
    @Saved @Getter protected RGBA color = null;
    @Saved @Getter protected LinkProperties properties;

    protected boolean riftStateChanged; // not saved

    public TileEntityRift() {
        relativeRotation = true;
        pitch = 0;
        alwaysDelete = false;
    }

    public void copyFrom(TileEntityRift oldRift) {
        relativeRotation = oldRift.relativeRotation;
        yaw = oldRift.yaw;
        pitch = oldRift.pitch;
        properties = oldRift.properties;
        if (oldRift.isFloating() != isFloating()) updateType();

        markDirty();
    }

    // NBT
    @Override public void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); NBTUtils.readFromNBT(this, nbt); }

    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { nbt = super.writeToNBT(nbt); return NBTUtils.writeToNBT(this, nbt); }

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

    // Tile entity properties

    // Use vanilla behavior of refreshing only when block changes, not state (otherwise, opening the door would destroy the tile entity)
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        // newState is not accurate if we change the state during onBlockBreak
        newSate = world.getBlockState(pos);
        return oldState.getBlock() != newSate.getBlock() &&
               !(oldState.getBlock() instanceof BlockDimensionalDoor
                 && newSate.getBlock() instanceof BlockFloatingRift);
    }

    // Modification functions

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        markDirty();
    }

    public void setRelativeRotation(boolean relativeRotation) {
        this.relativeRotation = relativeRotation;
        markDirty();
    }

    public void setDestination(RiftDestination destination) {
        if (this.destination != null) {
            this.destination.unregister(new Location(world, pos));
        }
        this.destination = destination;
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
        return RiftRegistry.instance().isRiftAt(new Location(world, pos));
    }

    public void register() {
        if (isRegistered()) return;
        Location loc = new Location(world, pos);
        RiftRegistry.instance().addRift(loc);
        destination.register(new Location(world, pos));
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
        Rift rift = RiftRegistry.instance().getRift(new Location(world, pos));
        rift.isFloating = isFloating();
        rift.markDirty();
    }

    public void targetGone(Location loc) {
        if (!destination.keepAfterTargetGone(new Location(world, pos), loc)) setDestination(null);
        updateColor();
    }

    public void sourceGone(Location loc) {
        updateColor();
    }

    // Teleport logic
    public boolean teleport(Entity entity) {
        riftStateChanged = false;

        // Check that the rift has as destination
        if (destination == null) {
            DimDoors.sendMessage(entity, "This rift has no destination!");
            return false;
        }

        // Attempt a teleport
        try {
            if (destination.teleport(new RotatedLocation(new Location(world, pos), yaw, pitch), entity)) {
                // Set last used rift for players (don't set for other entities to avoid filling the registry too much)
                // TODO: it should maybe be set for some non-player entities too
                if (!ModDimensions.isDimDoorsPocketDimension(WorldUtils.getDim(world)) && entity instanceof EntityPlayer) {
                    RiftRegistry.instance().setOverworldRift(entity.getUniqueID(), new Location(world, pos));
                }
                return true;
            }
        } catch (Exception e) {
            DimDoors.sendMessage(entity, "There was an exception while teleporting!");
            DimDoors.log.error("Teleporting failed with the following exception: ", e);
        }
        return false;
    }

    public void teleportTo(Entity entity, float fromYaw, float fromPitch) {
        if (relativeRotation) {
            TeleportUtils.teleport(entity, new Location(world, pos), yaw + entity.rotationYaw - fromYaw, pitch + entity.rotationPitch - fromPitch);
        } else {
            TeleportUtils.teleport(entity, new Location(world, pos), yaw, pitch);
        }
    }

    public void teleportTo(Entity entity) {
        TeleportUtils.teleport(entity, new Location(world, pos), yaw, pitch);
    }

    public void updateColor() {
        if (forcedColor) return;
        if (!isRegistered()) {
            color = new RGBA(0, 0, 0, 1);
        } else if (destination == null) {
            color = new RGBA(0.7f, 0.7f, 0.7f, 1);
        } else {
            RGBA newColor = destination.getColor(new Location(world, pos));
            if (!color.equals(newColor)) {
                color = newColor;
                markDirty();
            }
        }
    }

    @Override
    public void markDirty() {
        if (!forcedColor) updateColor();
        super.markDirty();
    }

    // Info
    protected abstract boolean isFloating();
}

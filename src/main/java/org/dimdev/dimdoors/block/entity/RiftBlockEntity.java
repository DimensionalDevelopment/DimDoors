package org.dimdev.dimdoors.block.entity;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.AutoSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.rift.targets.*;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

public abstract class RiftBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Target, EntityTarget, AutoSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
    /*@Saved*/ protected VirtualTarget destination; // How the rift acts as a source
    @Saved
    protected LinkProperties properties;
    @Saved
    protected boolean alwaysDelete;
    @Saved
    protected boolean forcedColor;
    @Saved
    protected float[] color = null;

    protected boolean riftStateChanged; // not saved

    public RiftBlockEntity(BlockEntityType<? extends RiftBlockEntity> type) {
        super(type);
        alwaysDelete = false;
    }


    // NBT
    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        super.fromTag(state, nbt);
        AnnotatedNbt.load(this, nbt);
        destination = nbt.contains("destination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompound("destination")) : null;
    }


    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(world.getBlockState(pos), tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        save(tag);
        if (destination != null) tag.put("destination", destination.toTag(new CompoundTag()));
        return tag;
    }

    public void setDestination(VirtualTarget destination) {
        if (this.destination != null && isRegistered()) {
            this.destination.unregister();
        }
        this.destination = destination;
        if (destination != null) {
            if (world != null && pos != null) {
                destination.setLocation(new Location((ServerWorld) world, pos));
            }
            if (isRegistered()) destination.register();
        }
        riftStateChanged = true;
        markDirty();
        updateColor();
    }

    public void setColor(float[] color) {
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

    public boolean isRegistered() {
        return !PocketTemplate.isReplacingPlaceholders() && RiftRegistry.instance(world).isRiftAt(new Location((ServerWorld) world, pos));
    }

    public void register() {
        if (isRegistered()) {
            return;
        }

        Location loc = new Location((ServerWorld) world, pos);
        RiftRegistry.instance(world).addRift(loc);
        if (destination != null) destination.register();
        updateProperties();
        updateColor();
    }

    public void updateProperties() {
        if (isRegistered())
            RiftRegistry.instance(world).setProperties(new Location((ServerWorld) world, pos), properties);
        markDirty();
    }

    public void unregister() {
        if (isRegistered()) {
            RiftRegistry.instance(world).removeRift(new Location((ServerWorld) world, pos));
        }
    }

    public void updateType() {
        if (!isRegistered()) return;
        Rift rift = RiftRegistry.instance(world).getRift(new Location((ServerWorld) world, pos));
        rift.isDetached = isDetached();
        rift.markDirty();
    }

    public void handleTargetGone(Location location) {
        if (destination.shouldInvalidate(location)) {
            destination = null;
            markDirty();
        }

        updateColor();
    }

    public void handleSourceGone(Location location) {
        updateColor();
    }

    public Target getTarget() {
        if (destination == null) {
            return new MessageTarget("rifts.unlinked");
        } else {
            destination.setLocation(new Location((ServerWorld) world, pos));
            return destination;
        }
    }

    public boolean teleport(Entity entity) {
        riftStateChanged = false;

        // Attempt a teleport
        try {
            EntityTarget target = getTarget().as(Targets.ENTITY);

            if (target.receiveEntity(entity, entity.yaw)) {
                VirtualLocation vloc = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos()));
                EntityUtils.chat(entity, new LiteralText("You are at x = " + vloc.x + ", y = ?, z = " + vloc.z + ", w = " + vloc.depth));
                return true;
            }
        } catch (Exception e) {
            EntityUtils.chat(entity, new LiteralText("Something went wrong while trying to teleport you, please report this bug."));
            LOGGER.error("Teleporting failed with the following exception: ", e);
        }

        return false;
    }

    public void updateColor() {
        if (forcedColor) return;
        if (!isRegistered()) {
            color = new float[]{0, 0, 0, 1};
        } else if (destination == null) {
            color = new float[]{0.7f, 0.7f, 0.7f, 1};
        } else {
            destination.setLocation(new Location((ServerWorld) world, pos));
            float[] newColor = destination.getColor();
            if (color == null && newColor != null || !Arrays.equals(color, newColor)) {
                color = newColor;
                markDirty();
            }
        }
    }

    protected abstract boolean isDetached();

    public void copyFrom(DetachedRiftBlockEntity rift) {
        destination = rift.destination;
        properties = rift.properties;
        alwaysDelete = rift.alwaysDelete;
        forcedColor = rift.forcedColor;
    }

    public VirtualTarget getDestination() {
        return destination;
    }

    public LinkProperties getProperties() {
        return properties;
    }

    public boolean isAlwaysDelete() {
        return alwaysDelete;
    }

    public boolean isForcedColor() {
        return forcedColor;
    }

    public float[] getColor() {
        return color;
    }
}

package org.dimdev.dimdoors.block.entity;

import lombok.Getter;
import lombok.ToString;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableText;
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
import org.dimdev.pocketlib.VirtualLocation;
import org.dimdev.util.Location;

import javax.annotation.Nonnull;
import java.util.Arrays;

@ToString
public abstract class RiftBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Target, EntityTarget, AutoSerializable {
    private static final Logger LOGGER = LogManager.getLogger();
    /*@Saved*/ @Nonnull @Getter protected VirtualTarget destination; // How the rift acts as a source
    @Saved @Getter protected LinkProperties properties;
    @Saved @Getter protected boolean alwaysDelete;
    @Saved @Getter protected boolean forcedColor;
    @Saved @Getter protected float[] color = null;

    protected boolean riftStateChanged; // not saved

    public RiftBlockEntity(BlockEntityType<? extends RiftBlockEntity> type) {
        super(type);
        alwaysDelete = false;
    }

    // NBT
    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.load(this, nbt);
        destination = nbt.contains("destination") ? VirtualTarget.readVirtualTargetNBT(nbt.getCompound("destination")) : null;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        fromTag(tag);
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
                destination.setLocation(new Location(world, pos));
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
        return !PocketTemplate.isReplacingPlaceholders() && RiftRegistry.instance().isRiftAt(new Location(world, pos));
    }

    public void register() {
        if (isRegistered()) {
            return;
        }

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
        Rift rift = RiftRegistry.instance().getRift(new Location(world, pos));
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
            destination.setLocation(new Location(world, pos));
            return destination;
        }
    }

    public boolean teleport(Entity entity) {
        riftStateChanged = false;

        // Attempt a teleport
        try {
            EntityTarget target = getTarget().as(Targets.ENTITY);

            if (target.receiveEntity(entity, entity.yaw, entity.pitch)) {
                VirtualLocation vloc = VirtualLocation.fromLocation(new Location(entity.world, entity.getBlockPos()));
                entity.sendMessage(new TranslatableText("You are at x = " + vloc.x + ", y = ?, z = " + vloc.z + ", w = " + vloc.depth));
                return true;
            }
        } catch (Exception e) {
            entity.sendMessage(new TranslatableText("Something went wrong while trying to teleport you, please report this bug."));
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
            destination.setLocation(new Location(world, pos));
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
}

package org.dimdev.dimdoors.block.entity;

import java.util.Objects;

import com.mojang.serialization.Codec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.pockets.PocketTemplate;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.rift.targets.EntityTarget;
import org.dimdev.dimdoors.rift.targets.MessageTarget;
import org.dimdev.dimdoors.rift.targets.Target;
import org.dimdev.dimdoors.rift.targets.Targets;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.EntityUtils;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.NbtUtil;
import org.dimdev.dimdoors.util.RGBA;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;

public abstract class RiftBlockEntity extends BlockEntity implements BlockEntityClientSerializable, Target, EntityTarget {
    private static final Codec<RiftData> CODEC = RiftData.CODEC;
    private static final Logger LOGGER = LogManager.getLogger();
    public static long showRiftCoreUntil = 0;

    protected RiftData data = new RiftData();

    protected boolean riftStateChanged; // not saved

    public RiftBlockEntity(BlockEntityType<? extends RiftBlockEntity> type) {
        super(type);
    }

    // NBT
    @Override
    public void fromTag(BlockState state, CompoundTag nbt) {
        this.deserialize(nbt);
    }

    protected void deserialize(CompoundTag nbt) {
        this.data = NbtUtil.deserialize(nbt.get("data"), CODEC);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        this.serialize(tag);

        return super.toTag(tag);
    }

    protected CompoundTag serialize(CompoundTag tag) {
        if (this.data != null) tag.put("data", NbtUtil.serialize(this.data, CODEC));
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        this.deserialize(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        return this.serialize(tag);
    }

    public void setDestination(VirtualTarget destination) {
        System.out.println("setting Destination " + destination);

        if (this.getDestination() != null && this.isRegistered()) {
            this.getDestination().unregister();
        }
        this.data.setDestination(destination);
        if (destination != null) {
            if (this.world != null && this.pos != null) {
                destination.setLocation(new Location((ServerWorld) this.world, this.pos));
            }
            if (this.isRegistered()) destination.register();
        }
        this.riftStateChanged = true;
        this.markDirty();
        this.updateColor();
    }

    public void setColor(RGBA color) {
        this.data.setColor(color);
        this.markDirty();
    }

    public void setProperties(LinkProperties properties) {
        this.data.setProperties(properties);
        this.updateProperties();
        this.markDirty();
    }

    public void markStateChanged() {
        this.riftStateChanged = true;
        this.markDirty();
    }

    public boolean isRegistered() {
        return !PocketTemplate.isReplacingPlaceholders() && RiftRegistry.instance().isRiftAt(new Location((ServerWorld) this.world, this.pos));
    }

    public void register() {
        if (this.isRegistered()) {
            return;
        }

        Location loc = new Location((ServerWorld) this.world, this.pos);
        RiftRegistry.instance().addRift(loc);
        if (this.data.getDestination() != VirtualTarget.NoneTarget.DUMMY) this.data.getDestination().register();
        this.updateProperties();
        this.updateColor();
    }

    public void updateProperties() {
        if (this.isRegistered())
            RiftRegistry.instance().setProperties(new Location((ServerWorld) this.world, this.pos), this.data.getProperties());
        this.markDirty();
    }

    public void unregister() {
        if (this.isRegistered()) {
            RiftRegistry.instance().removeRift(new Location((ServerWorld) this.world, this.pos));
        }
    }

    public void updateType() {
        if (!this.isRegistered()) return;
        Rift rift = RiftRegistry.instance().getRift(new Location((ServerWorld) this.world, this.pos));
        rift.isDetached = this.isDetached();
        rift.markDirty();
    }

    public void handleTargetGone(Location location) {
        if (this.data.getDestination().shouldInvalidate(location)) {
            this.data.setDestination(null);
            this.markDirty();
        }

        this.updateColor();
    }

    public void handleSourceGone(Location location) {
        this.updateColor();
    }

    public Target getTarget() {
        if (this.data.getDestination() == VirtualTarget.NoneTarget.DUMMY) {
            return new MessageTarget("rifts.unlinked1");
        } else {
            this.data.getDestination().setLocation(new Location((ServerWorld) this.world, this.pos));
            return this.data.getDestination();
        }
    }

    public boolean teleport(Entity entity) {
        this.riftStateChanged = false;

        // Attempt a teleport
        try {
            EntityTarget target = this.getTarget().as(Targets.ENTITY);

            if (target.receiveEntity(entity, entity.yaw)) {
                VirtualLocation vLoc = VirtualLocation.fromLocation(new Location((ServerWorld) entity.world, entity.getBlockPos()));
                EntityUtils.chat(entity, new LiteralText("You are at x = " + vLoc.getX() + ", y = ?, z = " + vLoc.getZ() + ", w = " + vLoc.getDepth()));
                return true;
            }
        } catch (Exception e) {
            EntityUtils.chat(entity, new LiteralText("Something went wrong while trying to teleport you, please report this bug."));
            LOGGER.error("Teleporting failed with the following exception: ", e);
        }

        return false;
    }

    public void updateColor() {
        if (this.data.isForcedColor()) return;
        if (!this.isRegistered()) {
            this.data.setColor(new RGBA(0, 0, 0, 1));
        } else if (this.data.getDestination() == VirtualTarget.NoneTarget.DUMMY) {
            this.data.setColor(new RGBA(0.7f, 0.7f, 0.7f, 1));
        } else {
            this.data.getDestination().setLocation(new Location((ServerWorld) this.world, this.pos));
            RGBA newColor = this.data.getDestination().getColor();
            if (this.data.getColor() == RGBA.NONE && newColor != RGBA.NONE || !Objects.equals(this.data.getColor(), newColor)) {
                this.data.setColor(newColor);
                this.markDirty();
            }
        }
    }

    protected abstract boolean isDetached();

    public void copyFrom(DetachedRiftBlockEntity rift) {
        this.data.setDestination(rift.data.getDestination());
        this.data.setProperties(rift.data.getProperties());
        this.data.setAlwaysDelete(rift.data.isAlwaysDelete());
        this.data.setForcedColor(rift.data.isForcedColor());
    }

    public VirtualTarget getDestination() {
        return this.data.getDestination();
    }

    public LinkProperties getProperties() {
        return this.data.getProperties();
    }

    public boolean isAlwaysDelete() {
        return this.data.isAlwaysDelete();
    }

    public boolean isForcedColor() {
        return this.data.isForcedColor();
    }

    public RGBA getColor() {
        return this.data.getColor();
    }

    public void setData(RiftData data) {
        this.data = data;
    }

    public RiftData getData() {
        return this.data;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}

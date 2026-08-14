package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.rift.targets.MessageTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.Objects;
import java.util.function.Consumer;

import static org.dimdev.dimdoors.DimensionalDoors.LOGGER;

public interface Rift extends Target {

    RiftData getData();

    void setData(RiftData data);

    default void setDestination(VirtualTarget<?> destination) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting destination {} for {}", destination, this.getRiftBlockPos().toShortString());
        }

        var data = getData();

        if (data.getDestination() != null && this.isRegistered()) {
            data.getDestination().unregister();
        }
        data.setDestination(destination);
        if (destination != null && destination != VirtualTarget.NoneTarget.INSTANCE) {
            if (this.getRiftLevel() != null) {
                destination.setLocation(location());
            }
            if (this.isRegistered()) destination.register();
        }
        this.setChanged();
        this.updateColor();
        setStateDirty(true);
    }

    void setStateDirty(boolean riftStat);

    boolean isStateDirty();

    default LinkProperties getProperties() {
        return getData().getProperties();
    }

    default void setProperties(LinkProperties properties) {
        this.getData().setProperties(properties);
        this.updateProperties();
        this.setChanged();
    }

    default void updateProperties() {
        if (this.isRegistered())
            RiftRegistry.getInstance().setProperties(location(), getData().getProperties());
        this.setChanged();
    }

    void setChanged();

    default void markStateChanged() {
        this.setStateDirty(true);
        this.setChanged();
    }

    default boolean isAlwaysDelete() {
        return getData().isAlwaysDelete();
    }

    default boolean isForcedColor() {
        return getData().isForcedColor();
    }

    default RGBA getColor() {
        return getData().getColor();
    }

    default void setColor(RGBA color) {
        this.getData().setColor(color);
        this.setChanged();
    }

    default Target getTarget() {
        var data = getData();

        if (data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
            return new MessageTarget("rifts.unlinked1");
        } else {
            //noinspecti on ConstantConditions
            data.getDestination().setLocation(location());
            return data.getDestination();
        }
    }

    default boolean isRegistered() {
        return this.getRiftLevel() != null && RiftRegistry.getInstance().isRiftAt(location());
    }

    default void register() {
        if (this.isRegistered()) {
            return;
        }

        var data = getData();

        Location loc = location();
        RiftRegistry.getInstance().addRift(loc);
        if (data.getDestination() != VirtualTarget.NoneTarget.INSTANCE) {
            data.getDestination().setLocation(loc);
            data.getDestination().register();
        }
        this.updateProperties();
        this.updateColor();
    }

    default void unregister() {
        if (isDeleteRift() && this.isRegistered()) {
            RiftRegistry.getInstance().removeRift(location());
        }
    }

    default void updateType() {
        if (!this.isRegistered()) return;
        org.dimdev.dimdoors.rift.registry.Rift rift = RiftRegistry.getInstance().getRift(location());
        rift.setDetached(this.isDetached());
        rift.markDirty();
    }

    default void handleSourceMoved(Location location) {
        this.getData().setDestination(location.asTarget());
        this.setChanged();
        this.updateColor();
    }

    default void handleTargetGone(Location location) {
        var data = getData();

        if (data.getDestination().shouldInvalidate(location)) {
            data.setDestination(VirtualTarget.NoneTarget.INSTANCE);
            setChanged();
        }

        this.updateColor();
    }


    default void handleSourceGone(Location location) {
        this.updateColor();
    }

    default void updateColor() {
        var data = getData();

        if (data.isForcedColor()) return;
        if (!isRegistered()) {
            setColor(new RGBA(0, 0, 0, 1));
        } else if (data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
            data.setColor(new RGBA(0.7f, 0.7f, 0.7f, 1));
        } else {
            data.getDestination().setLocation(location());
            RGBA newColor = data.getDestination().getColor();
            if (data.getColor() == null && newColor != null || !Objects.equals(data.getColor(), newColor)) {
                data.setColor(newColor);
                this.setChanged();
            }
        }
    }

    default void copyFrom(Rift rift) {
        this.setData(rift.getData().copy());
    }

    boolean isDetached();

    default void detach() {}

    BlockPos getRiftBlockPos();

    Level getRiftLevel();

    BlockState getRiftBlockState();

    default void tick(Level level, BlockPos pos, BlockState blockState) {
        if (level.isClientSide) return;

        update(level, pos, blockState);
    }

    default void update(Level level, BlockPos pos, BlockState blockState) {

    }

    default org.dimdev.dimdoors.rift.registry.Rift asRift() {
        return RiftRegistry.getInstance().getRift(location());
    }


    void setDeleteRift(boolean deleteRift);

    boolean isDeleteRift();

    default void sync() {
        setChanged();

        var level = getRiftLevel();

        try {
            if(level != null) {
                level.sendBlockUpdated(getRiftBlockPos(), getRiftBlockState(), getRiftBlockState(), 2);
            }
        } catch (UnsupportedOperationException e) {
            LOGGER.warn("Failed to sync rift block entity: {}", e.getMessage());
        }
    }

    default void gatherDebug(Consumer<Component> textConsumer) {
        textConsumer.accept(Component.literal("Size: " + getData().getSize()));
    }

    default Location location() {
        return Location.ofWorld((ServerLevel) this.getRiftLevel(), this.getRiftBlockPos());
    }
}

package org.dimdev.dimdoors.compat.create.block;

import com.mojang.serialization.DataResult;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.block.entity.Rift;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.compat.create.CreateCompatBlockEntityTypes;
import org.dimdev.dimdoors.compat.create.target.KineticTarget;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.MessageTarget;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LiminalCouplerBlockEntity extends GeneratingKineticBlockEntity implements Rift, KineticTarget {
    private RiftData data = new RiftData();
    private boolean deleteRift = true;

    private float targetRotationalSpeed;
    private float targetStressCapacity;

    public LiminalCouplerBlockEntity(BlockPos pos, BlockState state) {
        this(CreateCompatBlockEntityTypes.LIMINAL_COUPLER, pos, state);
    }

    public LiminalCouplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(5);
    }

    @Override
    public void initialize() {
        super.initialize();
        syncTargetKinetics();
        updateGeneratedRotation();
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) {
            return;
        }

        syncTargetKinetics();
    }

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);

        if (data.getDestination() != VirtualTarget.NoneTarget.INSTANCE && level instanceof ServerLevel serverLevel) {
            data.getDestination().setLocation(Location.ofWorld(serverLevel, worldPosition));
        }

        tag.put("data", RiftData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);

        if (tag.contains("data")) {
            DataResult<RiftData> result = RiftData.CODEC.parse(NbtOps.INSTANCE, tag.getCompound("data"));
            data = result.result().map(RiftData::copy).orElseGet(RiftData::new);
        }
    }

    @Override
    public void remove() {
        unregister();
        super.remove();
    }

    @Override
    public float getGeneratedSpeed() {
        if (hasSource()) {
            return 0;
        }

        return targetRotationalSpeed;
    }

    @Override
    public float calculateAddedStressCapacity() {
        float speed = Math.abs(getGeneratedSpeed());
        lastCapacityProvided = speed == 0 ? 0 : targetStressCapacity / speed;
        return lastCapacityProvided;
    }

    @Override
    public float getStressCapacity() {
        return hasNetwork() && hasSource() && getSpeed() != 0 ? capacity : 0;
    }

    @Override
    public float getRotationalSpeed() {
        return hasSource() ? getSpeed() : 0;
    }

    private void syncTargetKinetics() {
        KineticTarget target = resolveTargetKinetics();
        float newSpeed = target.getRotationalSpeed();
        float newCapacity = target.getStressCapacity();

        if (targetRotationalSpeed == newSpeed && targetStressCapacity == newCapacity) {
            return;
        }

        targetRotationalSpeed = newSpeed;
        targetStressCapacity = newCapacity;
        updateGeneratedRotation();
        setChanged();
    }

    private KineticTarget resolveTargetKinetics() {
        Target target = getTarget();
        KineticTarget kineticTarget = target.as(KineticTarget.class);

        if (kineticTarget instanceof KineticBlockEntity kineticBlockEntity && isSameCreateNetwork(kineticBlockEntity)) {
            return EmptyKineticTarget.INSTANCE;
        }

        return kineticTarget;
    }

    private boolean isSameCreateNetwork(KineticBlockEntity other) {
        return level == other.getLevel()
                && hasNetwork()
                && other.hasNetwork()
                && Objects.equals(network, other.network);
    }

    @Override
    public RiftData getData() {
        return data;
    }

    @Override
    public void setData(RiftData data) {
        this.data = data == null ? new RiftData() : data.copy();
        markStateChanged();
    }

    @Override
    public VirtualTarget<?> getDestination() {
        return data.getDestination();
    }

    @Override
    public void setDestination(VirtualTarget<?> destination) {
        if (getDestination() != null && isRegistered()) {
            getDestination().unregister();
        }

        data.setDestination(destination == null ? VirtualTarget.NoneTarget.INSTANCE : destination);

        if (data.getDestination() != VirtualTarget.NoneTarget.INSTANCE && level instanceof ServerLevel serverLevel) {
            data.getDestination().setLocation(Location.ofWorld(serverLevel, worldPosition));
            if (isRegistered()) {
                data.getDestination().register();
            }
        }

        syncTargetKinetics();
        markStateChanged();
        updateColor();
    }

    @Override
    public LinkProperties getProperties() {
        return data.getProperties();
    }

    @Override
    public void setProperties(LinkProperties properties) {
        data.setProperties(properties);
        updateProperties();
        setChanged();
    }

    @Override
    public void updateProperties() {
        if (isRegistered()) {
            DimensionalRegistry.getRiftRegistry().setProperties(location(), data.getProperties());
        }
        setChanged();
    }

    @Override
    public boolean isAlwaysDelete() {
        return data.isAlwaysDelete();
    }

    @Override
    public boolean isForcedColor() {
        return data.isForcedColor();
    }

    @Override
    public RGBA getColor() {
        return data.getColor();
    }

    @Override
    public void setColor(RGBA color) {
        data.setColor(color);
        setChanged();
    }

    @Override
    public Target getTarget() {
        if (data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
            return new MessageTarget("rifts.unlinked1");
        }

        if (level instanceof ServerLevel serverLevel) {
            data.getDestination().setLocation(Location.ofWorld(serverLevel, worldPosition));
        }

        return data.getDestination();
    }

    @Override
    public boolean isRegistered() {
        return level instanceof ServerLevel && DimensionalRegistry.getRiftRegistry().isRiftAt(location());
    }

    @Override
    public void register() {
        if (isRegistered() || !(level instanceof ServerLevel)) {
            return;
        }

        Location location = location();
        DimensionalRegistry.getRiftRegistry().addRift(location);

        if (data.getDestination() != VirtualTarget.NoneTarget.INSTANCE) {
            data.getDestination().setLocation(location);
            data.getDestination().register();
        }

        updateProperties();
        updateColor();
    }

    @Override
    public void unregister() {
        if (deleteRift && isRegistered()) {
            DimensionalRegistry.getRiftRegistry().removeRift(location());
        }
    }

    @Override
    public void updateType() {
        if (!isRegistered()) {
            return;
        }

        org.dimdev.dimdoors.rift.registry.Rift rift = DimensionalRegistry.getRiftRegistry().getRift(location());
        rift.setDetached(false);
        rift.markDirty();
    }

    @Override
    public void markStateChanged() {
        setChanged();
    }

    @Override
    public void handleSourceMoved(Location location) {
        data.setDestination(location.asTarget());
        syncTargetKinetics();
        markStateChanged();
        updateColor();
    }

    @Override
    public void handleTargetGone(Location location) {
        if (data.getDestination().shouldInvalidate(location)) {
            data.setDestination(VirtualTarget.NoneTarget.INSTANCE);
            syncTargetKinetics();
            markStateChanged();
        }

        updateColor();
    }

    @Override
    public void handleSourceGone(Location location) {
        updateColor();
    }

    @Override
    public void updateColor() {
        if (data.isForcedColor()) {
            return;
        }

        if (!isRegistered()) {
            data.setColor(new RGBA(0, 0, 0, 1));
        } else if (data.getDestination() == VirtualTarget.NoneTarget.INSTANCE) {
            data.setColor(new RGBA(0.7f, 0.7f, 0.7f, 1));
        } else {
            data.setColor(data.getDestination().getColor());
        }

        setChanged();
    }

    @Override
    public void copyFrom(Rift rift) {
        setData(rift.getData());
    }

    @Override
    public void setDeleteRift(boolean deleteRift) {
        this.deleteRift = deleteRift;
    }

    @Override
    public void detach() {
        unregister();
    }

    private Location location() {
        return Location.ofWorld((ServerLevel) level, worldPosition);
    }

    private enum EmptyKineticTarget implements KineticTarget {
        INSTANCE;

        @Override
        public float getStressCapacity() {
            return 0;
        }

        @Override
        public float getRotationalSpeed() {
            return 0;
        }

        @Override
        public @Nullable Target receiveOther() {
            return null;
        }
    }
}

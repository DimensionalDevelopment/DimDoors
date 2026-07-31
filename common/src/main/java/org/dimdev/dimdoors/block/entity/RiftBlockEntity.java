package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.jetbrains.annotations.NotNull;

public abstract class RiftBlockEntity extends BlockEntity implements Rift, Target, EntityTarget {
    @NotNull protected RiftData data = new RiftData();
    protected boolean riftStateChanged;
    private boolean deleteRift = true;

    public RiftBlockEntity(BlockEntityType<? extends RiftBlockEntity> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(nbt, provider);
        this.deserialize(nbt);
    }

    public void deserialize(CompoundTag nbt) {
        this.data = RiftData.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("data")).getOrThrow();

        if(nbt.contains("size", Tag.TAG_FLOAT)) {
            this.data.setSize((int) Math.clamp(nbt.getFloat("size"), 0, 200));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(nbt, provider);
        this.serialize(nbt);
    }

    public CompoundTag serialize(CompoundTag nbt) {
        nbt.put("data", RiftData.CODEC.encodeStart(NbtOps.INSTANCE, this.data).getOrThrow());
        return nbt;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        var nbt = super.getUpdateTag(provider);
        nbt.put("data", RiftData.CODEC.encodeStart(NbtOps.INSTANCE, this.data).getOrThrow());
        return nbt;
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public void handleSourceMoved(Location location) {
        this.data.setDestination(location.asTarget());
        this.setChanged();
        this.updateColor();
    }

    public void setData(RiftData data) {
        this.data = data == null ? new RiftData() : data.copy();
    }

    public @NotNull RiftData getData() {
        return this.data;
    }

    @Override
    public void setDeleteRift(boolean deleteRift) {
        this.deleteRift = deleteRift;
    }

    @Override
    public boolean isDeleteRift() {
        return deleteRift;
    }

    @Override
    public boolean isStateDirty() {
        return riftStateChanged;
    }

    @Override
    public void setStateDirty(boolean riftState) {
        this.riftStateChanged = riftState;
    }
}

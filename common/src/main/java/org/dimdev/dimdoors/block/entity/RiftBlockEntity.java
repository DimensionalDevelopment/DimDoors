package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.jetbrains.annotations.NotNull;

public abstract class RiftBlockEntity<T extends RiftBlockEntity<T>> extends BlockEntity implements Rift, Target, EntityTarget {
    @NotNull
    protected RiftData data = new RiftData();
    protected boolean riftStateChanged;
    private boolean deleteRift = true;

    public static final CodecRecord<RiftBlockEntity<?>, RiftData> RIFT_DATA_BUILDER = new CodecRecord<RiftBlockEntity<?>, RiftData>("data", RiftData.CODEC, RiftData::new, RiftBlockEntity::getData);

    public RiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        prepareTag(nbt);

        this.deserialize(Deserialize.create(provider.createSerializationContext(NbtOps.INSTANCE), nbt));
    }

    protected void prepareTag(CompoundTag nbt) {
        if (nbt.contains("size", Tag.TAG_FLOAT)) {
            var data = nbt.contains("data") ? nbt.getCompound("data") : new CompoundTag();
            data.putInt("size", (int) Math.clamp(nbt.getFloat("size"), 0, 200));
            nbt.put("data", data);
        }
    }

    public void deserialize(Deserialize<Tag> nbt) {
        this.data = nbt.get(RIFT_DATA_BUILDER);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.@NotNull Provider provider) {
        Serialize<Tag, T> serialize = Serialize.nbt(nbt, (T) this);

        this.serialize(serialize);
    }

    public void serialize(Serialize<Tag, T> serialize) {
        serialize.put(RIFT_DATA_BUILDER);
    }

    public record Deserialize<K>(K data, DynamicOps<K> ops) {
        public <V, O> O get(CodecRecord<V, O> builder) {
            var value = ops.get(data, builder.name()).flatMap(field -> builder.codec().parse(ops, field));

            if(builder.defaultValue() != null) {
                return value.result().orElseGet(builder.defaultValue());
            } else {
                return value.getOrThrow();
            }
        }

        public static <V> Deserialize<V> create(DynamicOps<V> ops, V object) {
            return new Deserialize<>(object, ops);
        }
    }

    public record Serialize<K, T>(K data, DynamicOps<K> ops, T subject, TriConsumer<String, K, K> consumer) {
        private static void nbtAccept(String name, Tag tag1, Tag data) {
            ((CompoundTag) tag1).put(name, data);
        }

        public <O> void put(CodecRecord<? super T, O> builder) {

            var value = builder.codec().encodeStart(ops, builder.function().apply(subject)).getOrThrow();
            consumer.accept(builder.name(), data, value);
        }

        public static <R> Serialize<Tag, R> nbt(CompoundTag tag, R object) {
            return new Serialize<>(tag, NbtOps.INSTANCE, object, Serialize::nbtAccept);
        }
    }



    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider) {
        var nbt = super.getUpdateTag(provider);

        saveAdditional(nbt, provider);
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

    @Override
    public BlockPos getRiftBlockPos() {
        return getBlockPos();
    }

    @Override
    public BlockState getRiftBlockState() {
        return getBlockState();
    }

    @Override
    public Level getRiftLevel() {
        return getLevel();
    }
}

package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.api.util.Location;

import java.util.UUID;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
    public static final MapCodec<RiftPlaceholder> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId),
            Location.CODEC.fieldOf("location").forGetter(RiftPlaceholder::getLocation)
    ).apply(instance, RiftPlaceholder::new));
    public static final Codec<RiftPlaceholder> CODEC = MAP_CODEC.codec();

    public RiftPlaceholder() {
    }

    private RiftPlaceholder(UUID id, Location location) {
        this();
        this.setId(id);
        this.setLocation(location);
    }

    @Override
    public void sourceGone(RegistryVertex source) {
    }

    @Override
    public void targetGone(RegistryVertex target) {
    }

    @Override
    public void sourceAdded(RegistryVertex source) {
    }

    @Override
    public void targetAdded(RegistryVertex target) {
    }

    @Override
    public void targetChanged(RegistryVertex target) {
    }

    @Override
    public void markDirty() {

    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.RIFT_PLACEHOLDER;
    }

    public static CompoundTag toNbt(RiftPlaceholder vertex) {
        CompoundTag nbt = new CompoundTag();
        nbt.putUUID("id", vertex.id);
        if (vertex.getLocation() != null) nbt.put("location", Location.toNbt(vertex.getLocation()));
        return nbt;
    }

    public static RiftPlaceholder fromNbt(CompoundTag nbt) {
        RiftPlaceholder vertex = new RiftPlaceholder();
        vertex.id = nbt.getUUID("id");
        if (nbt.contains("location")) vertex.setLocation(Location.fromNbt(nbt.getCompound("location")));
        return vertex;
    }
}

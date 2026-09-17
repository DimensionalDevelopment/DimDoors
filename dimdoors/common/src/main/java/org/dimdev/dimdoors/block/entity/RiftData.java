package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.Mth;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;

import java.util.Optional;
import java.util.Set;

public class RiftData {
    public static final Codec<RiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget.INSTANCE).forGetter(RiftData::getDestination),
            LinkProperties.CODEC.optionalFieldOf("properties").forGetter(data -> Optional.ofNullable(data.properties)),
            RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).forGetter(RiftData::getColor),
            Codec.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::isAlwaysDelete),
            Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter(RiftData::isForcedColor),
            Codec.INT.optionalFieldOf("size", 0).forGetter(RiftData::getSize)
    ).apply(instance, (destination, properties, color, alwaysDelete, forcedColor, size) -> {
        RiftData data = new RiftData();
        data.destination = destination;
        data.properties = properties.orElse(null);
        data.color = color;
        data.alwaysDelete = alwaysDelete;
        data.forcedColor = forcedColor;
        data.setSize(size);
        return data;
    }));

    public static final Codec<Holder<RiftData>> HOLDER_CODEC = RegistryFileCodec.create(ModRegistryKeys.RIFT_DATA, CODEC);

    private VirtualTarget<?> destination = VirtualTarget.NoneTarget.INSTANCE; // How the rift acts as a source
    private LinkProperties properties = null;
    private boolean alwaysDelete;
    private boolean forcedColor;
    private int size = 0;
    private RGBA color = RGBA.NONE;

    public RiftData() {
    }

    public RiftData copy() {
        RiftData data = new RiftData();
        data.destination = this.destination == null ? null : this.destination.copy();
        data.properties = this.properties == null ? null : this.properties.toBuilder()
                .groups(this.properties.getGroups() == null ? null : Set.copyOf(this.properties.getGroups()))
                .build();
        data.color = this.color == null ? null : this.color.clone();
        data.alwaysDelete = this.alwaysDelete;
        data.setSize(size);
        data.forcedColor = this.forcedColor;
        return data;
    }

    public VirtualTarget<?> getDestination() {
        return this.destination;
    }

    public void setDestination(VirtualTarget<?> destination) {
        this.destination = destination;
    }

    public LinkProperties getProperties() {
        return this.properties;
    }

    public void setProperties(LinkProperties properties) {
        this.properties = properties;
    }

    public boolean isAlwaysDelete() {
        return this.alwaysDelete;
    }

    public void setAlwaysDelete(boolean alwaysDelete) {
        this.alwaysDelete = alwaysDelete;
    }

    public boolean isForcedColor() {
        return this.forcedColor;
    }

    public void setForcedColor(boolean forcedColor) {
        this.forcedColor = forcedColor;
    }

    public RGBA getColor() {
        return this.color;
    }

    public void setColor(RGBA color) {
        this.forcedColor = color != null;
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = Mth.clamp(size, 0, 200);
    }
}

package org.dimdev.dimdoors.block.entity;

import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.RGBA;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RiftData {
    public static Codec<RiftData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget.DUMMY).forGetter(RiftData::getDestination),
            LinkProperties.CODEC.optionalFieldOf("properties", LinkProperties.NONE).forGetter(RiftData::getProperties),
            Codec.BOOL.fieldOf("alwaysDelete").forGetter(RiftData::isAlwaysDelete),
            Codec.BOOL.fieldOf("forcedColor").forGetter(RiftData::isForcedColor),
            RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).forGetter(RiftData::getColor)
    ).apply(instance, RiftData::new));

    private VirtualTarget destination = VirtualTarget.NoneTarget.DUMMY; // How the rift acts as a source
    private LinkProperties properties = LinkProperties.NONE;
    private boolean alwaysDelete;
    private boolean forcedColor;
    private RGBA color = RGBA.NONE;

    public RiftData() {}

    private RiftData(VirtualTarget destination, LinkProperties properties, boolean alwaysDelete, boolean forcedColor, RGBA color) {
        this.destination = destination;
        this.properties = properties;
        this.alwaysDelete = alwaysDelete;
        this.forcedColor = forcedColor;
        this.color = color;
    }

    public VirtualTarget getDestination() {
        return this.destination;
    }

    public void setDestination(VirtualTarget destination) {
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
}

package org.dimdev.dimdoors.block.entity;

import com.alcatrazescapee.cyanide.codec.Codecs;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.CodecUtils;

import java.util.Optional;
import java.util.function.Function;

public class RiftData {
    public static final Codec<RiftData> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget.INSTANCE).forGetter(RiftData::getDestination),
        LinkProperties.CODEC.optionalFieldOf("properties").forGetter(a -> Optional.ofNullable(a.properties)),
        Codec.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::isAlwaysDelete),
        Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter(RiftData::isForcedColor),
        RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).forGetter(RiftData::getColor)
    ).apply(instance, RiftData::new));

    public static final Codec<Holder<RiftData>> CODEC = RegistryFileCodec.create(ModRegistries.RIFT_DATA, BASE_CODEC);

	private VirtualTarget destination; // How the rift acts as a source
	private LinkProperties properties;
	private boolean alwaysDelete;
	private boolean forcedColor;
	private RGBA color;

    public RiftData(VirtualTarget destination, Optional<LinkProperties> properties, boolean alwaysDelete, boolean forcedColor, RGBA color) {
        this.destination = destination;
        this.properties = properties.orElse(null);
        this.alwaysDelete = alwaysDelete;
        this.forcedColor = forcedColor;
        this.color = color;
    }

	public RiftData() {
        this(VirtualTarget.NoneTarget.INSTANCE, Optional.empty(), false, false, RGBA.NONE);
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
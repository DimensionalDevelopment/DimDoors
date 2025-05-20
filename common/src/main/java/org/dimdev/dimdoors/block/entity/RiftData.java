package org.dimdev.dimdoors.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.RGBA;
import org.dimdev.dimdoors.pockets.PocketLoader;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.CodecUtils;

import java.util.function.Function;
import java.util.function.Supplier;

public class RiftData {
	private VirtualTarget destination; // How the rift acts as a source
	private LinkProperties properties;
	private boolean alwaysDelete;
	private boolean forcedColor;
	private RGBA color;

	public RiftData() {
		this(VirtualTarget.NoneTarget.INSTANCE, null, RGBA.NONE, false, false);
	}

	public RiftData(VirtualTarget destination, LinkProperties properties, RGBA color, boolean alwaysDelete, boolean forcedColor) {
        this.destination = destination;
        this.properties = properties;
        this.color = color;
        this.alwaysDelete = alwaysDelete;
        this.forcedColor = forcedColor;
    }

	public VirtualTarget getDestination() {
		return this.destination;
	}

	public RiftData setDestination(VirtualTarget destination) {
		this.destination = destination;
		return this;
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

	public static final Codec<RiftData> CODEC_BASE = RecordCodecBuilder.create(instance -> instance.group(
			VirtualTarget.CODEC.optionalFieldOf("destination", VirtualTarget.NoneTarget.INSTANCE).forGetter(RiftData::getDestination),
			LinkProperties.CODEC.optionalFieldOf("properties", LinkProperties.NONE).forGetter(RiftData::getProperties),
			RGBA.CODEC.optionalFieldOf("color", RGBA.NONE).forGetter(RiftData::getColor),
			Codec.BOOL.optionalFieldOf("alwaysDelete", false).forGetter(RiftData::isAlwaysDelete),
			Codec.BOOL.optionalFieldOf("forcedColor", false).forGetter(RiftData::isForcedColor)
	).apply(instance, RiftData::new));

	public static final Codec<RiftData> CODEC = CodecUtils.codecWithMapFallback(CODEC_BASE, a -> PocketLoader.getInstance().getRiftData(a));

	public static CompoundTag toNbt(RiftData data) {
		CompoundTag nbt = new CompoundTag();
		if (data.destination != VirtualTarget.NoneTarget.INSTANCE) nbt.put("destination", VirtualTarget.toNbt(data.destination));
		if (data.properties != null) nbt.put("properties", LinkProperties.toNbt(data.properties));
		if (data.color != null) nbt.put("color", RGBA.toNbt(data.color));
		nbt.putBoolean("alwaysDelete", data.alwaysDelete);
		nbt.putBoolean("forcedColor", data.forcedColor);
		return nbt;
	}

	public static RiftData fromNbt(CompoundTag nbt) {
		RiftData data = new RiftData();
		data.destination = nbt.contains("destination") ? VirtualTarget.fromNbt(nbt.getCompound("destination")) : VirtualTarget.NoneTarget.INSTANCE;
		data.properties = nbt.contains("properties") ? LinkProperties.fromNbt(nbt.getCompound("properties")) : null;
		data.alwaysDelete = nbt.getBoolean("alwaysDelete");
		data.forcedColor = nbt.getBoolean("forcedColor");
		data.color = nbt.contains("color") ? RGBA.fromNbt(nbt.getCompound("color")) : RGBA.NONE;
		return data;
	}
}

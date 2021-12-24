package org.dimdev.dimdoors.block.entity;

import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.api.util.RGBA;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;

public class RiftData {
	private VirtualTarget destination = VirtualTarget.NoneTarget.INSTANCE; // How the rift acts as a source
	private LinkProperties properties = null;
	private boolean alwaysDelete;
	private boolean forcedColor;
	private RGBA color = RGBA.NONE;

	public RiftData() {
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

	public RiftData setProperties(LinkProperties properties) {
		this.properties = properties;
		return this;
	}

	public boolean isAlwaysDelete() {
		return this.alwaysDelete;
	}

	public RiftData setAlwaysDelete(boolean alwaysDelete) {
		this.alwaysDelete = alwaysDelete;
		return this;
	}

	public boolean isForcedColor() {
		return this.forcedColor;
	}

	public RiftData setForcedColor(boolean forcedColor) {
		this.forcedColor = forcedColor;
		return this;
	}

	public RGBA getColor() {
		return this.color;
	}

	public RiftData setColor(RGBA color) {
		this.forcedColor = color != null;
		this.color = color;
		return this;
	}

	public static NbtCompound toNbt(RiftData data) {
		NbtCompound nbt = new NbtCompound();
		if (data.destination != VirtualTarget.NoneTarget.INSTANCE) nbt.put("destination", VirtualTarget.toNbt(data.destination));
		if (data.properties != null) nbt.put("properties", LinkProperties.toNbt(data.properties));
		if (data.color != null) nbt.put("color", RGBA.toNbt(data.color));
		nbt.putBoolean("alwaysDelete", data.alwaysDelete);
		nbt.putBoolean("forcedColor", data.forcedColor);
		return nbt;
	}

	public static RiftData fromNbt(NbtCompound nbt) {
		RiftData data = new RiftData();
		data.destination = nbt.contains("destination") ? VirtualTarget.fromNbt(nbt.getCompound("destination")) : VirtualTarget.NoneTarget.INSTANCE;
		data.properties = nbt.contains("properties") ? LinkProperties.fromNbt(nbt.getCompound("properties")) : null;
		data.alwaysDelete = nbt.getBoolean("alwaysDelete");
		data.forcedColor = nbt.getBoolean("forcedColor");
		data.color = nbt.contains("color") ? RGBA.fromNbt(nbt.getCompound("color")) : RGBA.NONE;
		return data;
	}
}

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

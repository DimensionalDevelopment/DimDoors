package org.dimdev.dimdoors.block.entity;

import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import org.dimdev.dimdoors.rift.registry.LinkProperties;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.RGBA;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;

public class RiftData {
	private VirtualTarget destination = null; // How the rift acts as a source
	private LinkProperties properties = null;
	private boolean alwaysDelete;
	private boolean forcedColor;
	private RGBA color = null;

	public RiftData() {
	}

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

	public static CompoundTag toTag(RiftData data) {
		CompoundTag tag = new CompoundTag();
		if (data.destination != null) tag.put("destination", VirtualTarget.toTag(data.destination));
		if (data.properties != null) tag.put("properties", LinkProperties.toTag(data.properties));
		if (data.color != null) tag.put("color", RGBA.toTag(data.color));
		tag.putBoolean("alwaysDelete", data.alwaysDelete);
		tag.putBoolean("forcedColor", data.forcedColor);
		return tag;
	}

	public static RiftData fromTag(CompoundTag tag) {
		RiftData data = new RiftData();
		data.destination = tag.contains("destination") ? VirtualTarget.fromTag(tag.getCompound("destination")) : VirtualTarget.NoneTarget.INSTANCE;
		data.properties = tag.contains("properties") ? LinkProperties.fromTag(tag.getCompound("properties")) : null;
		data.alwaysDelete = tag.getBoolean("alwaysDelete");
		data.forcedColor = tag.getBoolean("forcedColor");
		data.color = tag.contains("color") ? RGBA.fromTag(tag.getCompound("color")) : null;
		return data;
	}

	public WWidget widget() {
		WBox box = new WBox(Axis.VERTICAL);
		WToggleButton alwaysDelete = new WToggleButton().setLabel(new LiteralText("Always Delete")).setOnToggle(this::setAlwaysDelete);
		alwaysDelete.setToggle(this.alwaysDelete);
		WToggleButton forcedColor = new WToggleButton().setLabel(new LiteralText("Forced Color")).setOnToggle(this::setForcedColor);
		forcedColor.setToggle(this.forcedColor);

		box.add(alwaysDelete);
		box.add(forcedColor);

		return box;
	}
}

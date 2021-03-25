package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import net.minecraft.nbt.CompoundTag;

public class Rift extends RegistryVertex {
	private static final Logger LOGGER = LogManager.getLogger();
	private Location location;
	private boolean isDetached;
	private LinkProperties properties;

	public Rift(Location location) {
		this.location = location;
		this.setWorld(location.getWorldId());
	}

	public Rift(Location location, boolean isDetached, LinkProperties properties) {
		this.location = location;
		this.isDetached = isDetached;
		this.properties = properties;
	}

	public Rift(UUID id, Location location, boolean isDetached, LinkProperties properties) {
		this.location = location;
		this.isDetached = isDetached;
		this.properties = properties;
		this.id = id;
	}

	public Rift() {
	}

	@Override
	public void sourceGone(RegistryVertex source) {
		super.sourceGone(source);
		RiftBlockEntity riftTileEntity = (RiftBlockEntity) this.location.getBlockEntity();
		if (source instanceof Rift) {
			riftTileEntity.handleSourceGone(((Rift) source).location);
		}
	}

	@Override
	public void targetGone(RegistryVertex target) {
		super.targetGone(target);
		RiftBlockEntity riftTileEntity = (RiftBlockEntity) this.location.getBlockEntity();
		if (target instanceof Rift) {
			riftTileEntity.handleTargetGone(((Rift) target).location);
		}
		riftTileEntity.updateColor();
	}

	public void targetChanged(RegistryVertex target) {
		LOGGER.debug("Rift " + this + " notified of target " + target + " having changed. Updating color.");
		((RiftBlockEntity) this.location.getBlockEntity()).updateColor();
	}

	public void markDirty() {
		((RiftBlockEntity) this.location.getBlockEntity()).updateColor();
		for (Location location : DimensionalRegistry.getRiftRegistry().getSources(this.location)) {
			DimensionalRegistry.getRiftRegistry().getRift(location).targetChanged(this);
		}
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.RIFT;
	}

	public static CompoundTag toTag(Rift rift) {
		CompoundTag tag = new CompoundTag();
		tag.putUuid("id", rift.id);
		tag.put("location", Location.toTag(rift.location));
		tag.putBoolean("isDetached", rift.isDetached);
		if (rift.properties != null) tag.put("properties", LinkProperties.toTag(rift.properties));
		return tag;
	}

	public static Rift fromTag(CompoundTag tag) {
		Rift rift = new Rift();
		rift.id = tag.getUuid("id");
		rift.location = Location.fromTag(tag.getCompound("location"));
		rift.isDetached = tag.getBoolean("isDetached");
		if (tag.contains("properties")) rift.properties = LinkProperties.fromTag(tag.getCompound("properties"));
		return rift;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isDetached() {
		return isDetached;
	}

	public void setDetached(boolean detached) {
		isDetached = detached;
	}

	public LinkProperties getProperties() {
		return properties;
	}

	public void setProperties(LinkProperties properties) {
		this.properties = properties;
	}
}

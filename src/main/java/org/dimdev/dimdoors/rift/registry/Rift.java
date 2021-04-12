package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

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

	public static NbtCompound toNbt(Rift rift) {
		NbtCompound nbt = new NbtCompound();
		nbt.putUuid("id", rift.id);
		nbt.put("location", Location.toNbt(rift.location));
		nbt.putBoolean("isDetached", rift.isDetached);
		if (rift.properties != null) nbt.put("properties", LinkProperties.toNbt(rift.properties));
		return nbt;
	}

	public static Rift fromNbt(NbtCompound nbt) {
		Rift rift = new Rift();
		rift.id = nbt.getUuid("id");
		rift.location = Location.fromNbt(nbt.getCompound("location"));
		rift.isDetached = nbt.getBoolean("isDetached");
		if (nbt.contains("properties")) rift.properties = LinkProperties.fromNbt(nbt.getCompound("properties"));
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

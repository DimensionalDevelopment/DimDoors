package org.dimdev.dimdoors.rift.registry;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.Optional;
import java.util.UUID;

public class Rift extends RegistryVertex {
	public static final MapCodec<Rift> CODEC = RecordCodecBuilder.mapCodec(a -> commonRiftFields(a).apply(a, Rift::new));

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

	public Rift(UUID id, Optional<Location> location, boolean isDetached, Optional<LinkProperties> properties) {
		this.location = location.orElse(null);
		this.isDetached = isDetached;
		this.properties = properties.orElse(null);
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

		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) {
			if (target instanceof Rift) {
				riftBlockEntity.handleTargetGone(((Rift) target).location);
			}
			riftBlockEntity.updateColor();
		}
	}

	public void targetChanged(RegistryVertex target) {
		LOGGER.debug("Rift " + this + " notified of target " + target + " having changed. Updating color.");
		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) riftBlockEntity.updateColor();
	}

	public void markDirty() {
		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) riftBlockEntity.updateColor();

		for (Location location : DimensionalRegistry.getRiftRegistry().getSources(this.location)) {
			DimensionalRegistry.getRiftRegistry().getRift(location).targetChanged(this);
		}
	}

	private void updateColor() {
		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) riftBlockEntity.updateColor();
	}

	@Override
	public RegistryVertexType<? extends RegistryVertex> getType() {
		return RegistryVertexType.RIFT.get();
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

	public static <T extends Rift> Products.P4<RecordCodecBuilder.Mu<T>, UUID, Optional<Location>, Boolean, Optional<LinkProperties>> commonRiftFields(RecordCodecBuilder.Instance<T> inst) {
		return inst.group(
						UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId),
						Location.CODEC.optionalFieldOf("location").forGetter(rift -> Optional.ofNullable(rift.getLocation())),
						Codec.BOOL.optionalFieldOf("isDetached", false).forGetter(Rift::isDetached),
						LinkProperties.CODEC.optionalFieldOf("properties").forGetter(rift -> Optional.ofNullable(rift.getProperties())));
	}
}

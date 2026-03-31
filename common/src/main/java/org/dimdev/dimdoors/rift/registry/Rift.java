package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.Optional;
import java.util.UUID;

public class Rift extends RegistryVertex {
    public static final MapCodec<Rift> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
            .and(Location.CODEC.fieldOf("location").forGetter(a -> a.location))
            .and(Codec.BOOL.optionalFieldOf("isDetached", false).forGetter(a -> a.isDetached))
            .and(LinkProperties.CODEC.optionalFieldOf("properties").forGetter(a -> Optional.ofNullable(a.properties)))
            .apply(instance, Rift::new)
    );

	private static final Logger LOGGER = LogManager.getLogger();
	private Location location;
	private boolean isDetached;
	private LinkProperties properties;

	public Rift(Location location) {
        super();
		this.location = location;
	}

	public Rift(Location location, boolean isDetached, LinkProperties properties) {
        super();
		this.location = location;
		this.isDetached = isDetached;
		this.properties = properties;
	}

	public Rift(UUID id, Location location, boolean isDetached, LinkProperties properties) {
        super(id);
		this.location = location;
		this.isDetached = isDetached;
		this.properties = properties;
	}

	public Rift() {
        super();
	}

    public Rift(UUID id) {
        super(id);
    }


    public Rift(UUID id, Location location, boolean isDetached, Optional<LinkProperties> properties) {
        super(id);
        this.location = location;
        this.isDetached = isDetached;
        this.properties = properties.orElse(null);
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
        LOGGER.debug("Rift {} notified of target {} having changed. Updating color.", this, target);
		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) riftBlockEntity.updateColor();
	}

	public void markDirty() {
		if(this.location.getBlockEntity() instanceof RiftBlockEntity riftBlockEntity) riftBlockEntity.updateColor();

		for (Location location : DimensionalRegistry.getRiftRegistry().getTargets(this.location)) {
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

	public static CompoundTag toNbt(Rift rift) {
		CompoundTag nbt = new CompoundTag();
		nbt.putUUID("id", rift.id);
		nbt.put("location", Location.toNbt(rift.location));
		nbt.putBoolean("isDetached", rift.isDetached);
		if (rift.properties != null) nbt.put("properties", LinkProperties.toNbt(rift.properties));
		return nbt;
	}

	public static Rift fromNbt(CompoundTag nbt) {
		Rift rift = new Rift();
		rift.id = nbt.getUUID("id");
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
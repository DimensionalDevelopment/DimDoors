package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.RiftUtils;

import java.util.Optional;
import java.util.UUID;

public class Rift extends RegistryVertex {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final MapCodec<Rift> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId),
            Location.CODEC.fieldOf("location").forGetter(Rift::getLocation),
            Codec.BOOL.optionalFieldOf("isDetached", false).forGetter(Rift::isDetached),
            LinkProperties.CODEC.optionalFieldOf("properties").forGetter(rift -> Optional.ofNullable(rift.getProperties()))
    ).apply(instance, Rift::new));
    public static final Codec<Rift> CODEC = MAP_CODEC.codec();

    private Location location;
    private boolean isDetached;
    private LinkProperties properties;

    public Rift(Location location) {
        this.location = location;
        this.setWorld(location.getWorldId());
    }

    public Rift(Location location, boolean isDetached, LinkProperties properties) {
        this.location = location;
        this.setWorld(location.getWorldId());
        this.isDetached = isDetached;
        this.properties = properties;
    }

    public Rift(UUID id, Location location, boolean isDetached, LinkProperties properties) {
        this.location = location;
        this.setWorld(location.getWorldId());
        this.isDetached = isDetached;
        this.properties = properties;
        this.id = id;
    }

    private Rift(UUID id, Location location, boolean isDetached, Optional<LinkProperties> properties) {
        this(id, location, isDetached, properties.orElse(null));
    }

    public Rift() {
    }

    @Override
    public void sourceGone(RegistryVertex source) {
        super.sourceGone(source);

        RiftUtils.runIfRiftAt(location, rift -> {
            if (source instanceof Rift sourceRift) {
                rift.handleSourceGone(sourceRift.location);
            }
        });
    }

    @Override
    public void targetGone(RegistryVertex target) {
        super.targetGone(target);

        RiftUtils.runIfRiftAt(location, rift -> {
            if (target instanceof Rift targetRift) {
                rift.handleTargetGone(targetRift.location);
            }
            rift.updateColor();
        });
    }

    @Override
    public void targetMoved(RegistryVertex target) {
        super.sourceAdded(target);

        RiftUtils.runIfRiftAt(location, rift -> {
            if (target instanceof Rift) {
                rift.handleSourceMoved(((Rift) target).location);
            }

            rift.updateColor();
        });
    }

    public void targetChanged(RegistryVertex target) {
        LOGGER.debug("Rift {} notified of target {} having changed. Updating color.", this, target);
        RiftUtils.runIfRiftAt(location, org.dimdev.dimdoors.block.entity.Rift::updateColor);
    }

    public void markDirty() {
        RiftUtils.runIfRiftAt(location, org.dimdev.dimdoors.block.entity.Rift::updateColor);

        for (Location location : RiftRegistry.getInstance().getTargets(this.location)) {
            RiftRegistry.getInstance().getRift(location).targetChanged(this);
        }
    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.RIFT;
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
        rift.setLocation(Location.fromNbt(nbt.getCompound("location")));
        rift.isDetached = nbt.getBoolean("isDetached");
        if (nbt.contains("properties")) rift.properties = LinkProperties.fromNbt(nbt.getCompound("properties"));
        return rift;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (location != null) this.setWorld(location.getWorldId());
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

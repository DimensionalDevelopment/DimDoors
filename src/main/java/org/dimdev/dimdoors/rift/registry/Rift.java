package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.block.entity.RiftData;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.RGBA;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class Rift extends RegistryVertex {
    private static final Logger LOGGER = LogManager.getLogger();
    public Location location;
    public boolean isDetached;
    public LinkProperties properties;

    public Rift(Location location) {
        this.location = location;
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
        for (Location location : RiftRegistry.instance().getSources(this.location)) {
            RiftRegistry.instance().getRift(location).targetChanged(this);
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
        tag.put("properties", LinkProperties.toTag(rift.properties));
        return tag;
    }

    public static Rift fromTag(CompoundTag tag) {
        Rift rift = new Rift();
        rift.id = tag.getUuid("id");
        rift.location = Location.fromTag(tag.getCompound("location"));
        rift.isDetached = tag.getBoolean("isDetached");
        rift.properties = LinkProperties.fromTag(tag.getCompound("properties"));;
        return rift;
    }
}

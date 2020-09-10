package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;

public class Rift extends RegistryVertex {
    public static final Codec<Rift> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                DynamicSerializableUuid.field_25122.fieldOf("id").forGetter(a -> a.id),
                Location.CODEC.fieldOf("location").forGetter(a -> a.location),
                Codec.BOOL.fieldOf("isDetached").forGetter(a -> a.isDetached),
                LinkProperties.CODEC.fieldOf("properties").forGetter(a -> a.properties)
        ).apply(instance, (id, location, isDetached, properties) -> {
            Rift pointer = new Rift(location, isDetached, properties);
            pointer.id = id;
            return pointer;
        });
    });


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

    public Rift() {
    }

    @Override
    public void sourceGone(RegistryVertex source) {
        super.sourceGone(source);
        RiftBlockEntity riftTileEntity = (RiftBlockEntity) location.getBlockEntity();
        if (source instanceof Rift) {
            riftTileEntity.handleSourceGone(((Rift) source).location);
        }
    }

    @Override
    public void targetGone(RegistryVertex target) {
        super.targetGone(target);
        RiftBlockEntity riftTileEntity = (RiftBlockEntity) location.getBlockEntity();
        if (target instanceof Rift) {
            riftTileEntity.handleTargetGone(((Rift) target).location);
        }
        riftTileEntity.updateColor();
    }

    public void targetChanged(RegistryVertex target) {
        LOGGER.debug("Rift " + this + " notified of target " + target + " having changed. Updating color.");
        ((RiftBlockEntity) location.getBlockEntity()).updateColor();
    }

    public void markDirty() {
        ((RiftBlockEntity) location.getBlockEntity()).updateColor();
        for (Location location : RiftRegistry.instance().getSources(location)) {
            RiftRegistry.instance().getRift(location).targetChanged(this);
        }
    }

    @Override
    public RegistryVertexType<? extends RegistryVertex> getType() {
        return RegistryVertexType.RIFT;
    }
}

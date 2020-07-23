package org.dimdev.dimdoors.rift.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.block.entity.RiftBlockEntity;
import org.dimdev.dimdoors.util.Location;

import net.minecraft.nbt.CompoundTag;

public class Rift extends RegistryVertex {
    private static final Logger LOGGER = LogManager.getLogger();
    @Saved
    public Location location;
    @Saved
    public boolean isDetached;
    @Saved
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
        for (Location location : RiftRegistry.instance(world).getSources(location)) {
            RiftRegistry.instance(world).getRift(location).targetChanged(this);
        }
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        AnnotatedNbt.load(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        AnnotatedNbt.save(this, nbt);
        return nbt;
    }
}

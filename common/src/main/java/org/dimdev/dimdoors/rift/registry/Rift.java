package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.UUID;

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
        org.dimdev.dimdoors.block.entity.Rift riftTileEntity = (org.dimdev.dimdoors.block.entity.Rift) this.location.getBlockEntity();
        if (source instanceof Rift) {
            riftTileEntity.handleSourceGone(((Rift) source).location);
        }
    }

    @Override
    public void targetGone(RegistryVertex target) {
        super.targetGone(target);

        if (this.location.getBlockEntity() instanceof org.dimdev.dimdoors.block.entity.Rift riftBlockEntity) {
            if (target instanceof Rift) {
                riftBlockEntity.handleTargetGone(((Rift) target).location);
            }
            riftBlockEntity.updateColor();
        }
    }

    @Override
    public void targetMoved(RegistryVertex target) {
        super.sourceAdded(target);

        if (this.location.getBlockEntity() instanceof org.dimdev.dimdoors.block.entity.Rift riftBlockEntity) {
            if (target instanceof Rift) {
                riftBlockEntity.handleSourceMoved(((Rift) target).location);
            }
            riftBlockEntity.updateColor();
        }
    }

    public void targetChanged(RegistryVertex target) {
        LOGGER.debug("Rift " + this + " notified of target " + target + " having changed. Updating color.");
        if (this.location.getBlockEntity() instanceof org.dimdev.dimdoors.block.entity.Rift riftBlockEntity) riftBlockEntity.updateColor();
    }

    public void markDirty() {
        if (this.location.getBlockEntity() instanceof org.dimdev.dimdoors.block.entity.Rift riftBlockEntity) riftBlockEntity.updateColor();

        for (Location location : DimensionalRegistry.getRiftRegistry().getTargets(this.location)) {
            DimensionalRegistry.getRiftRegistry().getRift(location).targetChanged(this);
        }
    }

    private void updateColor() {
        if (this.location.getBlockEntity() instanceof org.dimdev.dimdoors.block.entity.Rift riftBlockEntity) riftBlockEntity.updateColor();
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

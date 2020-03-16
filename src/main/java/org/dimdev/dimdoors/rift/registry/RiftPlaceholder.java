package org.dimdev.dimdoors.rift.registry;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RiftPlaceholder extends Rift { // TODO: don't extend rift
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void sourceGone(RegistryVertex source) {}

    @Override
    public void targetGone(RegistryVertex target) {}

    @Override
    public void sourceAdded(RegistryVertex source) {}

    @Override
    public void targetAdded(RegistryVertex target) {}

    @Override
    public void targetChanged(RegistryVertex target) {}

    @Override
    public void markDirty() {
        RiftRegistry.instance().markSubregistryDirty(world);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        LOGGER.error("Reading a rift placeholder from NBT!");
        super.fromTag(nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        LOGGER.error("Writing a rift placeholder from NBT!");
        return super.toTag(nbt);
    }
}

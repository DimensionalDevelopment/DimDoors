package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;

public class LimboTarget extends VirtualTarget implements EntityTarget {
    public LimboTarget() {
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt = super.toTag(nbt);
        return nbt;
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        //FabricDimensions.teleport(entity, entity.getServer().getWorld(LIMBO));
        return true;
    }
}

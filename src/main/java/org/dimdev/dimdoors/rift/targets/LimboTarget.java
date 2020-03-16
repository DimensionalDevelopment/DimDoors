package org.dimdev.dimdoors.rift.targets;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.world.ModDimensions;

public class LimboTarget extends VirtualTarget implements EntityTarget {
    public LimboTarget() {}

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
        FabricDimensions.teleport(entity, ModDimensions.LIMBO);
        return true;
    }
}

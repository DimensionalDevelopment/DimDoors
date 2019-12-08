package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.world.limbo.LimboDimension;
import org.dimdev.util.TeleportUtil;

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
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        TeleportUtil.teleport(entity, LimboDimension.getLimboSkySpawn(entity));
        return true;
    }
}

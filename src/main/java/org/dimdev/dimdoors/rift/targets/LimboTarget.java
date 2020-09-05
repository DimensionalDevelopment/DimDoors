package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.util.TeleportUtil;

public class LimboTarget extends VirtualTarget implements EntityTarget {
    public static final Codec<LimboTarget> CODEC = Codec.unit(LimboTarget::new);

    public LimboTarget() {
    }

    @Override
    public boolean receiveEntity(Entity entity, float yawOffset) {
        //FabricDimensions.teleport(entity, entity.getServer().getWorld(LIMBO));
        return true;
    }

    @Override
    public VirtualTargetType<? extends VirtualTarget> getType() {
        return VirtualTargetType.LIMBO;
    }
}

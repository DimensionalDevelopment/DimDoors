package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.RedstoneTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.block.LiminalTransmitterBlock;
import org.dimdev.dimdoors.rift.targets.LocationProvider;
import org.dimdev.dimdoors.rift.targets.Targets;

public class LiminalTransmitterBlockEntity extends RiftBlockEntity<LiminalTransmitterBlockEntity> {
    public LiminalTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LIMINAL_TRANSMITTER, pos, state);
    }

    public boolean sendSignal(int strength) {
        Target target = getTarget();
        if (target == null) return false;

        Location location = target instanceof LocationProvider provider ? provider.getLocation() : null;

        RedstoneTarget redstoneTarget = target.as(Targets.REDSTONE);
        if (redstoneTarget == null) return false;

        return redstoneTarget.recieveSignal(strength, location);
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        return false;
    }

    @Override
    public boolean isDetached() {
        return false;
    }
}

package org.dimdev.dimdoors.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.rift.registry.DialingAddress;
import org.dimdev.dimdoors.rift.targets.DialingTarget;
import org.dimdev.dimdoors.rift.targets.DialingTargetImpl;
import org.dimdev.dimdoors.util.Copyable;

public class DialingDoorBlockEntity extends EntranceRiftBlockEntity<DialingDoorBlockEntity> implements DialingTarget, Copyable<DialingTarget> {
    private static final CodecRecord<DialingDoorBlockEntity, DialingAddress> DIALING_ADDRESS_BUILDER = new CodecRecord<>("address", DialingAddress.CODEC, DialingAddress.DEFAULT, a -> a.address);

    private DialingAddress address = DialingAddress.DEFAULT;

    protected DialingDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.DIALING_DOOR, pos, state);
    }

    @Override
    public void serialize(Serialize<Tag, DialingDoorBlockEntity> serialize) {
        super.serialize(serialize);
        serialize.put(DIALING_ADDRESS_BUILDER);
    }

    @Override
    public void deserialize(Deserialize<Tag> nbt) {
        super.deserialize(nbt);
        this.address = nbt.get(DIALING_ADDRESS_BUILDER);
    }

    public void turnDial(DialingAddress.DialType type) {
        setAddress(address.turnDial(type));
    }

    @Override
    public Target getTarget() {
        return this;
    }

    @Override
    public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity, Location location) {
        if (location != null) {
            return super.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);
        }

        return DialingTarget.super.receiveEntity(entity, relativePos, relativeAngle, relativeVelocity, location);
    }

    public DialingAddress getAddress() {
        return address;
    }

    @Override
    public Location getLocation() {
        return Location.ofWorld((ServerLevel) level, getBlockPos());
    }

    public void setAddress(DialingAddress address) {
        this.address = address;
        sync();
    }

    @Override
    public DialingTarget copy() {
        return new DialingTargetImpl(getAddress());
    }
}

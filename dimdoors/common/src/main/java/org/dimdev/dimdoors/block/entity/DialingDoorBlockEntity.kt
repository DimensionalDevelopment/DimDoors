package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.targets.DialingTarget
import org.dimdev.dimdoors.rift.targets.DialingTargetImpl
import org.dimdev.dimdoors.util.Copyable

class DialingDoorBlockEntity(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<DialingDoorBlockEntity>(ModBlockEntityTypes.DIALING_DOOR, pos, state), DialingTarget, Copyable<DialingTarget> {
    override var address = DialingAddress.DEFAULT

    override fun serialize(serialize: Serialize<Tag, DialingDoorBlockEntity>) {
        super.serialize(serialize)
        serialize.put(DIALING_ADDRESS_BUILDER)
    }

    override fun deserialize(nbt: Deserialize<Tag>) {
        super.deserialize(nbt)
        this.address = nbt.get(DIALING_ADDRESS_BUILDER)
    }

    fun turnDial(type: DialingAddress.DialType) {
        updateAddress(address.turnDial(type))
    }

    override val target: Target get() = this

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        if (location != null) {
            return super<EntranceRiftBlockEntity>.receiveEntity(owner
                , entity,
                relativePos,
                relativeAngle,
                relativeVelocity,
                location
            )
        }

        return super<DialingTarget>.receiveEntity(owner, entity, relativePos, relativeAngle, relativeVelocity, location)
    }

    fun updateAddress(address: DialingAddress) {
        this.address = address
        sync()
    }

    override fun copy(): DialingTarget {
        return DialingTargetImpl(address)
    }

    companion object {
        private val DIALING_ADDRESS_BUILDER = CodecRecord<DialingDoorBlockEntity, DialingAddress>(
            "address",
            DialingAddress.CODEC,
            DialingAddress.DEFAULT,
            DialingDoorBlockEntity::address
        )
    }
}

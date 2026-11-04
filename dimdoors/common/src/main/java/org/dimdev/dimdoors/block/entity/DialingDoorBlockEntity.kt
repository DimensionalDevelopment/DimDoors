package org.dimdev.dimdoors.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimdoors.api.rift.target.Target
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.registry.DialingAddress
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.targets.DialingTarget
import org.dimdev.dimdoors.rift.targets.DialingTargetImpl
import org.dimdev.dimdoors.util.Copyable

class DialingDoorBlockEntity(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<DialingDoorBlockEntity>(ModBlockEntityTypes.DIALING_DOOR, pos, state), DialingTarget, Copyable<DialingTarget> {
    override var address = DialingAddress.DEFAULT


    override fun <T> writeUniqueData(serialize: Serialize<T, out DialingDoorBlockEntity>) {
        super.writeUniqueData(serialize)
        serialize.put(DIALING_ADDRESS_BUILDER)
    }

    override fun <T> readUniqueData(deserialize: Deserialize<T>) {
        super.readUniqueData(deserialize)
        this.address = deserialize.get(DIALING_ADDRESS_BUILDER)
    }

    override fun gatherFieldNodes(list: MutableList<FieldNode>) {
        super.gatherFieldNodes(list)
        list.add(DIALING_ADDRESS_BUILDER.fieldNode)
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
        private val DIALING_ADDRESS_BUILDER = CodecRecord(
            "address",
            DialingAddress.CODEC,
            DialingAddress.DEFAULT,
            DialingDoorBlockEntity::address,
            DialingDoorBlockEntity::updateAddress
        )
    }
}

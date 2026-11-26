package org.dimdev.dimdoors.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Rotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.errata.schema.FieldNode
import org.dimdev.dimdoors.item.IntrinsicRift
import org.dimdev.dimdoors.rift.targets.DialingTarget
import org.dimdev.dimdoors.rift.targets.DialingTargetImpl
import org.dimdev.dimdoors.subsystem.graph.Vertex
import org.dimdev.dimdoors.subsystem.player.DialingAddress
import org.dimdev.dimdoors.util.Copyable
import org.dimdev.dimdoors.util.Location

class DialingDoorBlockEntity(pos: BlockPos, state: BlockState) : EntranceRiftBlockEntity<DialingDoorBlockEntity>(ModBlockEntityTypes.DIALING_DOOR, pos, state), DialingTarget, Copyable<DialingTarget>, IntrinsicRift {
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

    override fun target(holder: Location) = this

    override fun detach() {}

    override fun receiveEntity(
        owner: Vertex,
        entity: Entity,
        relativePos: Vec3,
        relativeAngle: Rotations,
        relativeVelocity: Vec3,
        location: Location?
    ): Boolean {
        if (location != null && owner.providedLocation != location) {
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

    override val name = "Dialing Door"

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

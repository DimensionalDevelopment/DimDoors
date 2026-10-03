package org.dimdev.dimcore.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import org.dimdev.dimcore.api.ext.castOrNull

abstract class TransferBridgeImpl : TransferBridge {
    private val bindings = mutableMapOf<TransferType<*>, Binding<*>>()

    override fun <U : Unit<U>> find(type: TransferType<U>, level: Level?, pos: BlockPos?, side: Direction?): Handle<U>? =
        if (level == null || pos == null) null else type.binding()?.find(level, pos, side)

    interface Binding<U : Unit<U>> {
        val type: TransferType<U>
        fun find(level: Level, pos: BlockPos, side: Direction?): Handle<U>?
        fun register(blockEntitytType: BlockEntityType<*>)
    }

    override fun <U : Unit<U>> declare(type: TransferType<U>, blockEntityType: BlockEntityType<*>) {
        type.binding()?.register(blockEntityType)
    }

    fun <U : Unit<U>, H> bind(
        type: TransferType<U>,
        lookup: Lookup<H, *>,
        toHandle: (TransferType<U>, H) -> Handle<U>,
        toHandler: (TransferType<U>, Handle<U>) -> H
    ) {
        bindings[type] = object : Binding<U> {
            override val type: TransferType<U> = type

            override fun find(level: Level, pos: BlockPos, side: Direction?): Handle<U>? = lookup.find(level, pos, side)?.let { toHandle(type, it) }

            override fun register(blockEntitytType: BlockEntityType<*>) = lookup.register(blockEntitytType) { be, side -> type.expose(be, side)?.let { toHandler(type, it) } }
        }
    }

    private fun <U : Unit<U>> TransferType<U>.binding(): Binding<U>? = bindings[this]?.castOrNull()
}

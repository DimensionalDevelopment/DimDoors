package org.dimdev.dimcore.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType

interface TransferBridge {
    fun <U : Unit<U>> find(type: TransferType<U>, level: Level?, pos: BlockPos?, side: Direction?): Handle<U>?
    fun interactWithFluid(player: Player?, hand: InteractionHand?, level: Level?, pos: BlockPos?, side: Direction?): Boolean
    fun <U : Unit<U>> declare(type: TransferType<U>, blockEntityType: BlockEntityType<*>)
}

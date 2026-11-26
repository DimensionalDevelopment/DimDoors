package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.blockentity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.rift.Rift
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.sound.ModSoundEvents

class RiftMoverItem(properties: Properties) : PortableRiftItem(properties) {
    override fun getDescriptionId(stack: ItemStack): String = this.descriptionId

    override fun canUse(stack: ItemStack) = true

    override fun useOn(level: ServerLevel, player: Player, stack: ItemStack, pos: BlockPos, facing: Direction, state: BlockState): InteractionResult {
        return if (ItemRiftRegistry.rift(stack) != null) {
            var placed: BlockEntityRift<*>

            val provider = state.block.castOrNull<RiftVariantProvider>()?.takeIf(RiftVariantProvider::isBase)

            if(provider == null) {
                val pos = pos.relative(facing)
                if (!level.getBlockState(pos).canBeReplaced()) return InteractionResult.FAIL

                level.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState())
                placed = level.getBlockEntity(pos)?.castOrNull<DetachedRiftBlockEntity>() ?: return InteractionResult.FAIL
            } else {
                val pos = provider.getRiftPos(level, pos, state)
                placed = provider.convertToRiftProvider(level, pos, level.getBlockState(pos)) ?: return InteractionResult.FAIL

            }

            val holder = ItemRiftRegistry.rift(stack) ?: return InteractionResult.FAIL

            Rift.transfer(stack, holder, this, placed, placed.location(), placed)

            level.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_END, SoundSource.BLOCKS, 0.6f, 1f)

            InteractionResult.CONSUME
        }
        else super.useOn(level, player, stack, pos, facing, state)
    }


    override val name: String
        get() = "Rift Mover"
}
package org.dimdev.dimdoors.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.block.RiftVariantProvider

class PortableRiftUseOnBlockListener : UseItemOnBlockCallback {
    override fun useItemOnBlock(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        val stack = player.getItemInHand(hand)
        if (stack.item !is PortableRiftItem || !stack.has(ModDataComponentTypes.TRACKED_ITEM)) return InteractionResult.PASS
        if (world.getBlockState(hitResult.blockPos).block.castOrNull<RiftVariantProvider>()?.isBase() != true) return InteractionResult.PASS

        return stack.useOn(UseOnContext(player, hand, hitResult))
    }
}

package org.dimdev.dimdoors.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.block.DetachedRiftBlock
import org.dimdev.dimdoors.block.DimensionalPortalBlock
import org.dimdev.dimdoors.block.RiftVariantProvider
import org.dimdev.dimdoors.tag.ModItemTags

class RiftUseOnBlockListener : UseItemOnBlockCallback {
    override fun useItemOnBlock(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        val stack = player.getItemInHand(hand)
        val block = world.getBlockState(hitResult.blockPos).block

        if (stack.item is ItemRift && stack.has(ModDataComponentTypes.TRACKED_ITEM) && block.castOrNull<RiftVariantProvider>()?.isBase() == true) return stack.useOn(UseOnContext(player, hand, hitResult))

        if (block !is DetachedRiftBlock && block !is DimensionalPortalBlock) return InteractionResult.PASS
        if (stack.isEmpty || stack.`is`(ModItemTags.DETACH_RIFT_INTERACTING)) return InteractionResult.PASS

        val offhand = player.offhandItem
        if (hand == InteractionHand.OFF_HAND || !offhand.`is`(ModItemTags.DETACH_RIFT_INTERACTING)) return InteractionResult.FAIL

        return UseItemOnBlockCallback.EVENT.invoker().useItemOnBlock(player, world, InteractionHand.OFF_HAND, hitResult).takeUnless { it == InteractionResult.PASS }
            ?: offhand.useOn(UseOnContext(player, InteractionHand.OFF_HAND, hitResult)).takeUnless { it == InteractionResult.PASS }
            ?: InteractionResult.FAIL
    }
}

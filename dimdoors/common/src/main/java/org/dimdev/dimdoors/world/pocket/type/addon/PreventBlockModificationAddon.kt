package org.dimdev.dimdoors.world.pocket.type.addon

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.api.event.UseItemOnBlockCallback
import org.dimdev.dimdoors.world.pocket.type.Pocket

object PreventBlockModificationAddon : PocketAddon, SingletonInstance<PreventBlockModificationAddon>(), UseItemOnBlockCallback {
    fun attackBlock(player: Player, hand: InteractionHand?, pos: BlockPos?, face: Direction?): InteractionResult {
        return if (preventsBlockModification(player)) InteractionResult.FAIL else InteractionResult.PASS
    }

    fun preventsBlockModification(player: Player): Boolean = !player.isCreative

    fun useItem(player: Player?, hand: InteractionHand?) = InteractionResult.PASS

    fun useBlock(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        if (player.isCreative) return InteractionResult.PASS
        if (player.getItemInHand(hand).item is BlockItem) {
            val blockPos = hitResult.blockPos
            val blockState = world.getBlockState(blockPos)
            val result = blockState.useWithoutItem(world, player, hitResult)
            if (result.consumesAction()) return result

            return InteractionResult.FAIL
        }
        return InteractionResult.PASS
    }

    override fun useItemOnBlock(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult = useBlock(player, world, hand, hitResult)

    override val type get() = PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON

    object PreventBlockModificationBuilderAddon : SingletonInstance<PreventBlockModificationBuilderAddon>(), PocketAddon.PocketBuilderAddon<PreventBlockModificationAddon, PreventBlockModificationBuilderAddon> {
        override fun apply(pocket: Pocket<*, *>): PreventBlockModificationAddon {
            pocket.addAddon(PreventBlockModificationAddon)

            return PreventBlockModificationAddon
        }

        override val type get() = PocketAddons.PREVENT_BLOCK_MODIFICATION_ADDON
    }
}

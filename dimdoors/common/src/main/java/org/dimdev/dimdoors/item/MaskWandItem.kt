package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.dimdev.dimdoors.api.item.AttackBlockResult
import org.dimdev.dimdoors.api.item.ExtendedItem
import org.dimdev.dimdoors.entity.ModEntityTypes
import org.dimdev.dimdoors.entity.mask.MaskEntity
import org.dimdev.dimdoors.entity.mask.MaskMode
import org.dimdev.dimdoors.entity.mask.MaskType

class MaskWandItem(settings: Properties) : Item(settings), ExtendedItem {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (!player.isCreative || !player.isShiftKeyDown) return InteractionResultHolder.pass(stack)

        stack.remove(ModDataComponentTypes.MASK_WAND_WAYPOINTS)
        player.displayClientMessage(Component.translatable("item.dimdoors.mask_wand.cleared"), true)
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        if (!player.isCreative) return InteractionResult.PASS
        if (player.isShiftKeyDown) return use(context.level, player, context.hand).result

        val stack = context.itemInHand
        val waypoints = stack.getOrDefault(ModDataComponentTypes.MASK_WAND_WAYPOINTS, emptyList())

        if (waypoints.size >= MaskEntity.MAX_WAYPOINTS) {
            player.displayClientMessage(Component.translatable("item.dimdoors.mask_wand.full", MaskEntity.MAX_WAYPOINTS), true)
            return InteractionResult.FAIL
        }

        stack.set(ModDataComponentTypes.MASK_WAND_WAYPOINTS, waypoints + context.clickedPos.relative(context.clickedFace))
        player.displayClientMessage(Component.translatable("item.dimdoors.mask_wand.added", waypoints.size + 1), true)
        return InteractionResult.sidedSuccess(context.level.isClientSide)
    }

    override fun onAttackBlock(world: Level, player: Player, hand: InteractionHand, pos: BlockPos, direction: Direction): AttackBlockResult {
        if (!player.isCreative) return AttackBlockResult.fail(false)
        if (world !is ServerLevel) return AttackBlockResult.success(true)

        val stack = player.getItemInHand(hand)
        val waypoints = stack.getOrDefault(ModDataComponentTypes.MASK_WAND_WAYPOINTS, emptyList())

        val homePos = pos.relative(direction)
        if (!world.getBlockState(homePos).canBeReplaced()) return AttackBlockResult.fail(false)

        val mask = MaskEntity(ModEntityTypes.MASK, world)
        mask.maskType = stack.getOrDefault(ModDataComponentTypes.MASK_TYPE, MaskType.CYCLOP)
        mask.bind(homePos, waypoints)
        mask.moveTo(Vec3.atBottomCenterOf(homePos))
        world.addFreshEntity(mask)

        mask.home = homePos

        return AttackBlockResult.success(false)
    }

    private fun MaskEntity.bind(homePos: BlockPos, absoluteWaypoints: List<BlockPos>) {
        home = homePos
        waypoints = absoluteWaypoints.map { it.subtract(homePos) }
        waypointIndex = 0
        waypointReverse = false
        mode = if (waypoints.isEmpty()) MaskMode.GUARD else MaskMode.PATROL
    }
}

package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.DimensionalPortalBlock
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.Rift
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import kotlin.math.atan2

class RiftBladeItem(settings: Properties) : SwordItem(Tiers.IRON, settings), DetachRiftInteracting {
    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) = ToolTipHelper.processTranslation(list, "${this.descriptionId}.info")

    override fun isFoil(itemStack: ItemStack) = true

    override fun isValidRepairItem(item: ItemStack, repairingItem: ItemStack) = ModItems.STABLE_FABRIC == repairingItem.item

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val rift = level.getBlockEntity(pos) as? DetachedRiftBlockEntity ?: return InteractionResult.PASS
        if (level.isClientSide) return InteractionResult.SUCCESS
        val serverPlayer = context.player as? ServerPlayer ?: return InteractionResult.PASS

        level.setBlockAndUpdate(
            pos,
            ModBlocks.DIMENSIONAL_PORTAL.defaultBlockState().setValue(
                DimensionalPortalBlock.FACING,
                context.clickedFace.opposite
            )
        )
        level.getBlockEntity(pos)?.castOrNull<Rift<*>>()?.copyFrom(rift)
        context.itemInHand.hurtAndBreak(1, serverPlayer.serverLevel(), serverPlayer) {}
        return InteractionResult.SUCCESS
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (player is ServerPlayer) {
            //        double damageMultiplier = (double) stack.getDamageValue() / (double) stack.getMaxDamage(); //TODO: Decide if to remove old code or still use.
    //        // TODO: gaussian, instead or random
    //        double offsetDistance = Math.random() * damageMultiplier * 7 + 2; //TODO: make these offset distances configurable
    //        double offsetRotationYaw = (Math.random() - 0.5) * damageMultiplier * 360;
    //
    //        var playerVec = player.position();
    //        var entityVec = hit.getLocation();
    //        var offsetDirection = playerVec.subtract(entityVec).normalize();
    //        offsetDirection = offsetDirection.yRot((float) (offsetRotationYaw * Math.PI) / 180);
    //
    //        Vec3 added = entityVec.add(offsetDirection.scale(offsetDistance));
    //        BlockPos teleportPosition = new BlockPos(new Vec3i((int) added.x, (int) added. y, (int) added.z));
    //        while (world.getBlockState(teleportPosition).blocksMotion())
    //        teleportPosition = teleportPosition.above();
    //        player.teleportTo(teleportPosition.getX(), teleportPosition.getY(), teleportPosition.getZ());
    //        player.setYRot((float) (Math.random() * 2 * Math.PI));
    //
    //        stack.hurtAndBreak(1, player, a -> a.broadcastBreakEvent(hand));


            // Determine target position directly from the hit location

            val world = interactionTarget.level();

            val targetVec = interactionTarget.position()

            var teleportPosition = BlockPos(targetVec.x().toInt(), targetVec.y().toInt(), targetVec.z().toInt())

            // Ensure the target position is not inside a block
            while (world.getBlockState(teleportPosition).blocksMotion()) teleportPosition = teleportPosition.above()

            world.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.CHORUS_FRUIT_TELEPORT,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )

            // Teleport the player to the target position
            player.teleportTo(
                teleportPosition.x + 0.5,
                teleportPosition.y.toDouble(),
                teleportPosition.z + 0.5
            )

            // Calculate and set the yaw rotation to face the target entity
            val direction = targetVec.subtract(player.position()).normalize()
            val yaw = (atan2(direction.z, direction.x) * (180 / Math.PI)).toFloat() - 90
            player.yRot = yaw


            stack.hurtAndBreak(1, player.serverLevel(), player) {}

            return InteractionResult.SUCCESS
        } else return InteractionResult.SUCCESS
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("${this.descriptionId}.rift_miss"), true)
            triggerRiftCoreHighlight()
        }

        return InteractionResultHolder<ItemStack>(InteractionResult.FAIL, player.getItemInHand(hand))
    }

    companion object {
        const val ID: String = "rift_blade"
    }
}

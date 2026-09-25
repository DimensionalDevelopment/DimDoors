package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import org.dimdev.dimdoors.api.util.TeleportUtil
import org.dimdev.dimdoors.api.util.math.MathUtil.entityEulerAngle
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.RaycastHelper.raycast
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.world.ModDimensions
import org.dimdev.dimdoors.world.pocket.VirtualLocation

class DimensionalEraserItem(settings: Properties?) : DimDoorsItem(settings) {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        val hit = raycast(player, 1.0f) { a: Entity? -> a !is Player }

        if (!world.isClientSide() && hit != null && hit.type == HitResult.Type.ENTITY) {
            val target = (hit as EntityHitResult).entity
            if (target is ServerPlayer) {
                var teleportPos = target.blockPosition()
                while (ModDimensions.LIMBO_DIMENSION.getBlockState(
                        VirtualLocation.getTopPos(
                            ModDimensions.LIMBO_DIMENSION,
                            teleportPos.x,
                            teleportPos.z
                        )
                    ).block === ModBlocks.ETERNAL_FLUID
                ) {
                    teleportPos = teleportPos.offset(1, 0, 1)
                }
                TeleportUtil.teleport(
                    target,
                    ModDimensions.LIMBO_DIMENSION,
                    teleportPos.atY(255),
                    entityEulerAngle(target),
                    target.deltaMovement
                )
            } else {
                target.remove(Entity.RemovalReason.KILLED)
                player.level().playSound(
                    null,
                    player.x,
                    player.y,
                    player.z,
                    ModSoundEvents.BLOOP,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f
                )
            }
            return InteractionResultHolder<ItemStack?>(InteractionResult.SUCCESS, stack)
        }

        return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        list: MutableList<Component?>,
        tooltipFlag: TooltipFlag
    ) {
        list.add(Component.translatable("${this.descriptionId}.info"))
    }
}
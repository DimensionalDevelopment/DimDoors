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
import org.dimdev.dimdoors.item.RaycastHelper.raycast
import org.dimdev.dimdoors.rift.targets.LimboTarget
import org.dimdev.dimdoors.sound.ModSoundEvents

class DimensionalEraserItem(settings: Properties) : DimDoorsItem(settings) {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)

        val hit = raycast(player, 1.0f) { a: Entity? -> a !is Player }

        if (!world.isClientSide() && hit != null && hit.type == HitResult.Type.ENTITY) {
            val target = (hit as EntityHitResult).entity
            if (target is ServerPlayer) {
                LimboTarget.teleportToLimbo(target)
            } else {
                target.remove(Entity.RemovalReason.KILLED)
                player.level().playSound(null, player.x, player.y, player.z, ModSoundEvents.BLOOP, SoundSource.BLOCKS, 1.0f, 1.0f)
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
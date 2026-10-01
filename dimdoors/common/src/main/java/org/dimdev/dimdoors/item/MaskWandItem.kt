package org.dimdev.dimdoors.item

import net.minecraft.client.resources.language.I18n.exists
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class MaskWandItem(settings: Properties) : Item(settings) {
    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        val hit = player.pick(RaycastHelper.REACH_DISTANCE.toDouble(), 0f, false)

        if (world.isClientSide()) {
            return InteractionResultHolder.fail<ItemStack?>(stack)
        } else {
            if (hit.getType() == HitResult.Type.BLOCK) {
//        MaskEntity mask = ModEntityTypes.MASK.create((ServerWorld) world, null, LiteralText.EMPTY, player, ((BlockHitResult) hit).getBlockPos(), SpawnReason.SPAWNER, true, false);
//        world.spawnEntity(mask);
            }
        }

        return InteractionResultHolder.success<ItemStack?>(stack)
    }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (exists(this.descriptionId + ".info")) {
            list.add(Component.translatable("${this.descriptionId}.info"))
        }
    }

    companion object {
        private val LOGGER: Logger? = LogManager.getLogger()

        const val ID: String = "rift_configuration_tool"
    }
}

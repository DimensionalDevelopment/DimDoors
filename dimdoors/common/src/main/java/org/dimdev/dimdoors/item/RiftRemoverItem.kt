package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.world.ModLootTables

class RiftRemoverItem(settings: Properties) : Item(settings), DetachRiftInteracting {
    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        ToolTipHelper.processTranslation(list, "${this.descriptionId}.info")
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val rift = level.getBlockEntity(pos) as? DetachedRiftBlockEntity ?: return InteractionResult.PASS
        if (level.isClientSide) return InteractionResult.SUCCESS
        val serverPlayer = context.player as? ServerPlayer ?: return InteractionResult.PASS

        if (rift.weight >= 0) {
            rift.setClosing()
            level.playSound(null, serverPlayer.blockPosition(), ModSoundEvents.RIFT_CLOSE, SoundSource.BLOCKS, 0.6f, 1f)

            context.itemInHand.hurtAndBreak(10, serverPlayer.serverLevel(), serverPlayer) {}
            val ctx = LootParams.Builder(serverPlayer.serverLevel())
                .withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(pos))
                .withParameter(LootContextParams.THIS_ENTITY, serverPlayer)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.BLOCK_USE)

            serverPlayer.server.reloadableRegistries().getLootTable(ModLootTables.REMOVED_RIFT).getRandomItems(ctx)
                .forEach { stack ->
                    Containers.dropItemStack(
                        level,
                        pos.x.toDouble(),
                        pos.y.toDouble(),
                        pos.z.toDouble(),
                        stack
                    )
                }

            serverPlayer.displayClientMessage(Component.translatable(this.descriptionId + ".closing"), true)
            return InteractionResult.SUCCESS
        } else {
            serverPlayer.displayClientMessage(Component.translatable(this.descriptionId + ".already_closing"), true)
        }
        return InteractionResult.FAIL
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (world.isClientSide) {
            player.displayClientMessage(Component.translatable("tools.rift_miss"), true)
            triggerRiftCoreHighlight()
        }

        return InteractionResultHolder<ItemStack>(InteractionResult.FAIL, player.getItemInHand(hand))
    }

    companion object {
        const val ID: String = "rift_remover"
    }
}

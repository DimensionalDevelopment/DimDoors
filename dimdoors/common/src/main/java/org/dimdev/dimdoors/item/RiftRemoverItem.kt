package org.dimdev.dimdoors.item

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.item.RaycastHelper.findDetachRift
import org.dimdev.dimdoors.item.RaycastHelper.hitsDetachedRift
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import org.dimdev.dimdoors.sound.ModSoundEvents
import org.dimdev.dimdoors.world.ModLootTables
import java.util.*
import java.util.function.Consumer

class RiftRemoverItem(settings: Properties) : Item(settings) {
    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        ToolTipHelper.processTranslation(list, "${this.description}.info")
    }

    override fun use(world: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(hand)
        val hit: HitResult = findDetachRift(player, RaycastHelper.DETACH)

        if (world.isClientSide) {
            if (!hitsDetachedRift(hit, world)) {
                player.displayClientMessage(Component.translatable("tools.rift_miss"), true)
                triggerRiftCoreHighlight()
            }
            return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, stack)
        }

        val slot = if (hand == InteractionHand.MAIN_HAND) EquipmentSlot.MAINHAND else EquipmentSlot.OFFHAND

        if (hitsDetachedRift(hit, world)) {
            // casting to BlockHitResult is mostly safe since RaycastHelper#hitsDetachedRift already checks hit type
            val rift = world.getBlockEntity((hit as BlockHitResult).getBlockPos()) as DetachedRiftBlockEntity?
            if (Objects.requireNonNull<DetachedRiftBlockEntity?>(rift).getWeight() >= 0) {
                rift!!.setClosing()
                world.playSound(null, player.blockPosition(), ModSoundEvents.RIFT_CLOSE, SoundSource.BLOCKS, 0.6f, 1f)

                val serverPlayer = player as ServerPlayer

                stack.hurtAndBreak(10, serverPlayer.serverLevel(), serverPlayer, Consumer { a: Item? -> })
                val pos = hit.getBlockPos()
                val ctx = LootParams.Builder(world as ServerLevel)
                    .withParameter<BlockState?>(LootContextParams.BLOCK_STATE, world.getBlockState(pos))
                    .withParameter<Entity?>(LootContextParams.THIS_ENTITY, player)
                    .withParameter<Vec3?>(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .create(LootContextParamSets.BLOCK_USE)

                world.getServer().reloadableRegistries().getLootTable(ModLootTables.REMOVED_RIFT).getRandomItems(ctx)
                    .forEach(
                        Consumer { stack1: ItemStack? ->
                            Containers.dropItemStack(
                                world,
                                hit.getBlockPos().getX().toDouble(),
                                hit.getBlockPos().getY().toDouble(),
                                hit.getBlockPos().getZ().toDouble(),
                                stack1
                            )
                        })

                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".closing"), true)
                return InteractionResultHolder<ItemStack?>(InteractionResult.SUCCESS, stack)
            } else {
                player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".already_closing"), true)
            }
        }
        return InteractionResultHolder<ItemStack?>(InteractionResult.FAIL, stack)
    }

    companion object {
        const val ID: String = "rift_remover"
    }
}

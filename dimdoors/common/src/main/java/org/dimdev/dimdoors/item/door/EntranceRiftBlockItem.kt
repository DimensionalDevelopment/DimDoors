package org.dimdev.dimdoors.item.door

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import org.dimdev.dimcore.api.castOrNull
import org.dimdev.dimcore.api.client.ToolTipHelper
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.RiftProvider
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.item.RaycastHelper
import org.dimdev.dimdoors.item.RaycastHelper.DETACH
import org.dimdev.dimdoors.listener.UseDoorItemOnBlockCallbackListener.DimDoorBlockPlaceContext
import org.dimdev.dimdoors.rift.RiftUtils.triggerRiftCoreHighlight
import kotlin.math.sqrt

open class EntranceRiftBlockItem(
    block: Block,
    settings: Properties,
    private val setupFunction: (EntranceRiftBlockEntity<*>) -> Unit = {},
    private var hasToolTip: Boolean = true
) : BlockItem(block, settings) {

    constructor(block: Block, settings: Properties, setupFunction: (EntranceRiftBlockEntity<*>) -> Unit) : this(
        block,
        settings,
        setupFunction,
        false
    )

    override fun appendHoverText(
        itemStack: ItemStack,
        world: TooltipContext,
        list: MutableList<Component>,
        tooltipContext: TooltipFlag
    ) {
        if (hasToolTip) {
            ToolTipHelper.processTranslation(list, this.descriptionId + ".info")
        }
    }

    override fun place(ctx: BlockPlaceContext): InteractionResult {
        val context = DimDoorBlockPlaceContext(ctx, RaycastHelper.findDetachRift(ctx.player!!, DETACH))

        var pos = context.clickedPos

        val placedOnRift = context.level.getBlockState(pos).block === ModBlocks.DETACHED_RIFT

        if (!placedOnRift) {
            context.setToProperReplaced()
            pos = context.clickedPos
        }

        if (!placedOnRift && !context.player!!.isShiftKeyDown && isRiftNear(context.level, pos)) {
            // Allowing on second right click would require cancelling client-side, which
            // is impossible (see https://github.com/MinecraftForge/MinecraftForge/issues/3272)
            // without sending custom packets.

            if (context.level.isClientSide) {
                context.player!!
                    .displayClientMessage(Component.translatable("rifts.entrances.rift_too_close"), true)
                triggerRiftCoreHighlight()
            }

            return InteractionResult.FAIL
        }

        if (context.level.isClientSide) return super.place(context)

        // Store the rift entity if there's a rift block there that may be broken
        var rift: DetachedRiftBlockEntity? = null
        if (placedOnRift) {
            rift = context.level.getBlockEntity(pos) as DetachedRiftBlockEntity?
            rift!!.isDeleteRift = false
            context.level.removeBlock(pos, false)
        }


        val result = super.place(context)
        when {
            result == InteractionResult.SUCCESS || result == InteractionResult.CONSUME -> {
                val state = context.level.getBlockState(pos)
                if (rift == null) {
                    // Get the rift entity (not hard coded, works with any door size)
                    state.block.castOrNull<RiftProvider<*>>()?.getRift(
                        context.level,
                        pos,
                        state
                    )?.castOrNull<EntranceRiftBlockEntity<*>>()?.let {
                        // Configure the rift to its default functionality
                        this.setupRift(it)

                        // Register the rift in the registry
                        it.setChanged()
                        it.register()
                    }
                } else {
                    // Copy from the old rift
                    context.level.getBlockEntity(pos)?.castOrNull<EntranceRiftBlockEntity<*>>()?.also {
                        it.copyFrom(rift)
                        it.updateType()
                    }

                }
            }

            rift != null -> {
                rift.isDeleteRift = false
            }
        }

        return result
    }

    open fun setupRift(entranceRift: EntranceRiftBlockEntity<*>) = setupFunction(entranceRift)

    companion object {
        fun isRiftNear(world: Level, pos: BlockPos): Boolean {
            for (x in pos.x - 5..<pos.x + 5) {
                for (y in pos.y - 5..<pos.y + 5) {
                    for (z in pos.z - 5..<pos.z + 5) {
                        val searchPos = BlockPos(x, y, z)
                        if (world.getBlockState(searchPos).block === ModBlocks.DETACHED_RIFT) {
                            val rift = world.getBlockEntity(searchPos)?.castOrNull<DetachedRiftBlockEntity>() ?: continue

                            if (sqrt(pos.distSqr(searchPos)) < rift.data.size) return true
                        }
                    }
                }
            }

            return false
        }
    }
}

package org.dimdev.dimdoors.item

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.blockentity.BlockEntityRift
import org.dimdev.dimdoors.rift.item.Holder
import org.dimdev.dimdoors.rift.item.ItemRiftRegistry
import org.dimdev.dimdoors.rift.registry.RiftGraph
import org.dimdev.dimdoors.rift.targets.RiftReference
import org.dimdev.dimdoors.rift.targets.VirtualTarget

class PortableRiftItem(properties: Properties) : Item(properties), ItemRift {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level as? ServerLevel ?: return InteractionResult.SUCCESS
        val player = context.player ?: return InteractionResult.PASS
        val stack = context.itemInHand

        return if (stack.has(ModDataComponentTypes.TRACKED_ITEM)) place(level, stack, context.clickedPos.relative(context.clickedFace))
        else pluck(level, player, stack, context.clickedPos)
    }

    private fun pluck(level: ServerLevel, player: Player, stack: ItemStack, pos: BlockPos): InteractionResult {
        val rift = level.getBlockEntity(pos)?.castOrNull<BlockEntityRift<*>>() ?: return InteractionResult.PASS
        val holder = Holder.entity(player)
        val old = rift.vertex()

        ItemRiftRegistry.attach(stack, holder)
        val carried = vertex(holder)

        copyFrom(holder, rift, rift.location())
        setProperties(holder, rift.properties())

        RiftGraph.getInstance().run {
            sources(old.id).forEach { source ->
                vertex(source)?.getTarget(source)?.castOrNull<BlockEntityRift<*>>()?.setDestination(RiftReference(carried))
            }
        }
        data(holder).destination.takeIf { it !== VirtualTarget.NoneTarget }?.register(carried)

        rift.unregister()
        return InteractionResult.CONSUME
    }

    private fun place(level: ServerLevel, stack: ItemStack, pos: BlockPos): InteractionResult {
        if (!level.getBlockState(pos).canBeReplaced()) return InteractionResult.FAIL
        val holder = ItemRiftRegistry.rift(stack) ?: return InteractionResult.FAIL
        val carried = vertex(holder)

        level.setBlockAndUpdate(pos, ModBlocks.DETACHED_RIFT.defaultBlockState())
        val placed = level.getBlockEntity(pos)?.castOrNull<BlockEntityRift<*>>() ?: return InteractionResult.FAIL

        data(holder).destination.unregister(carried)
        placed.copyFrom(placed.location(), this, holder)
        placed.register()
        placed.setProperties(properties(holder))

        RiftGraph.getInstance().run {
            val dest = RiftReference(placed.vertex())
            sources(carried.id).forEach { source ->
                vertex(source)?.getTarget(source)?.castOrNull<BlockEntityRift<*>>()?.setDestination(dest)
            }
        }

        ItemRiftRegistry.detach(stack)
        stack.shrink(1)
        return InteractionResult.CONSUME
    }

    override val name: String
        get() = "Portable Rift"
}
package org.dimdev.dimdoors.world.decay

import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import kotlin.math.min

object DecayInventoryHelper {
    @JvmStatic
    fun takeContents(level: Level, pos: BlockPos): List<ItemStack> {
        return takeContents(level.getBlockEntity(pos))
    }

    @JvmStatic
    fun takeContents(blockEntity: BlockEntity?): List<ItemStack> {
        val container = blockEntity as? Container ?: return listOf()

        val contents = mutableListOf<ItemStack>()

        for (slot in 0 until container.containerSize) {
            val stack = container.removeItemNoUpdate(slot)

            if (!stack.isEmpty) {
                contents.add(stack)
            }
        }

        container.setChanged()
        return contents
    }

    @JvmStatic
    fun transferOrDrop(level: Level, pos: BlockPos, contents: List<ItemStack>) {
        if (contents.isEmpty()) {
            return
        }

        val container = level.getBlockEntity(pos) as? Container
        val leftovers = mutableListOf<ItemStack>()

        for (stack in contents) {
            var remainder = stack.copy()

            if (container != null) {
                remainder = insert(container, remainder)
            }

            if (!remainder.isEmpty) {
                leftovers.add(remainder)
            }
        }

        container?.setChanged()

        drop(level, pos, leftovers)
    }

    @JvmStatic
    fun drop(level: Level, pos: BlockPos, contents: List<ItemStack>) {
        for (stack in contents) {
            if (!stack.isEmpty) {
                Containers.dropItemStack(level, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack)
            }
        }
    }

    private fun insert(container: Container, stack: ItemStack): ItemStack {
        for (slot in 0 until container.containerSize) {
            if (stack.isEmpty) break

            if (!container.canPlaceItem(slot, stack)) {
                continue
            }

            val existing = container.getItem(slot)

            if (existing.isEmpty) {
                val moved = min(stack.count, min(container.maxStackSize, stack.maxStackSize))
                val placed = stack.copy()
                placed.count = moved
                container.setItem(slot, placed)
                stack.shrink(moved)
                continue
            }

            if (!ItemStack.isSameItemSameComponents(existing, stack)) {
                continue
            }

            val max = min(container.maxStackSize, existing.maxStackSize)
            val space = max - existing.count

            if (space <= 0) {
                continue
            }

            val moved = min(space, stack.count)
            existing.grow(moved)
            container.setItem(slot, existing)
            stack.shrink(moved)
        }

        return stack
    }
}

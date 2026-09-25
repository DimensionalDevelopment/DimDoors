package org.dimdev.dimcore.api.entity

import com.mojang.datafixers.types.Type
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.dimdev.dimcore.mixin.accessor.BlockEntityTypeAccessor
import java.util.*

class MutableBlockEntityType<T : BlockEntity>(
    factory: BlockEntityFactory<out T>,
    blocks: MutableSet<Block>,
    type: Type<*>
) : BlockEntityType<T>(factory, HashSet(blocks), type) {
    fun addBlock(block: Block): Boolean = getBlocks(this).add(block)

    operator fun plus(block: Block): MutableBlockEntityType<T> {
        addBlock(block)
        return this
    }

    operator fun minus(block: Block): MutableBlockEntityType<T> {
        removeBlock(block)
        return this
    }

    fun removeBlock(block: Block): Boolean = getBlocks(this).remove(block)

    class Builder<T : BlockEntity> private constructor(
        private val factory: BlockEntityFactory<out T>,
        private val blocks: MutableSet<Block>
    ) {
        fun build(type: Type<*>): MutableBlockEntityType<T> = MutableBlockEntityType(this.factory, this.blocks, type)

        companion object {
            fun <T : BlockEntity> create(factory: BlockEntityFactory<out T>, vararg blocks: Block) = Builder(factory, HashSet<Block>(mutableListOf(*blocks)))
        }
    }

    @FunctionalInterface
    interface BlockEntityFactory<T : BlockEntity> : BlockEntitySupplier<T>
    companion object {
        fun getBlocks(type: BlockEntityType<*>): MutableSet<Block> {
            return (type as BlockEntityTypeAccessor).getBlocks()
        }
    }
}

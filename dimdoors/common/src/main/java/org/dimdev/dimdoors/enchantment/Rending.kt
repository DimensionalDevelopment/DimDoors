package org.dimdev.dimdoors.enchantment

import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TieredItem
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.world.decay.Decay.DecayContext.Companion.create
import org.dimdev.dimdoors.world.decay.Decay.DecayLoader.getPatterns
import org.dimdev.dimdoors.world.decay.DecayPatternHolder
import org.dimdev.dimdoors.world.decay.DecaySource
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern
import org.dimdev.dimdoors.world.decay.results.DecayResult

object Rending {
    @JvmStatic
    fun replaceBlockDrops(
        level: ServerLevel,
        pos: BlockPos,
        tool: ItemStack,
        drops: MutableList<ItemStack>
    ): MutableList<ItemStack> {
        val enchantmentLevel = getLevel(tool)
        if (enchantmentLevel <= 0 || drops.isEmpty() || level.random.nextFloat() >= dropChance(enchantmentLevel)) {
            return drops
        }

        for (i in drops.indices) {
            val drop = drops[i]

            val blockItem = drop.item.castOrNull<BlockItem>() ?: continue

            val replacement = getRendingDrop(level, pos, blockItem.block) ?: continue

            drops[i] = drop.transmuteCopy(replacement, drop.count)
        }

        return drops
    }

    @JvmStatic
    fun raisesMiningTier(stack: ItemStack, state: BlockState): Boolean {
        if (getLevel(stack) < 2) return false

        val tieredItem = stack.castOrNull<TieredItem>() ?: return false

        val tool = stack.get(DataComponents.TOOL) ?: return false
        if (tool.getMiningSpeed(state) <= tool.defaultMiningSpeed()) return false

        val nextIncorrectTag = nextIncorrectTag(tieredItem.tier.incorrectBlocksForDrops)
        return nextIncorrectTag != null && !state.`is`(nextIncorrectTag)
    }

    fun getLevel(stack: ItemStack) = stack.enchantments.entrySet().filter { a -> a.key.`is`(ModEnchants.RENDING_ENCHANTMENT) }.maxOfOrNull { it.intValue } ?: 0

    private fun dropChance(level: Int): Float {
        return if (level >= 2) 0.3f else 0.1f
    }

    private fun getRendingDrop(level: ServerLevel, pos: BlockPos, block: Block): Item? {
        val state = block.defaultBlockState()

        val context = create(level, pos, state, DecaySource.CUSTOM)

        return getPatterns(context)
            .asSequence()
            .map(DecayPatternHolder::value)
            .filterIsInstance<CompoundDecayPattern>()
            .filter { data -> data.test(context) }
            .map(CompoundDecayPattern::result).
            flatMap(DecayResult::produces)
            .map(DecayResult.Result::obj)
            .filterIsInstance<ItemLike>()
            .firstNotNullOfOrNull { it.asItem() }
    }

    private fun nextIncorrectTag(tag: TagKey<Block?>): TagKey<Block?>? {
        if (tag == BlockTags.INCORRECT_FOR_WOODEN_TOOL || tag == BlockTags.INCORRECT_FOR_GOLD_TOOL) return BlockTags.INCORRECT_FOR_STONE_TOOL
        if (tag == BlockTags.INCORRECT_FOR_STONE_TOOL) return BlockTags.INCORRECT_FOR_IRON_TOOL
        if (tag == BlockTags.INCORRECT_FOR_IRON_TOOL) return BlockTags.INCORRECT_FOR_DIAMOND_TOOL
        if (tag == BlockTags.INCORRECT_FOR_DIAMOND_TOOL) return BlockTags.INCORRECT_FOR_NETHERITE_TOOL
        return null
    }
}

package org.dimdev.dimdoors.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.world.decay.Decay;
import org.dimdev.dimdoors.world.decay.DecaySource;
import org.dimdev.dimdoors.world.decay.pattern.CompoundDecayPattern;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class Rending {
    private Rending() {
    }

    public static List<ItemStack> replaceBlockDrops(ServerLevel level, BlockPos pos, ItemStack tool, List<ItemStack> drops) {
        int enchantmentLevel = getLevel(tool);
        if (enchantmentLevel <= 0 || drops.isEmpty() || level.random.nextFloat() >= dropChance(enchantmentLevel)) {
            return drops;
        }

        List<ItemStack> replaced = null;
        for (int i = 0; i < drops.size(); i++) {
            ItemStack drop = drops.get(i);
            if (!(drop.getItem() instanceof BlockItem blockItem)) {
                continue;
            }

            Item replacement = getRendingDrop(level, pos, blockItem.getBlock());
            if (replacement == null) {
                continue;
            }

            if (replaced == null) {
                replaced = new ArrayList<>(drops);
            }
            replaced.set(i, drop.transmuteCopy(replacement, drop.getCount()));
        }

        return replaced == null ? drops : replaced;
    }

    public static boolean raisesMiningTier(ItemStack stack, BlockState state) {
        if (getLevel(stack) < 2 || !(stack.getItem() instanceof TieredItem tieredItem)) {
            return false;
        }

        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null || tool.getMiningSpeed(state) <= tool.defaultMiningSpeed()) {
            return false;
        }

        TagKey<Block> nextIncorrectTag = nextIncorrectTag(tieredItem.getTier().getIncorrectBlocksForDrops());
        return nextIncorrectTag != null && !state.is(nextIncorrectTag);
    }

    public static int getLevel(ItemStack stack) {
        int level = 0;
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().unwrapKey().filter(ModEnchants.RENDING_ENCHANTMENT::equals).isPresent()) {
                level = Math.max(level, entry.getIntValue());
            }
        }
        return level;
    }

    private static float dropChance(int level) {
        return level >= 2 ? 0.3F : 0.1F;
    }

    @Nullable
    private static Item getRendingDrop(ServerLevel level, BlockPos pos, Block block) {
        BlockState state = block.defaultBlockState();
        var context = new Decay.DecayContext(level, pos, state, pos, state, state.getFluidState(), null, DecaySource.CUSTOM);
        for (var holder : Decay.DecayLoader.getPatterns(context)) {
            if (holder.value() instanceof CompoundDecayPattern pattern && pattern.test(context)) {
                for (var result : pattern.result().produces()) {
                    if (result.obj() instanceof Block blockObj) {
                        return blockObj.asItem();
                    }
                }
            }

        }
        return null;
    }

    @Nullable
    private static TagKey<Block> nextIncorrectTag(TagKey<Block> tag) {
        if (tag.equals(BlockTags.INCORRECT_FOR_WOODEN_TOOL) || tag.equals(BlockTags.INCORRECT_FOR_GOLD_TOOL)) {
            return BlockTags.INCORRECT_FOR_STONE_TOOL;
        }
        if (tag.equals(BlockTags.INCORRECT_FOR_STONE_TOOL)) {
            return BlockTags.INCORRECT_FOR_IRON_TOOL;
        }
        if (tag.equals(BlockTags.INCORRECT_FOR_IRON_TOOL)) {
            return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
        }
        if (tag.equals(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)) {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }
        return null;
    }
}

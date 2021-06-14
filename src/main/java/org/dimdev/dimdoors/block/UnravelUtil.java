package org.dimdev.dimdoors.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.dimdev.dimdoors.item.ModItems;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UnravelUtil {
	public static final Set<Block> whitelistedBlocksForLimboRemoval = new HashSet<>();
	public static final Map<Item, Item> unravelItemsMap = new HashMap<>();
	public static final Map<Block, Block> unravelBlocksMap = new HashMap<>();
	static {
		whitelistedBlocksForLimboRemoval.add(Blocks.GRASS_BLOCK);
		whitelistedBlocksForLimboRemoval.add(Blocks.STONE);
		whitelistedBlocksForLimboRemoval.add(Blocks.SAND);
		whitelistedBlocksForLimboRemoval.add(Blocks.SANDSTONE);
		whitelistedBlocksForLimboRemoval.add(Blocks.ACACIA_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.AZALEA_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.BIRCH_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.DARK_OAK_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.JUNGLE_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.SPRUCE_LEAVES);
		whitelistedBlocksForLimboRemoval.add(Blocks.TERRACOTTA);
		whitelistedBlocksForLimboRemoval.add(Blocks.RED_SAND);

		//Add unravled items when we have them, for now, we do this.
		unravelItemsMap.put(Items.STONE, Items.COBBLESTONE);
		unravelItemsMap.put(Items.COBBLESTONE, Items.GRAVEL);
		unravelItemsMap.put(Items.GRAVEL, Items.SANDSTONE);
		unravelItemsMap.put(Items.SANDSTONE, Items.SAND);
		unravelItemsMap.put(Items.SAND, ModItems.UNRAVELLED_FABRIC);

		for(Item item : unravelItemsMap.keySet()) {
			Item item2 = unravelItemsMap.get(item);
			if(item instanceof BlockItem && item2 instanceof BlockItem) {
				unravelBlocksMap.put(((BlockItem)item).getBlock(), ((BlockItem)item2).getBlock());
			}
		}
		unravelBlocksMap.put(Blocks.WATER, ModBlocks.UNFOLDED_BLOCK);
	}
}

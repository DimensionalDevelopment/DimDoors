package org.dimdev.dimdoors.datagen;

import java.io.IOException;

import org.dimdev.dimdoors.block.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import static org.dimdev.dimdoors.datagen.DatagenInitializer.LOOT_TABLE_CONSUMER;

public class LootTableProvider implements DataProvider {
	private final DataGenerator dataGenerator;

	public LootTableProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}

	@Override
	public void run(DataCache cache) throws IOException {
		for (Block block : ModBlocks.FABRIC_BLOCKS.values()) {
			LOOT_TABLE_CONSUMER.registerBlockDropSelfRequiresSilkTouch(block);
		}
		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.GOLD_DOOR);
		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.QUARTZ_DOOR);
//		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.OAK_DIMENSIONAL_DOOR);
//		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.IRON_DIMENSIONAL_DOOR);
//		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.GOLD_DIMENSIONAL_DOOR);
//		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
		LOOT_TABLE_CONSUMER.registerBlockDropSelf(ModBlocks.MARKING_PLATE);
	}

	@Override
	public String getName() {
		return "Dimdoors Loot Tables";
	}
}

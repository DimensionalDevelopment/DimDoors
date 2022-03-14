package org.dimdev.dimdoors.datagen;

import java.io.IOException;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTablesProvider;
import org.dimdev.dimdoors.block.ModBlocks;

import net.minecraft.block.Block;

public class LootTableProvider extends FabricBlockLootTablesProvider {

	public LootTableProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateBlockLootTables() {
		for (Block block : ModBlocks.FABRIC_BLOCKS.values()) {
			this.addDropWithSilkTouch(block);
		}
		this.addDropWithSilkTouch(ModBlocks.GOLD_DOOR);
		this.addDropWithSilkTouch(ModBlocks.QUARTZ_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.OAK_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.IRON_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.GOLD_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
		this.addDropWithSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
		this.addDropWithSilkTouch(ModBlocks.MARKING_PLATE);
	}

	@Override
	public String getName() {
		return "Dimdoors Loot Tables";
	}
}

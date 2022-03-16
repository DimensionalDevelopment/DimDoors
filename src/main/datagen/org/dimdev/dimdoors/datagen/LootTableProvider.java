package org.dimdev.dimdoors.datagen;

import java.io.IOException;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTablesProvider;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import org.dimdev.dimdoors.block.ModBlocks;

import net.minecraft.block.Block;
import org.dimdev.dimdoors.item.ModItems;

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

		this.addDrop(ModBlocks.SOLID_STATIC, (blockx) -> oreDrops(blockx, ModItems.INFRANGIBLE_FIBER));

		this.addDrop(ModBlocks.UNRAVELLED_FABRIC, (blockx) -> dropsWithSilkTouch(blockx, addSurvivesExplosionCondition(blockx, ItemEntry.builder(ModItems.FRAYED_FILAMENT).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).alternatively(ItemEntry.builder(blockx)))));
	}

	@Override
	public String getName() {
		return "Dimdoors Loot Tables";
	}
}

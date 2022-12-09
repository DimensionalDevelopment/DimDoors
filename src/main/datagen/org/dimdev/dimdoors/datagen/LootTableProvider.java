package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.condition.TableBonusLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class LootTableProvider extends FabricBlockLootTableProvider {

	public LootTableProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generate() {
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

		this.addDrop(ModBlocks.UNRAVELLED_FABRIC, (blockx) -> dropsWithSilkTouch(blockx, addSurvivesExplosionCondition(blockx, ItemEntry.builder(ModItems.FRAYED_FILAMENTS).conditionally(TableBonusLootCondition.builder(Enchantments.FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).alternatively(ItemEntry.builder(blockx)))));
	}

	@Override
	public String getName() {
		return "Dimdoors Loot Tables";
	}
}

package org.dimdev.dimdoors.datagen;

import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class LootTableProvider extends FabricBlockLootTableProvider {

	public LootTableProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generate() {
		for (Block block : ModBlocks.FABRIC_BLOCKS.values()) {
			this.dropWhenSilkTouch(block);
		}
		this.dropWhenSilkTouch(ModBlocks.GOLD_DOOR);
		this.dropWhenSilkTouch(ModBlocks.QUARTZ_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.OAK_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.IRON_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.GOLD_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
		this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
		this.dropWhenSilkTouch(ModBlocks.MARKING_PLATE);

		this.add(ModBlocks.SOLID_STATIC, (blockx) -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER));

		this.add(ModBlocks.UNRAVELLED_FABRIC, (blockx) -> createSilkTouchDispatchTable(blockx,
				applyExplosionCondition(blockx, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS).when(
						BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(blockx)))));
	}

	@Override
	public String getName() {
		return "Dimdoors Loot Tables";
	}

	@Override
	public void accept(BiConsumer<ResourceLocation, LootTable.Builder> resourceLocationBuilderBiConsumer) {

	}
}

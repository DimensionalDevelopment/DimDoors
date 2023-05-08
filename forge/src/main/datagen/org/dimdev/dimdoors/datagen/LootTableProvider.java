package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.Collections;
import java.util.List;

public class LootTableProvider extends net.minecraft.data.loot.LootTableProvider {

	public LootTableProvider(PackOutput dataGenerator) {
		super(dataGenerator, BuiltInLootTables.all(), List.of(new SubProviderEntry(BlockProvider::new, LootContextParamSets.BLOCK)));
	}

	public static class BlockProvider extends BlockLootSubProvider {

		protected BlockProvider() {
			super(Collections.emptySet(), FeatureFlags.DEFAULT_FLAGS);
		}

		@Override
		public void generate() {
			for (RegistrySupplier<Block> block : ModBlocks.FABRIC_BLOCKS.values()) {
				this.dropWhenSilkTouch(block.get());
			}
			this.dropWhenSilkTouch(ModBlocks.GOLD_DOOR.get());
			this.dropWhenSilkTouch(ModBlocks.QUARTZ_DOOR.get());
//		this.addDropWithSilkTouch(ModBlocks.OAK_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.IRON_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.GOLD_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
			this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR.get());
			this.dropWhenSilkTouch(ModBlocks.MARKING_PLATE.get());

			this.add(ModBlocks.SOLID_STATIC.get(), (blockx) -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER.get()));

			this.add(ModBlocks.UNRAVELLED_FABRIC.get(), (blockx) -> createSilkTouchDispatchTable(blockx, applyExplosionCondition(blockx, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS.get()).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(blockx)))));
		}
	}

//	@Override
//	public String getName() {
//		return "Dimdoors Loot Tables";
//	}
}

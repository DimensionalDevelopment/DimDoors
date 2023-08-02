package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.function.BiConsumer;

public class LootTableProvider extends FabricBlockLootTableProvider {

	public LootTableProvider(FabricDataOutput dataGenerator) {
		super(dataGenerator);
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

			this.add(ModBlocks.UNRAVELLED_FABRIC.get(), (blockx) -> BlockLootSubProvider.createSilkTouchDispatchTable(blockx, applyExplosionCondition(blockx, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS.get()).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(blockx)))));

			this.dropSelf(ModBlocks.TESSELATING_LOOM.get());
		}
}

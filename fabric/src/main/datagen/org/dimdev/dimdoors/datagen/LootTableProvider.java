package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.forge.item.ModItems;

public class LootTableProvider extends FabricBlockLootTableProvider {

	public LootTableProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateBlockLootTables() {
			for (RegistrySupplier<Block> block : ModBlocks.FABRIC_BLOCKS.values()) {
				this.dropWhenSilkTouch(block.get());
			}
			this.dropSelf(ModBlocks.GOLD_DOOR.get());
			this.dropSelf(ModBlocks.QUARTZ_DOOR.get());
//		this.addDropWithSilkTouch(ModBlocks.OAK_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.IRON_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.GOLD_DIMENSIONAL_DOOR);
//		this.addDropWithSilkTouch(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
			this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR.get());
			this.dropWhenSilkTouch(ModBlocks.MARKING_PLATE.get());

			this.add(ModBlocks.SOLID_STATIC.get(), (blockx) -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER.get()));

			this.add(ModBlocks.UNRAVELLED_FABRIC.get(), (blockx) -> BlockLoot.createSilkTouchDispatchTable(blockx, applyExplosionCondition(blockx, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS.get()).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F)).otherwise(LootItem.lootTableItem(blockx)))));

			this.dropSelf(ModBlocks.TESSELATING_LOOM.get());

			this.dropSelf(ModBlocks.DRIFTWOOD_WOOD.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_LOG.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_PLANKS.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_LEAVES.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_SAPLING.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_FENCE.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_GATE.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_BUTTON.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_SLAB.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_STAIRS.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_DOOR.get());
			this.dropSelf(ModBlocks.DRIFTWOOD_TRAPDOOR.get());
			this.dropSelf(ModBlocks.AMALGAM_BLOCK.get());
			this.dropSelf(ModBlocks.AMALGAM_DOOR.get());
			this.dropSelf(ModBlocks.AMALGAM_TRAPDOOR.get());
			this.dropSelf(ModBlocks.RUST.get());
			this.dropSelf(ModBlocks.AMALGAM_SLAB.get());
			this.dropSelf(ModBlocks.AMALGAM_STAIRS.get());
			this.dropSelf(ModBlocks.AMALGAM_ORE.get());
			this.dropSelf(ModBlocks.CLOD_ORE.get());
			this.dropSelf(ModBlocks.CLOD_BLOCK.get());
			this.dropSelf(ModBlocks.GRAVEL_FENCE.get());
			this.dropSelf(ModBlocks.GRAVEL_BUTTON.get());
			this.dropSelf(ModBlocks.GRAVEL_SLAB.get());
			this.dropSelf(ModBlocks.GRAVEL_STAIRS.get());
			this.dropSelf(ModBlocks.GRAVEL_WALL.get());
			this.dropSelf(ModBlocks.DARK_SAND.get());
			this.dropSelf(ModBlocks.DARK_SAND_FENCE.get());
			this.dropSelf(ModBlocks.DARK_SAND_BUTTON.get());
			this.dropSelf(ModBlocks.DARK_SAND_SLAB.get());
			this.dropSelf(ModBlocks.DARK_SAND_STAIRS.get());
			this.dropSelf(ModBlocks.DARK_SAND_WALL.get());
			this.dropSelf(ModBlocks.CLAY_FENCE.get());
			this.dropSelf(ModBlocks.CLAY_GATE.get());
			this.dropSelf(ModBlocks.CLAY_BUTTON.get());
			this.dropSelf(ModBlocks.CLAY_SLAB.get());
			this.dropSelf(ModBlocks.CLAY_STAIRS.get());
			this.dropSelf(ModBlocks.CLAY_WALL.get());
			this.dropSelf(ModBlocks.MUD_FENCE.get());
			this.dropSelf(ModBlocks.MUD_GATE.get());
			this.dropSelf(ModBlocks.MUD_BUTTON.get());
			this.dropSelf(ModBlocks.MUD_SLAB.get());
			this.dropSelf(ModBlocks.MUD_STAIRS.get());
			this.dropSelf(ModBlocks.UNRAVELED_FENCE.get());
			this.dropSelf(ModBlocks.UNRAVELED_GATE.get());
			this.dropSelf(ModBlocks.UNRAVELED_BUTTON.get());
			this.dropSelf(ModBlocks.UNRAVELED_SLAB.get());
			this.dropSelf(ModBlocks.UNRAVELED_STAIRS.get());
			this.dropSelf(ModBlocks.DEEPSLATE_SLAB.get());
			this.dropSelf(ModBlocks.DEEPSLATE_STAIRS.get());
			this.dropSelf(ModBlocks.DEEPSLATE_WALL.get());
			this.dropSelf(ModBlocks.RED_SAND_SLAB.get());
			this.dropSelf(ModBlocks.RED_SAND_STAIRS.get());
			this.dropSelf(ModBlocks.RED_SAND_WALL.get());
			this.dropSelf(ModBlocks.SAND_SLAB.get());
			this.dropSelf(ModBlocks.SAND_STAIRS.get());
			this.dropSelf(ModBlocks.SAND_WALL.get());
			this.dropSelf(ModBlocks.END_STONE_SLAB.get());
			this.dropSelf(ModBlocks.END_STONE_STAIRS.get());
			this.dropSelf(ModBlocks.END_STONE_WALL.get());
			this.dropSelf(ModBlocks.NETHERRACK_FENCE.get());
			this.dropSelf(ModBlocks.NETHERRACK_SLAB.get());
			this.dropSelf(ModBlocks.NETHERRACK_STAIRS.get());
			this.dropSelf(ModBlocks.NETHERRACK_WALL.get());
			this.dropSelf(ModBlocks.UNRAVELED_SPIKE.get());
			this.dropSelf(ModBlocks.GRITTY_STONE.get());
		}
}

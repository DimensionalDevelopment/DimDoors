package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LootTableProvider extends FabricBlockLootTableProvider {

	public LootTableProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture) {
		super(dataGenerator, completableFuture);
	}
	@Override
	public void generate() {
        for (RegistrySupplier<Block> block : ModBlocks.FABRIC_BLOCKS.values()) {
            this.dropWhenSilkTouch(block.get());
        }
        this.dropSelf(ModBlocks.GOLD_DOOR.get());
        this.dropSelf(ModBlocks.QUARTZ_DOOR.get());
        this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR.get());
        this.dropWhenSilkTouch(ModBlocks.MARKING_PLATE.get());

        this.add(ModBlocks.SOLID_STATIC.get(), (blockx) -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER.get()));

        this.add(ModBlocks.UNRAVELLED_FABRIC.get(), block ->
                this.createSilkTouchDispatchTable(
                        block,
                        LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS.get())
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE),
                                        0.1F, 0.14285715F, 0.25F, 1.0F
                                ))
                                .when(ExplosionCondition.survivesExplosion())
                                .otherwise(
                                        LootItem.lootTableItem(block)
                                                .when(ExplosionCondition.survivesExplosion())
                                )
                )
        );
        this.dropSelf(ModBlocks.TESSELATING_LOOM.get());
        this.dropSelf(ModBlocks.REALITY_SPONGE.get());

        this.dropSelf(ModBlocks.DRIFTWOOD_WOOD.get());
        this.dropSelf(ModBlocks.DRIFTWOOD_LOG.get());
        this.dropSelf(ModBlocks.DRIFTWOOD_PLANKS.get());
        this.dropSelf(ModBlocks.DRIFTWOOD_LEAVES.get());
        add(ModBlocks.DRIFTWOOD_LEAVES.get(), block -> {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return createLeavesDrops(block, ModBlocks.DRIFTWOOD_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(this.doesNotHaveShearsOrSilkTouch()).add((this.applyExplosionCondition(block, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS.get()))).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
        });


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
        this.add(ModBlocks.AMALGAM_ORE.get(), (blockx) -> createOreDrop(blockx, ModItems.AMALGAM_LUMP.get()));
        this.add(ModBlocks.CLOD_ORE.get(), (blockx) -> createOreDrop(blockx, ModItems.CLOD.get()));
        this.dropSelf(ModBlocks.CLOD_BLOCK.get());


        this.dropSelf(ModBlocks.DARK_SAND.get());

        this.dropSelf(ModBlocks.GRAVEL_SET);
        this.dropSelf(ModBlocks.DARK_SAND_SET);
        this.dropSelf(ModBlocks.CLAY_SET);
        this.dropSelf(ModBlocks.TERRACOTTA_SET);
        this.dropSelf(ModBlocks.WHITE_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.WHITE_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.ORANGE_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.MAGENTA_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.LIGHT_BLUE_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.YELLOW_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.LIME_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.LIME_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.PINK_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.PINK_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.GRAY_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.GRAY_GLAZED_TERRACOTTASET);
        this.dropSelf(ModBlocks.LIGHT_GRAY_TERRACOTTASET);
        this.dropSelf(ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTASET);
        this.dropSelf(ModBlocks.CYAN_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.CYAN_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.PURPLE_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BLUE_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BLUE_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BROWN_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BROWN_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.GREEN_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.GREEN_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.RED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.RED_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BLACK_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.BLACK_GLAZED_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.MUD_SET);
        this.dropSelf(ModBlocks.UNRAVELED_SET);
        this.dropSelf(ModBlocks.DEEPSLATE_SET);
        this.dropSelf(ModBlocks.RED_SAND_SET);
        this.dropSelf(ModBlocks.SAND_SET);
        this.dropSelf(ModBlocks.END_STONE_SET);
        this.dropSelf(ModBlocks.NETHERRACK_SET);
        this.dropSelf(ModBlocks.UNRAVELED_SPIKE.get());
        this.dropSelf(ModBlocks.GRITTY_STONE.get());
    }

    private void dropSelf(ModBlocks.DecayGroupSet set) {
        dropSelf(set.fence().get());
        dropSelf(set.gate().get());
        dropSelf(set.button().get());
        dropSelf(set.slab().get());
        dropSelf(set.stairs().get());
        dropSelf(set.wall().get());
    }
}

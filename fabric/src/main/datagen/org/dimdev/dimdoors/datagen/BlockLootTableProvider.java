package org.dimdev.dimdoors.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModLootTables;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {

    public BlockLootTableProvider(FabricDataOutput dataGenerator, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(dataGenerator, completableFuture);
    }
    @Override
    public void generate() {
        for (Block block : ModBlocks.FABRIC_BLOCKS.values()) {
            this.dropWhenSilkTouch(block);
        }
        this.add(ModBlocks.GOLD_DOOR, this::createDoorTable);
        this.add(ModBlocks.QUARTZ_DOOR, this::createDoorTable);
        this.add(ModBlocks.STONE_DOOR, this::createDoorTable);

//        this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
        this.dropWhenSilkTouch(ModBlocks.MARKING_PLATE);

        this.add(ModBlocks.SOLID_STATIC, (blockx) -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER));

        this.add(ModBlocks.UNRAVELLED_FABRIC, block ->
                this.createSilkTouchDispatchTable(
                        block,
                        LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS)
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
        this.dropSelf(ModBlocks.TESSELATING_LOOM);
        this.dropSelf(ModBlocks.REALITY_SPONGE);

        this.dropSelf(ModBlocks.DRIFTWOOD_WOOD);
        this.dropSelf(ModBlocks.DRIFTWOOD_LOG);
        this.dropSelf(ModBlocks.DRIFTWOOD_PLANKS);
        this.dropSelf(ModBlocks.DRIFTWOOD_LEAVES);
        add(ModBlocks.DRIFTWOOD_LEAVES, block -> {
            HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
            return createLeavesDrops(block, ModBlocks.DRIFTWOOD_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(this.doesNotHaveShearsOrSilkTouch()).add((this.applyExplosionCondition(block, LootItem.lootTableItem(ModItems.FRAYED_FILAMENTS))).when(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
        });


        this.dropSelf(ModBlocks.DRIFTWOOD_SAPLING);
        this.dropSelf(ModBlocks.DRIFTWOOD_FENCE);
        this.dropSelf(ModBlocks.DRIFTWOOD_GATE);
        this.dropSelf(ModBlocks.DRIFTWOOD_BUTTON);
        this.dropSelf(ModBlocks.DRIFTWOOD_SLAB);
        this.dropSelf(ModBlocks.DRIFTWOOD_STAIRS);
        this.add(ModBlocks.DRIFTWOOD_DOOR, this::createDoorTable);
        this.dropSelf(ModBlocks.DRIFTWOOD_TRAPDOOR);
        this.dropSelf(ModBlocks.AMALGAM_BLOCK);
        this.add(ModBlocks.AMALGAM_DOOR, this::createDoorTable);
        this.dropSelf(ModBlocks.AMALGAM_TRAPDOOR);
        this.dropSelf(ModBlocks.RUST);
        this.dropSelf(ModBlocks.AMALGAM_SLAB);
        this.dropSelf(ModBlocks.AMALGAM_STAIRS);
        this.add(ModBlocks.AMALGAM_ORE, (blockx) -> createOreDrop(blockx, ModItems.AMALGAM_LUMP));
        this.add(ModBlocks.CLOD_ORE, (blockx) -> createOreDrop(blockx, ModItems.CLOD));
        this.dropSelf(ModBlocks.CLOD_BLOCK);


        this.dropSelf(ModBlocks.DARK_SAND);
        this.dropSelf(ModBlocks.PALE_SAND);
        this.dropSelf(ModBlocks.DARK_SAND_LAYER);
        this.dropSelf(ModBlocks.LINT_LAYER);
        this.dropSelf(ModBlocks.STONE_SLAB);
        this.dropSelf(ModBlocks.STONE_STAIRS);
        this.dropSelf(ModBlocks.STONE_WALL);

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
        this.dropSelf(ModBlocks.LIGHT_GRAY_TERRACOTTA_SET);
        this.dropSelf(ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET);
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
        this.dropSelf(ModBlocks.UNRAVELED_SPIKE);
        this.dropSelf(ModBlocks.GRITTY_STONE);
    }


    private void dropSelf(ModBlocks.DecayGroupSet set) {
        dropSelf(set.fence());
        dropSelf(set.gate());
        dropSelf(set.button());
        dropSelf(set.slab());
        dropSelf(set.stairs());
        dropSelf(set.wall());
    }
}

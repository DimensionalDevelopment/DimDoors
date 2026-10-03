package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.ModItems
import java.util.concurrent.CompletableFuture

class BlockLootTableProvider(dataGenerator: FabricDataOutput, completableFuture: CompletableFuture<HolderLookup.Provider>) : FabricBlockLootTableProvider(dataGenerator, completableFuture) {
    override fun generate() {
        for (block in ModBlocks.FABRIC_BLOCKS.values) {
            dropWhenSilkTouch(block)
        }
        add(ModBlocks.GOLD_DOOR) { createDoorTable(it) }
        add(ModBlocks.QUARTZ_DOOR) { createDoorTable(it) }
        add(ModBlocks.STONE_DOOR) { createDoorTable(it) }
        add(ModBlocks.DIALING_DOOR) { createDoorTable(it) }

//        this.dropWhenSilkTouch(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
        dropWhenSilkTouch(ModBlocks.MARKING_PLATE)

        add(ModBlocks.SOLID_STATIC) { blockx -> createOreDrop(blockx, ModItems.INFRANGIBLE_FIBER) }

        add(ModBlocks.UNRAVELLED_FABRIC) { block ->
            createSilkTouchDispatchTable(
                block,
                LootItem.lootTableItem(ModItems.FRAYED_FILAMENT)
                    .`when`(BonusLevelTableCondition.bonusLevelFlatChance(
                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE),
                        0.1f, 0.14285715f, 0.25f, 1.0f
                    ))
                    .`when`(ExplosionCondition.survivesExplosion())
                    .otherwise(
                        LootItem.lootTableItem(block)
                            .`when`(ExplosionCondition.survivesExplosion())
                    )
            )
        }
        dropSelf(ModBlocks.TESSELATING_LOOM)
        dropSelf(ModBlocks.REALITY_SPONGE)
        dropSelf(ModBlocks.LIMINAL_TRANSMITTER)

        dropSelf(ModBlocks.DRIFTWOOD_WOOD)
        dropSelf(ModBlocks.DRIFTWOOD_LOG)
        dropSelf(ModBlocks.DRIFTWOOD_PLANKS)
        dropSelf(ModBlocks.DRIFTWOOD_LEAVES)
        add(ModBlocks.DRIFTWOOD_LEAVES) { block ->
            val registryLookup = registries.lookupOrThrow(Registries.ENCHANTMENT)
            createLeavesDrops(block, ModBlocks.DRIFTWOOD_SAPLING, *NORMAL_LEAVES_SAPLING_CHANCES).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0f)).`when`(doesNotHaveShearsOrSilkTouch()).add(applyExplosionCondition(block, LootItem.lootTableItem(ModItems.FRAYED_FILAMENT)).`when`(BonusLevelTableCondition.bonusLevelFlatChance(registryLookup.getOrThrow(Enchantments.FORTUNE), 0.005f, 0.0055555557f, 0.00625f, 0.008333334f, 0.025f))))
        }


        dropSelf(ModBlocks.DRIFTWOOD_SAPLING)
        dropSelf(ModBlocks.DRIFTWOOD_FENCE)
        dropSelf(ModBlocks.DRIFTWOOD_GATE)
        dropSelf(ModBlocks.DRIFTWOOD_BUTTON)
        dropSelf(ModBlocks.DRIFTWOOD_SLAB)
        dropSelf(ModBlocks.DRIFTWOOD_STAIRS)
        add(ModBlocks.DRIFTWOOD_DOOR) { createDoorTable(it) }
        dropSelf(ModBlocks.DRIFTWOOD_TRAPDOOR)
        dropSelf(ModBlocks.AMALGAM_BLOCK)
        add(ModBlocks.AMALGAM_DOOR) { createDoorTable(it) }
        dropSelf(ModBlocks.AMALGAM_TRAPDOOR)
        dropSelf(ModBlocks.RUST)
        dropSelf(ModBlocks.AMALGAM_SLAB)
        dropSelf(ModBlocks.AMALGAM_STAIRS)
        add(ModBlocks.AMALGAM_ORE) { blockx -> createOreDrop(blockx, ModItems.AMALGAM_LUMP) }
        add(ModBlocks.CLOD_ORE) { blockx -> createOreDrop(blockx, ModItems.CLOD) }
        dropSelf(ModBlocks.CLOD_BLOCK)


        dropSelf(ModBlocks.DARK_SAND)
        dropSelf(ModBlocks.PALE_SAND)
        dropSelf(ModBlocks.DARK_SAND_LAYER)
        dropSelf(ModBlocks.LINT_LAYER)
        dropSelf(ModBlocks.STONE_SLAB)
        dropSelf(ModBlocks.STONE_STAIRS)
        dropSelf(ModBlocks.STONE_WALL)

        dropSelf(ModBlocks.GRAVEL_SET)
        dropSelf(ModBlocks.DARK_SAND_SET)
        dropSelf(ModBlocks.CLAY_SET)
        dropSelf(ModBlocks.TERRACOTTA_SET)
        dropSelf(ModBlocks.WHITE_TERRACOTTA_SET)
        dropSelf(ModBlocks.WHITE_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.ORANGE_TERRACOTTA_SET)
        dropSelf(ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.MAGENTA_TERRACOTTA_SET)
        dropSelf(ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIGHT_BLUE_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.YELLOW_TERRACOTTA_SET)
        dropSelf(ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIME_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIME_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.PINK_TERRACOTTA_SET)
        dropSelf(ModBlocks.PINK_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.GRAY_TERRACOTTA_SET)
        dropSelf(ModBlocks.GRAY_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIGHT_GRAY_TERRACOTTA_SET)
        dropSelf(ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.CYAN_TERRACOTTA_SET)
        dropSelf(ModBlocks.CYAN_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.PURPLE_TERRACOTTA_SET)
        dropSelf(ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.BLUE_TERRACOTTA_SET)
        dropSelf(ModBlocks.BLUE_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.BROWN_TERRACOTTA_SET)
        dropSelf(ModBlocks.BROWN_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.GREEN_TERRACOTTA_SET)
        dropSelf(ModBlocks.GREEN_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.RED_TERRACOTTA_SET)
        dropSelf(ModBlocks.RED_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.BLACK_TERRACOTTA_SET)
        dropSelf(ModBlocks.BLACK_GLAZED_TERRACOTTA_SET)
        dropSelf(ModBlocks.MUD_SET)
        dropSelf(ModBlocks.UNRAVELED_SET)
        dropSelf(ModBlocks.DEEPSLATE_SET)
        dropSelf(ModBlocks.RED_SAND_SET)
        dropSelf(ModBlocks.SAND_SET)
        dropSelf(ModBlocks.END_STONE_SET)
        dropSelf(ModBlocks.NETHERRACK_SET)
        dropSelf(ModBlocks.UNRAVELED_SPIKE)
        dropSelf(ModBlocks.GRITTY_STONE)
    }


    private fun dropSelf(set: ModBlocks.DecayGroupSet) {
        dropSelf(set.fence)
        dropSelf(set.gate)
        dropSelf(set.button)
        dropSelf(set.slab)
        dropSelf(set.stairs)
        dropSelf(set.wall)
    }
}

package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.world.ModLootTables;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class BlockUseLootTableProvider extends SimpleFabricLootTableProvider {
    public BlockUseLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.BLOCK_USE);
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(ModLootTables.REMOVED_RIFT, LootTable.lootTable().withPool(
                LootPool.lootPool().add(LootItem.lootTableItem(ModItems.WORLD_THREAD))
        ));
    }
}

package org.dimdev.dimdoors.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.commons.lang3.stream.Streams;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModLootTables;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ChestLootTableProvider extends SimpleFabricLootTableProvider {

    private final HolderLookup.Provider registries;

    public ChestLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup, LootContextParamSets.CHEST);
        this.registries = registryLookup.join();
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        biConsumer.accept(ModLootTables.DUNGEON_CHEST, LootTable.lootTable().withPool(
                        LootPool.lootPool()
                                .setRolls(UniformGenerator.between(2, 6))
                                .add(item(Items.DIAMOND, 1, 2, 4))
                                .add(item(Items.DIAMOND, 1, 3, 16))
                                .add(item(Items.GOLD_INGOT, 1, 3, 8))
                                .add(item(Items.EMERALD, 1, 2, 2))
                                .add(item(Items.COAL, 1, 3, 12))
                                .add(item(Items.QUARTZ, 1, 3, 12))
                                .add(item(Items.DIAMOND, 2, 8, 8))
                                .add(item(ModBlocks.LIME_FABRIC, 16, 64, 2))
                                .add(item(Items.BOOK, 10))
                                .add(item(Items.GOLDEN_APPLE, 1))
                )
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.BOOK).apply(
                                new EnchantWithLevelsFunction.Builder(ConstantValue.exactly(10)).fromOptions(
                                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.TREASURE)
                                )))
                        .add(EmptyLootItem.emptyItem().setWeight(14))
                ));

        biConsumer.accept(ModLootTables.DISPENSER_POTION_ARROWS, potionTable(Items.TIPPED_ARROW));
        biConsumer.accept(ModLootTables.DISPENSER_SPLASH_POTIONS, potionTable(Items.SPLASH_POTION));

        biConsumer.accept(ModLootTables.DISPENSER_PROJECTILES, LootTable.lootTable().withPool(
                LootPool.lootPool()
                        .setRolls(UniformGenerator.between(2, 3))
                        .add(item(Items.ARROW, 2, 5, 100))
                        .add(item(Items.FIRE_CHARGE, 1, 5, 15))
                        .add(item(Items.SPECTRAL_ARROW, 1, 2, 1))
                        .add(item(Items.SNOWBALL, 4, 16, 10))
                        .add(item(Items.WATER_BUCKET, 5))
                        .add(item(Items.LAVA_BUCKET, 2))
                        .add(lootTable(ModLootTables.DISPENSER_SPLASH_POTIONS, 15))
                        .add(lootTable(ModLootTables.DISPENSER_POTION_ARROWS, 15))

        ));

    }

    public LootTable.Builder potionTable(Item item) {
        var empty25 = EmptyLootItem.emptyItem().setWeight(25);

        Function<Holder<Potion>, LootPool.Builder> potionFunction = (potionHolder) -> LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(item)
                        .apply(SetPotionFunction.setPotion(potionHolder))
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5)))
                        .setWeight(4)
                ).add(empty25);

        return LootTable.lootTable()
                .pools(Streams.of(BuiltInRegistries.POTION.asHolderIdMap().iterator()).map(a -> potionFunction.apply(a).build()).toList());
    }

    public LootPoolSingletonContainer.Builder<?> item(ItemLike item, int min, int max, int weight) {
        return LootItem.lootTableItem(item).apply(
                SetItemCountFunction.setCount(UniformGenerator.between(min, max))).setWeight(weight);
    }

    public LootPoolSingletonContainer.Builder<?> item(ItemLike item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight);
    }

    public LootPoolSingletonContainer.Builder<?> lootTable(ResourceKey<LootTable> key, int weight) {
        return NestedLootTable.lootTableReference(key).setWeight(weight);
    }
}

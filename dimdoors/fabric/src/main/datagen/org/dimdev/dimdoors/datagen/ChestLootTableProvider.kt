package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer
import net.minecraft.world.level.storage.loot.entries.NestedLootTable
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.tag.ModEnchantmentTags
import org.dimdev.dimdoors.world.ModLootTables
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class ChestLootTableProvider(output: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) : SimpleFabricLootTableProvider(output, registryLookup, LootContextParamSets.CHEST) {

    private val registries: HolderLookup.Provider = registryLookup.join()

    override fun generate(biConsumer: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
        biConsumer.accept(ModLootTables.DUNGEON_CHEST, LootTable.lootTable().withPool(
                LootPool.lootPool()
                    .setRolls(UniformGenerator.between(2f, 6f))
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
            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
                .add(LootItem.lootTableItem(Items.BOOK).apply(
                    EnchantRandomlyFunction.Builder().withOneOf(
                        registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(ModEnchantmentTags.DUNGEON_LOOT)
                    )))
                .add(EmptyLootItem.emptyItem().setWeight(14))
            ))

        biConsumer.accept(ModLootTables.DISPENSER_POTION_ARROWS, potionTable(Items.TIPPED_ARROW))
        biConsumer.accept(ModLootTables.DISPENSER_SPLASH_POTIONS, potionTable(Items.SPLASH_POTION))

        biConsumer.accept(ModLootTables.DISPENSER_PROJECTILES, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(UniformGenerator.between(2f, 3f))
                .add(item(Items.ARROW, 2, 5, 100))
                .add(item(Items.FIRE_CHARGE, 1, 5, 15))
                .add(item(Items.SPECTRAL_ARROW, 1, 2, 1))
                .add(item(Items.SNOWBALL, 4, 16, 10))
                .add(item(Items.WATER_BUCKET, 5))
                .add(item(Items.LAVA_BUCKET, 2))
                .add(lootTable(ModLootTables.DISPENSER_SPLASH_POTIONS, 15))
                .add(lootTable(ModLootTables.DISPENSER_POTION_ARROWS, 15))

        ))

    }

    fun potionTable(item: Item): LootTable.Builder {
        val empty25 = EmptyLootItem.emptyItem().setWeight(25)

        val potionFunction = { potionHolder: Holder<Potion> -> LootPool.lootPool().setRolls(ConstantValue.exactly(1f))
            .add(LootItem.lootTableItem(item)
                .apply(SetPotionFunction.setPotion(potionHolder))
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2f, 5f)))
                .setWeight(4)
            ).add(empty25)
        }

        return LootTable.lootTable()
            .pools(BuiltInRegistries.POTION.asHolderIdMap().map { potionFunction(it).build() })
    }

    fun item(item: ItemLike, min: Int, max: Int, weight: Int): LootPoolSingletonContainer.Builder<*> =
        LootItem.lootTableItem(item).apply(
            SetItemCountFunction.setCount(UniformGenerator.between(min.toFloat(), max.toFloat()))).setWeight(weight)

    fun item(item: ItemLike, weight: Int): LootPoolSingletonContainer.Builder<*> = LootItem.lootTableItem(item).setWeight(weight)

    fun lootTable(key: ResourceKey<LootTable>, weight: Int): LootPoolSingletonContainer.Builder<*> = NestedLootTable.lootTableReference(key).setWeight(weight)
}

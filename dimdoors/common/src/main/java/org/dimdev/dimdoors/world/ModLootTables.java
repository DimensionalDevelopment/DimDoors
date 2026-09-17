package org.dimdev.dimdoors.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModLootTables {
    public static final ResourceKey<LootTable> DUNGEON_CHEST = ResourceKey.create(Registries.LOOT_TABLE, DimensionalDoors.id("chest/dungeon_chest"));
    public static final ResourceKey<LootTable> DISPENSER_PROJECTILES = ResourceKey.create(Registries.LOOT_TABLE, DimensionalDoors.id("chest/dispenser_projectiles"));
    public static final ResourceKey<LootTable> REMOVED_RIFT = ResourceKey.create(Registries.LOOT_TABLE, DimensionalDoors.id("block_use/removed_rift"));
    public static final ResourceKey<LootTable> DISPENSER_SPLASH_POTIONS = ResourceKey.create(Registries.LOOT_TABLE, DimensionalDoors.id("chest/dispenser_splash_potions"));
    public static final ResourceKey<LootTable> DISPENSER_POTION_ARROWS = ResourceKey.create(Registries.LOOT_TABLE, DimensionalDoors.id("chest/dispenser_potion_arrows"));
}
package org.dimdev.dimdoors.item.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModItemLootConditions {
    public static LootItemConditionType ENTITY_NEARBY;

    private ModItemLootConditions() {
    }

    public static void init() {
        ENTITY_NEARBY = register("entity_nearby", EntityNearBy.CODEC);
    }

    private static LootItemConditionType register(String id, MapCodec<? extends LootItemCondition> codec) {
        return DimensionalDoors.getSided().register(
                Registries.LOOT_CONDITION_TYPE,
                id,
                new LootItemConditionType(codec)
        );
    }
}

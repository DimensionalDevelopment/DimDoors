package org.dimdev.dimdoors.datagen

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.DisplayInfo
import net.minecraft.advancements.critereon.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Blocks
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.criteria.ModCriteria
import org.dimdev.dimdoors.criteria.PocketSpawnPointSetCondition
import org.dimdev.dimdoors.criteria.RiftTrackedCriterion
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.tag.ModWorldTags.tag
import org.dimdev.dimdoors.world.ModDimensions
import java.util.*
import java.util.function.Consumer

class AdvancementTab : Consumer<Consumer<AdvancementHolder>> {
    override fun accept(advancementConsumer: Consumer<AdvancementHolder>) {


        val root = Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.RIFT_BLADE, "root"))
            .addCriterion("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENDER_PEARL))
            .save(advancementConsumer, "dimdoors:dimdoors/root")
        Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.WORLD_THREAD, "string_theory"))
            .addCriterion("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.WORLD_THREAD))
            .parent(root)
            .save(advancementConsumer, "dimdoors:dimdoors/string_theory")
        val holeInTheSky = Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.RIFT_CONFIGURATION_TOOL, "hole_in_the_sky"))
            .addCriterion(
                "encounter_rift", ModCriteria.RIFT_TRACKED.createCriterion(
                    RiftTrackedCriterion.TriggerInstance(
                        Optional.empty<ContextAwarePredicate?>()
                    )
                )
            )
            .parent(root)
            .save(advancementConsumer, "dimdoors:dimdoors/hole_in_the_sky")
        val darkOstiology = Advancement.Builder.advancement()
            .display(
                makeDisplay(
                    BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_oak_door")),
                    "dark_ostiology"
                )
            )
            .addCriterion(
                "place_door",
                EnterBlockTrigger.TriggerInstance.entersBlock(BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_oak_door")))
            )
            .parent(holeInTheSky)
            .save(advancementConsumer, "dimdoors:dimdoors/dark_ostiology")
        Advancement.Builder.advancement()
            .display(
                makeDisplay(
                    BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_iron_door")),
                    "public_pocket"
                )
            )
            .parent(darkOstiology)
            .addCriterion(
                "public_pocket",
                ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.PUBLIC)
            )
            .save(advancementConsumer, "dimdoors:dimdoors/public_pocket")
        Advancement.Builder.advancement()
            .display(
                makeDisplay(
                    BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_minecraft_iron_door")),
                    "home_away_from_home"
                )
            )
            .parent(darkOstiology)
            .addCriterion(
                "private_pocket",
                ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.PERSONAL)
            )
            .save(advancementConsumer, "dimdoors:dimdoors/home_away_from_home")
        Advancement.Builder.advancement()
            .display(makeDisplay(Blocks.RESPAWN_ANCHOR, "out_of_time"))
            .addCriterion(
                "spawn", ModCriteria.POCKET_SPAWN_POINT_SET.createCriterion(
                    PocketSpawnPointSetCondition.TriggerInstance(
                        Optional.empty<ContextAwarePredicate?>()
                    )
                )
            )
            .parent(darkOstiology)
            .save(advancementConsumer, "dimdoors:dimdoors/out_of_time")
        val doorToAdventure = Advancement.Builder.advancement()
            .display(
                makeDisplay(
                    BuiltInRegistries.BLOCK.get(DimensionalDoors.id("block_ag_dim_dimdoors_gold_door")),
                    "door_to_adventure"
                )
            )
            .parent(holeInTheSky)
            .addCriterion(
                "enter_dungeon",
                ChangeDimensionTrigger.TriggerInstance.changedDimensionTo((ModDimensions.DUNGEON))
            )
            .save(advancementConsumer, "dimdoors:dimdoors/door_to_adventure")
        //        Advancement.Builder.advancement() TODO: Figure out what the heck ItemUsedOnBlockCriterion
//                .display(makeDisplay(Items.CHEST, "lost_and_found"))
//                .parent(doorToAdventure)
//                .addCriterion("open_chest", new ItemUsedOnBlockCriterion.Conditions
//                        (
//                                EntityPredicate.Extended.EMPTY,
//                                new LocationPredicate(
//                                        NumberRange.FloatRange.ANY,
//                                        NumberRange.FloatRange.ANY,
//                                        NumberRange.FloatRange.ANY,
//                                        null,
//                                        null,
//                                        ModDimensions.DUNGEON,
//                                        null,
//                                        LightPredicate.ANY,
//                                        BlockPredicate.Builder.create().blocks(Blocks.CHEST, Blocks.TRAPPED_CHEST).save(),
//                                FluidPredicate.ANY
//                                ),
//                                new ItemPredicate(
//                                        null,
//                                        null,
//                                        NumberRange.IntRange.ANY,
//                                        NumberRange.IntRange.ANY,
//                                        EnchantmentPredicate.ARRAY_OF_ANY,
//                                        EnchantmentPredicate.ARRAY_OF_ANY,
//                                        null,
//                                        NbtPredicate.ANY
//                                )
//                        )
//                )
//                .save(advancementConsumer, "dimdoors:dimdoors/lost_and_found");
        Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModBlocks.BLACK_FABRIC, "darklight"))
            .parent(doorToAdventure)
            .addCriterion(
                "get_fabric", InventoryChangeTrigger.TriggerInstance.hasItems(
                    ItemPredicate.Builder.item().of(
                        Registries.ITEM.tag("fabric")
                    ).build()
                )
            )
            .save(advancementConsumer, "dimdoors:dimdoors/darklight")
        val enterLimbo = Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.MONOLITH_SPAWNER, "enter_limbo"))
            .parent(doorToAdventure)
            .addCriterion("enter_limbo", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.LIMBO))
            .save(advancementConsumer, "dimdoors:dimdoors/enter_limbo")
        Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModBlocks.UNRAVELLED_FABRIC, "world_unfurled"))
            .parent(enterLimbo)
            .addCriterion(
                "get_the_unravelled",
                InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.UNRAVELLED_FABRIC)
            )
            .save(advancementConsumer, "dimdoors:dimdoors/world_unfurled")

        Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.INFRANGIBLE_FIBER, "unravelled_but_immutable"))
            .parent(enterLimbo)
            .addCriterion(
                "get_the_immutable",
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER)
            )
            .save(advancementConsumer, "dimdoors:dimdoors/unravelled_but_immutable")

        Advancement.Builder.advancement()
            .display(Companion.makeDisplay(ModItems.FRAYED_FILAMENT, "fuzzy_unreality"))
            .parent(enterLimbo)
            .addCriterion(
                "get_the_immutable",
                InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.FRAYED_FILAMENT)
            )
            .save(advancementConsumer, "dimdoors:dimdoors/fuzzy_unreality")

        //        Advancement.Task.create()
//                .display(makeDisplay(ModItems.IRON_DIMENSIONAL_DOOR, "public_pocket"))
//                .addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PUBLIC))
//                .parent(root)
//                .save(advancementConsumer, "dimdoors:dimdoors/public_pocket");
//        Advancement.Task.create()
//                .display(makeDisplay(ModItems.QUARTZ_DIMENSIONAL_DOOR, "private_pocket"))
//                .addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.PERSONAL))
//                .parent(root)
//                .save(advancementConsumer, "dimdoors:dimdoors/private_pocket");
//        Advancement.Task.create()
//                .display(makeDisplay(ModItems.GOLD_DIMENSIONAL_DOOR, "dungeon"))
//                .addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.DUNGEON))
//                .parent(root)
//                .save(advancementConsumer, "dimdoors:dimdoors/dungeon");
//        Advancement limbo = Advancement.Task.create()
//                .display(makeDisplay(ModItems.UNRAVELLED_FABRIC, "limbo"))
//                .addCriterion("changed_dimension", ChangedDimensionCriterion.Conditions.to(ModDimensions.LIMBO))
//                .parent(root)
//                .save(advancementConsumer, "dimdoors:dimdoors/limbo");
//        Advancement.Task.create()
//                .display(makeDisplay(ModItems.ETERNAL_FLUID_BUCKET, "escape_limbo"))
//                .addCriterion("changed_dimension", new ChangedDimensionCriterion.Conditions(EntityPredicate.Extended.EMPTY, ModDimensions.LIMBO, null))
//                .parent(limbo)
//                .save(advancementConsumer, "dimdoors:dimdoors/escape_limbo");
    }

    companion object {
        fun makeDisplay(item: ItemLike, titleKey: String?): DisplayInfo {
            return DisplayInfo(
                item.asItem().getDefaultInstance(),
                Component.translatable("advancement.dimdoors.$titleKey"),
                Component.translatable("advancement.dimdoors.$titleKey.desc"),
                Optional.of(DimensionalDoors.id("textures/block/unravelled_fabric.png")),
                AdvancementType.TASK,
                true,
                true,
                false
            )
        }

        fun makeDisplay(item: ItemLike, titleKey: String?, advancementFrame: AdvancementType?): DisplayInfo {
            return DisplayInfo(
                item.asItem().getDefaultInstance(),
                Component.translatable("dimdoors.advancement." + titleKey),
                Component.translatable("dimdoors.advancement." + titleKey + ".desc"),
                Optional.of(DimensionalDoors.id("textures/block/unravelled_fabric.png")),
                advancementFrame,
                true,
                true,
                false
            )
        }
    }
}

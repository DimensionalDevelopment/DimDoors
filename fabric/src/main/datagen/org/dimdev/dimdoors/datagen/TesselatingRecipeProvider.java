package org.dimdev.dimdoors.datagen;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.item.ModItems;

public class TesselatingRecipeProvider {
    public static void generate(RecipeOutput exporter) {
        ShapelessTesselatingRecipeBuilder.shapeless(ModItems.RIFT_BLADE).requires(Items.IRON_SWORD).requires(Items.ENDER_PEARL, 2).unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_SWORD)).save(exporter, DimensionalDoors.id("rift_blade"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.STABLE_FABRIC)
                .pattern("XX")
                .pattern("XX")
                .define('X', ModItems.WORLD_THREAD)
                .unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD))
                .save(exporter, DimensionalDoors.id("stable_fabric"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.LIMINAL_LINT)
                .pattern("XX")
                .pattern("XX")
                .define('X', ModItems.FRAYED_FILAMENT)
                .unlockedBy("frayed_filaments", TriggerInstance.hasItems(ModItems.FRAYED_FILAMENT))
                .save(exporter, DimensionalDoors.id("liminal_lint"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.ENDURING_FIBERS)
                .pattern("XX")
                .pattern("XX")
                .define('X', ModItems.INFRANGIBLE_FIBER)
                .unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER))
                .save(exporter, DimensionalDoors.id("enduring_fibers"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.RIFT_PEARL)
                .pattern("XO")
                .define('X', Ingredient.of(ModItems.STABLE_FABRIC)).define('O', Items.ENDER_PEARL).unlockedBy("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC)).save(exporter, DimensionalDoors.id("rift_pearl"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModBlocks.BLACK_FABRIC)
                .pattern("XX")
                .pattern("XO").define('O', ModItems.STABLE_FABRIC).define('X', ModItems.WORLD_THREAD).unlockedBy("stable_fabric", TriggerInstance.hasItems(ModItems.STABLE_FABRIC)).save(exporter, DimensionalDoors.id("fabric_of_reality"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.FUZZY_FIREBALL)
                .pattern("XOX")
                .define('X', ModItems.LIMINAL_LINT)
                .define('O', Items.FIRE_CHARGE)
                .unlockedBy("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT))
                .save(exporter, DimensionalDoors.id("fuzzy_fireball"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.FABRIC_OF_FINALITY)
                .pattern("XOX").define('X', ModItems.ENDURING_FIBERS)
                .define('O', Items.DRAGON_BREATH)
                .unlockedBy("enduring_fabric", TriggerInstance.hasItems(ModItems.ENDURING_FIBERS)).save(exporter, DimensionalDoors.id("fabric_of_finality"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModBlocks.REALITY_SPONGE.asItem())
                .pattern("XOX").
                pattern("OXO")
                .pattern("XOX").define('X', ModItems.STABLE_FABRIC).define('O', ModItems.INFRANGIBLE_FIBER).unlockedBy("liminal_lint", TriggerInstance.hasItems(ModItems.LIMINAL_LINT)).save(exporter, DimensionalDoors.id("reality_sponge"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.WORLD_THREAD_ARMOR.helmet())
                .pattern("XXX").
                pattern("X X")
                .define('X', ModItems.WORLD_THREAD).unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD)).save(exporter, DimensionalDoors.id("world_thread_helmet"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.WORLD_THREAD_ARMOR.chestplate())
                .pattern("X X")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', ModItems.WORLD_THREAD)
                .unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD))
                .save(exporter, DimensionalDoors.id("world_thread_chestplate"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.WORLD_THREAD_ARMOR.leggings())
                .pattern("XXX").
                pattern("X X")
                .pattern("X X")
                .define('X', ModItems.WORLD_THREAD)
                .unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD))
                .save(exporter, DimensionalDoors.id("world_thread_leggings"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.WORLD_THREAD_ARMOR.boots())
                .pattern("X X")
                .pattern("X X")
                .define('X', ModItems.WORLD_THREAD)
                .unlockedBy("world_thread", TriggerInstance.hasItems(ModItems.WORLD_THREAD))
                .save(exporter, DimensionalDoors.id("world_thread_boots"));

        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.GARMENT_OF_REALITY_ARMOR.boots())
                  .pattern("XXX")
                .pattern("XOX")
                .define('X', ModItems.STABLE_FABRIC)
                .define('O', ModItems.INFRANGIBLE_FIBER)
                .unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER))
                .save(exporter, DimensionalDoors.id("garment_of_reality_helmet"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.GARMENT_OF_REALITY_ARMOR.chestplate())
                .pattern("XOX")
                .pattern("XXX")
                .pattern("XXX")
                .define('X', ModItems.STABLE_FABRIC)
                .define('O', ModItems.INFRANGIBLE_FIBER)
                .unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER))
                .save(exporter, DimensionalDoors.id("garment_of_reality_chestplate"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.GARMENT_OF_REALITY_ARMOR.leggings())
                .pattern("XXX")
                .pattern("XOX")
                .pattern("X X")
                .define('X', ModItems.STABLE_FABRIC)
                .define('O', ModItems.INFRANGIBLE_FIBER)
                .unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER))
                .save(exporter, DimensionalDoors.id("garment_of_reality_leggings"));
        ShapedTesselatingRecipeJsonBuilder.shaped(ModItems.GARMENT_OF_REALITY_ARMOR.boots())
                .pattern("X X")
                .pattern("XOX")
                .define('X', ModItems.STABLE_FABRIC)
                .define('O', ModItems.INFRANGIBLE_FIBER)
                .unlockedBy("infrangible_fiber", TriggerInstance.hasItems(ModItems.INFRANGIBLE_FIBER))
                .save(exporter, DimensionalDoors.id("garment_of_reality_boots"));

    }
}

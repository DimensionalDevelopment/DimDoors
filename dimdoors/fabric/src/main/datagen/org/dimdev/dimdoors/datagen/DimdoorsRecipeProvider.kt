package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.DyeItem
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.block.ModBlocks.fabric
import org.dimdev.dimdoors.datagen.TesselatingRecipeProvider.generate
import org.dimdev.dimdoors.datagen.TesselatingRecipeProvider.hasItems
import org.dimdev.dimdoors.item.ModItems
import org.dimdev.dimdoors.item.door.DimensionalDoorItemRegistrar.PREFIX
import org.dimdev.dimdoors.tag.ModItemTags
import java.util.concurrent.CompletableFuture

class DimdoorsRecipeProvider(dataGenerator: PackOutput, completableFuture: CompletableFuture<HolderLookup.Provider?>?) :
    RecipeProvider(dataGenerator, completableFuture) {
    override fun buildRecipes(exporter: RecipeOutput) {
        //TODO: Find out proper RecipeCategory for these? I just random added this to make it work.

        doorBuilder(ModBlocks.STONE_DOOR, Ingredient.of(ConventionalItemTags.STONES)).unlockedBy("inventory_changed", has(ConventionalItemTags.STONES)).save(exporter)
        doorBuilder(ModBlocks.GOLD_DOOR, Ingredient.of(ConventionalItemTags.GOLD_INGOTS)).unlockedBy("inventory_changed", has(ConventionalItemTags.GOLD_INGOTS)).save(exporter)
        doorBuilder(ModBlocks.QUARTZ_DOOR, Ingredient.of(ConventionalItemTags.QUARTZ_GEMS)).unlockedBy("inventory_changed", has(ConventionalItemTags.QUARTZ_GEMS)).save(exporter)

        threeByThreePacker(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CLOD_BLOCK, ModItems.CLOD)
        threeByThreePacker(
            exporter,
            RecipeCategory.BUILDING_BLOCKS,
            ModBlocks.AMALGAM_BLOCK,
            ModItems.AMALGAM_LUMP
        )

        dimDoorRecipe(Blocks.OAK_DOOR, exporter)
        dimDoorRecipe(Blocks.IRON_DOOR, exporter)
        dimDoorRecipe(ModBlocks.GOLD_DOOR, exporter)
        dimDoorRecipe(ModBlocks.QUARTZ_DOOR, exporter)


        var ingredient: Ingredient = ModItems.AMALGAM_LUMP.ingredient()
        var trigger = ModItems.AMALGAM_LUMP.hasItems()
        doorBuilder(ModBlocks.AMALGAM_DOOR, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        trapdoorBuilder(ModBlocks.AMALGAM_TRAPDOOR, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.AMALGAM_SLAB, ingredient)
            .unlockedBy("inventory_changed", trigger).save(exporter)
        stairBuilder(ModBlocks.AMALGAM_STAIRS, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)

        ingredient = ModBlocks.DRIFTWOOD_PLANKS.ingredient()
        trigger = ModBlocks.DRIFTWOOD_PLANKS.hasItems()

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIFTWOOD_WOOD, 3)
            .define('#', ModBlocks.DRIFTWOOD_LOG)
            .pattern("##")
            .pattern("##")
            .unlockedBy("inventory_changed", ModBlocks.DRIFTWOOD_LOG.hasItems())
            .save(exporter)

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIFTWOOD_PLANKS, 4)
            .requires(ModItemTags.DRIFTWOOD_LOGS)
            .unlockedBy("inventory_changed", has(ModItemTags.DRIFTWOOD_LOGS))
            .save(exporter)

        fenceBuilder(ModBlocks.DRIFTWOOD_FENCE, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        fenceGateBuilder(ModBlocks.DRIFTWOOD_GATE, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        buttonBuilder(ModBlocks.DRIFTWOOD_BUTTON, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.DRIFTWOOD_SLAB, ingredient)
            .unlockedBy("inventory_changed", trigger).save(exporter)
        stairBuilder(ModBlocks.DRIFTWOOD_STAIRS, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        doorBuilder(ModBlocks.DRIFTWOOD_DOOR, ingredient).unlockedBy("inventory_changed", trigger)
            .save(exporter)
        trapdoorBuilder(ModBlocks.DRIFTWOOD_TRAPDOOR, ingredient)
            .unlockedBy("inventory_changed", trigger).save(exporter)

        shaped(RecipeCategory.MISC, ModItems.RIFT_REMOVER)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .define('#', ConventionalItemTags.GOLD_INGOTS)
            .define('X', ConventionalItemTags.ENDER_PEARLS)
            .unlockedBy("inventory_changed", ModItems.RIFT_BLADE.hasItems())
            .save(exporter, DimensionalDoors.id("rift_remover"))
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_SIGNATURE)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .define('#', ConventionalItemTags.IRON_INGOTS)
            .define('X', ConventionalItemTags.ENDER_PEARLS)
            .unlockedBy("inventory_changed", ModItems.RIFT_BLADE.hasItems())
            .save(exporter, DimensionalDoors.id("rift_signature"))
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.RIFT_STABILIZER)
            .pattern(" # ")
            .pattern("#X#")
            .pattern(" # ")
            .define('#', ConventionalItemTags.DIAMOND_GEMS)
            .define('X', ConventionalItemTags.ENDER_PEARLS)
            .unlockedBy("inventory_changed", ModItems.RIFT_BLADE.hasItems())
            .save(exporter, DimensionalDoors.id("rift_stabilizer"))
        shaped(RecipeCategory.MISC, ModItems.STABILIZED_RIFT_SIGNATURE)
            .pattern("# #")
            .pattern(" X ")
            .pattern("# #")
            .define('#', ConventionalItemTags.ENDER_PEARLS)
            .define('X', ModItems.RIFT_SIGNATURE)
            .unlockedBy("inventory_changed", ModItems.RIFT_SIGNATURE.hasItems())
            .save(exporter, DimensionalDoors.id("stabilized_rift_signature"))

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TESSELATING_LOOM)
            .pattern("XOX")
            .pattern("ALA")
            .pattern("XAX")
            .define('A', ModItems.WORLD_THREAD)
            .define('L', Blocks.LOOM)
            .define('X', Blocks.SCAFFOLDING)
            .define('O', DyeColor.BLACK.fabric())
            .unlockedBy("inventory_changed", InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.LOOM))
            .save(exporter, DimensionalDoors.id("tesselating_loom"))

        ColoredFabricRecipeProvider.generate(exporter)
        generate(exporter)



        blockSetRecipes(ModBlocks.GRAVEL_SET, Blocks.GRAVEL, exporter)
        blockSetRecipes(ModBlocks.DARK_SAND_SET, ModBlocks.DARK_SAND, exporter)
        blockSetRecipes(ModBlocks.CLAY_SET, Blocks.CLAY, exporter)
        blockSetRecipes(ModBlocks.TERRACOTTA_SET, Blocks.TERRACOTTA, exporter)
        //        blockSetRecipes(ModBlocks.WHITE_TERRACOTTA_SET, Blocks.WHITE_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.WHITE_GLAZED_TERRACOTTA_SET, Blocks.WHITE_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.ORANGE_TERRACOTTA_SET, Blocks.ORANGE_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET, Blocks.ORANGE_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.MAGENTA_TERRACOTTA_SET, Blocks.MAGENTA_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET, Blocks.MAGENTA_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIGHT_BLUE_TERRACOTTA_SET, Blocks.LIGHT_BLUE_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.YELLOW_TERRACOTTA_SET, Blocks.YELLOW_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET, Blocks.YELLOW_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIME_TERRACOTTA_SET, Blocks.LIME_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIME_GLAZED_TERRACOTTA_SET, Blocks.LIME_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.PINK_TERRACOTTA_SET, Blocks.PINK_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.PINK_GLAZED_TERRACOTTA_SET, Blocks.PINK_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.GRAY_TERRACOTTA_SET, Blocks.GRAY_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.GRAY_GLAZED_TERRACOTTA_SET, Blocks.GRAY_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIGHT_GRAY_TERRACOTTA_SET, Blocks.LIGHT_GRAY_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.CYAN_TERRACOTTA_SET, Blocks.CYAN_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.CYAN_GLAZED_TERRACOTTA_SET, Blocks.CYAN_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.PURPLE_TERRACOTTA_SET, Blocks.PURPLE_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET, Blocks.PURPLE_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BLUE_TERRACOTTA_SET, Blocks.BLUE_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BLUE_GLAZED_TERRACOTTA_SET, Blocks.BLUE_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BROWN_TERRACOTTA_SET, Blocks.BROWN_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BROWN_GLAZED_TERRACOTTA_SET, Blocks.BROWN_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.GREEN_TERRACOTTA_SET, Blocks.GREEN_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.GREEN_GLAZED_TERRACOTTA_SET, Blocks.GREEN_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.RED_TERRACOTTA_SET, Blocks.RED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.RED_GLAZED_TERRACOTTA_SET, Blocks.RED_GLAZED_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BLACK_TERRACOTTA_SET, Blocks.BLACK_TERRACOTTA, exporter);
//        blockSetRecipes(ModBlocks.BLACK_GLAZED_TERRACOTTA_SET, Blocks.BLACK_GLAZED_TERRACOTTA, exporter);
        blockSetRecipes(ModBlocks.MUD_SET, Blocks.MUD, exporter)
        blockSetRecipes(ModBlocks.UNRAVELED_SET, ModBlocks.UNRAVELLED_FABRIC, exporter)
        blockSetRecipes(ModBlocks.DEEPSLATE_SET, Blocks.DEEPSLATE, exporter)
        blockSetRecipes(ModBlocks.RED_SAND_SET, Blocks.RED_SAND, exporter)
        blockSetRecipes(ModBlocks.SAND_SET, Blocks.SAND, exporter)
        blockSetRecipes(ModBlocks.END_STONE_SET, Blocks.END_STONE, exporter)
        blockSetRecipes(ModBlocks.NETHERRACK_SET, Blocks.NETHERRACK, exporter)

        decaySmeltSet(ModBlocks.CLAY_SET, ModBlocks.TERRACOTTA_SET, exporter)
        terraCottaRecipes(
            ModBlocks.WHITE_TERRACOTTA_SET,
            Blocks.WHITE_TERRACOTTA,
            ModBlocks.WHITE_GLAZED_TERRACOTTA_SET,
            Blocks.WHITE_GLAZED_TERRACOTTA,
            DyeColor.WHITE,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.ORANGE_TERRACOTTA_SET,
            Blocks.ORANGE_TERRACOTTA,
            ModBlocks.ORANGE_GLAZED_TERRACOTTA_SET,
            Blocks.ORANGE_GLAZED_TERRACOTTA,
            DyeColor.ORANGE,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.MAGENTA_TERRACOTTA_SET,
            Blocks.MAGENTA_TERRACOTTA,
            ModBlocks.MAGENTA_GLAZED_TERRACOTTA_SET,
            Blocks.MAGENTA_GLAZED_TERRACOTTA,
            DyeColor.MAGENTA,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.LIGHT_BLUE_TERRACOTTA_SET,
            Blocks.LIGHT_BLUE_TERRACOTTA,
            ModBlocks.LIGHT_BLUE_GLAZED_TERRACOTTA_SET,
            Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
            DyeColor.LIGHT_BLUE,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.YELLOW_TERRACOTTA_SET,
            Blocks.YELLOW_TERRACOTTA,
            ModBlocks.YELLOW_GLAZED_TERRACOTTA_SET,
            Blocks.YELLOW_GLAZED_TERRACOTTA,
            DyeColor.YELLOW,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.LIME_TERRACOTTA_SET,
            Blocks.LIME_TERRACOTTA,
            ModBlocks.LIME_GLAZED_TERRACOTTA_SET,
            Blocks.LIME_GLAZED_TERRACOTTA,
            DyeColor.LIME,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.PINK_TERRACOTTA_SET,
            Blocks.PINK_TERRACOTTA,
            ModBlocks.PINK_GLAZED_TERRACOTTA_SET,
            Blocks.PINK_GLAZED_TERRACOTTA,
            DyeColor.PINK,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.GRAY_TERRACOTTA_SET,
            Blocks.GRAY_TERRACOTTA,
            ModBlocks.GRAY_GLAZED_TERRACOTTA_SET,
            Blocks.GRAY_GLAZED_TERRACOTTA,
            DyeColor.GRAY,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.LIGHT_GRAY_TERRACOTTA_SET,
            Blocks.LIGHT_GRAY_TERRACOTTA,
            ModBlocks.LIGHT_GRAY_GLAZED_TERRACOTTA_SET,
            Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA,
            DyeColor.LIGHT_GRAY,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.CYAN_TERRACOTTA_SET,
            Blocks.CYAN_TERRACOTTA,
            ModBlocks.CYAN_GLAZED_TERRACOTTA_SET,
            Blocks.CYAN_GLAZED_TERRACOTTA,
            DyeColor.CYAN,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.PURPLE_TERRACOTTA_SET,
            Blocks.PURPLE_TERRACOTTA,
            ModBlocks.PURPLE_GLAZED_TERRACOTTA_SET,
            Blocks.PURPLE_GLAZED_TERRACOTTA,
            DyeColor.PURPLE,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.BLUE_TERRACOTTA_SET,
            Blocks.BLUE_TERRACOTTA,
            ModBlocks.BLUE_GLAZED_TERRACOTTA_SET,
            Blocks.BLUE_GLAZED_TERRACOTTA,
            DyeColor.BLUE,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.BROWN_TERRACOTTA_SET,
            Blocks.BROWN_TERRACOTTA,
            ModBlocks.BROWN_GLAZED_TERRACOTTA_SET,
            Blocks.BROWN_GLAZED_TERRACOTTA,
            DyeColor.BROWN,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.GREEN_TERRACOTTA_SET,
            Blocks.GREEN_TERRACOTTA,
            ModBlocks.GREEN_GLAZED_TERRACOTTA_SET,
            Blocks.GREEN_GLAZED_TERRACOTTA,
            DyeColor.GREEN,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.RED_TERRACOTTA_SET,
            Blocks.RED_TERRACOTTA,
            ModBlocks.RED_GLAZED_TERRACOTTA_SET,
            Blocks.RED_GLAZED_TERRACOTTA,
            DyeColor.RED,
            exporter
        )
        terraCottaRecipes(
            ModBlocks.BLACK_TERRACOTTA_SET,
            Blocks.BLACK_TERRACOTTA,
            ModBlocks.BLACK_GLAZED_TERRACOTTA_SET,
            Blocks.BLACK_GLAZED_TERRACOTTA,
            DyeColor.BLACK,
            exporter
        )
    }

    private fun shaped(
        misc: RecipeCategory,
        item: ItemLike
    ) = ShapedRecipeBuilder.shaped(misc, item)

    private fun dimDoorRecipe(block: Block, exporter: RecipeOutput) {
        val id = block.builtInRegistryHolder().key().location()
        val door = BuiltInRegistries.ITEM.get(DimensionalDoors.id("$PREFIX${id.namespace}_${id.path}"))

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, door)
            .group("dimensional_doors")
            .unlockedBy("inventory_changed", block.hasItems())
            .pattern("XOX")
            .define('X', block)
            .define('O', ConventionalItemTags.ENDER_PEARLS)
            .save(exporter)
    }

    private fun terraCottaRecipes(
        baseSet: ModBlocks.DecayGroupSet,
        baseInput: ItemLike,
        glazedSet: ModBlocks.DecayGroupSet,
        glazedInput: ItemLike,
        color: DyeColor,
        exporter: RecipeOutput
    ) {
        blockSetRecipes(baseSet, baseInput, exporter)
        blockSetRecipes(glazedSet, glazedInput, exporter)
        decaySmeltSet(baseSet, glazedSet, exporter)

        dyedRecipe(ModBlocks.TERRACOTTA_SET.fence, baseSet.fence, color, exporter)
        dyedRecipe(ModBlocks.TERRACOTTA_SET.gate, baseSet.gate, color, exporter)
        dyedRecipe(ModBlocks.TERRACOTTA_SET.button, baseSet.button, color, exporter)
        dyedRecipe(ModBlocks.TERRACOTTA_SET.slab, baseSet.slab, color, exporter)
        dyedRecipe(ModBlocks.TERRACOTTA_SET.stairs, baseSet.stairs, color, exporter)
        dyedRecipe(ModBlocks.TERRACOTTA_SET.wall, baseSet.wall, color, exporter)
    }

    private fun dyedRecipe(base: Block, glazed: Block, color: DyeColor, exporter: RecipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, glazed)
            .group("stained_terracotta")
            .unlockedBy("inventory_changed", base.hasItems())
            .requires(base)
            .requires(DyeItem.byColor(color))
            .save(exporter, BuiltInRegistries.BLOCK.getKey(glazed).withSuffix("_dyed"))
    }

    private fun blockSetRecipes(set: ModBlocks.DecayGroupSet, craftingInput: ItemLike, exporter: RecipeOutput) {
        val craftingTrigger = InventoryChangeTrigger.TriggerInstance.hasItems(craftingInput)

        val ingredient = Ingredient.of(craftingInput)

        fenceBuilder(set.fence, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)
        fenceGateBuilder(set.gate, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)
        buttonBuilder(set.button, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, set.slab, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)
        stairBuilder(set.stairs, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)
        wallBuilder(RecipeCategory.BUILDING_BLOCKS, set.wall, ingredient).unlockedBy("inventory_changed", craftingTrigger).save(exporter)

        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.fence)
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.gate)
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.button)
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.slab)
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.stairs)
        stonecutterResultFromBase(exporter, RecipeCategory.BUILDING_BLOCKS, craftingInput, set.wall)
    }

    private fun decaySmeltSet(from: ModBlocks.DecayGroupSet, to: ModBlocks.DecayGroupSet, exporter: RecipeOutput) {
        smelting(
            from.fence.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.fence,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.fence.hasItems())
            .save(exporter, BuiltInRegistries.BLOCK.getKey(from.fence).withSuffix("_smelting"))
        smelting(
            from.gate.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.gate,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.gate.hasItems())
            .save(exporter, BuiltInRegistries.BLOCK.getKey(from.gate).withSuffix("_smelting"))
        smelting(
            from.button.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.button,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.button.hasItems()).save(exporter, BuiltInRegistries.BLOCK.getKey(from.button).withSuffix("_smelting"))
        smelting(
            from.slab.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.slab,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.slab.hasItems()).save(exporter, BuiltInRegistries.BLOCK.getKey(from.slab).withSuffix("_smelting"))
        smelting(
            from.stairs.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.stairs,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.stairs.hasItems())
            .save(exporter, BuiltInRegistries.BLOCK.getKey(from.stairs).withSuffix("_smelting"))
        smelting(
            from.wall.ingredient(),
            RecipeCategory.BUILDING_BLOCKS,
            to.wall,
            0.0f,
            200
        ).unlockedBy("inventory_changed", from.wall.hasItems())
            .save(exporter, BuiltInRegistries.BLOCK.getKey(from.wall).withSuffix("_smelting"))
    }

    private fun smelting(
        ingredient: Ingredient,
        buildingBlocks: RecipeCategory,
        result: ItemLike,
        f: Float,
        i: Int
    ) = SimpleCookingRecipeBuilder.smelting(ingredient, buildingBlocks, result, f, i)

    private fun ItemLike.ingredient(): Ingredient = Ingredient.of(this)

}

fun Holder<*>.key() = this.unwrapKey().get()




package org.dimdev.dimdoors.datagen

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.Holder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import org.dimdev.dimdoors.block.ModBlocks
import org.dimdev.dimdoors.item.ModItems

private val Holder.Reference<*>.path: String get() = key().location().path

object TesselatingRecipeProvider {
    lateinit var exporter: RecipeOutput

    fun generate(exporter: RecipeOutput) {
        this.exporter = exporter

        shapeless(ModItems.RIFT_BLADE) {
            requires(Items.IRON_SWORD)
            requires(Items.ENDER_PEARL, 2)
            unlockedByItem(Items.IRON_SWORD)
        }

        shaped(ModItems.STABLE_FABRIC) {
            pattern("XX")
            pattern("XX")
            define('X', ModItems.WORLD_THREAD)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.LIMINAL_LINT) {
            pattern("XX")
            pattern("XX")
            define('X', ModItems.FRAYED_FILAMENT)
            unlockedByItem(ModItems.FRAYED_FILAMENT)
        }

        shaped(ModBlocks.LIMINAL_TRANSMITTER) {
            pattern(" L ")
            pattern("LSL")
            pattern(" R ")
            define('L', ModItems.LIMINAL_LINT)
            define('S', Items.SCULK_SENSOR)
            define('R', Items.REPEATER)
            unlockedByItem(ModItems.LIMINAL_LINT)
        }

        shaped(ModItems.ENDURING_FIBERS) {
            pattern("XX")
            pattern("XX")
            define('X', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.INFRANGIBLE_FIBER)
        }

        shaped(ModItems.RIFT_PEARL) {
            pattern("XO")
            define('X', ModItems.STABLE_FABRIC)
            define('O', Items.ENDER_PEARL)
            unlockedByItem(ModItems.STABLE_FABRIC)
        }

        shaped(ModBlocks.BLACK_FABRIC, "fabric_of_reality") {
            pattern("XX")
            pattern("XO").define('O', ModItems.STABLE_FABRIC).define('X', ModItems.WORLD_THREAD)
            unlockedBy("stable_fabric", ModItems.STABLE_FABRIC.hasItems())
        }

        shaped(ModItems.FUZZY_FIREBALL) {
            pattern("XOX")
            define('X', ModItems.LIMINAL_LINT)
            define('O', Items.FIRE_CHARGE)
            unlockedByItem(ModItems.LIMINAL_LINT)
        }

        shaped(ModItems.FABRIC_OF_FINALITY) {
            pattern("XOX").define('X', ModItems.ENDURING_FIBERS)
            define('O', Items.DRAGON_BREATH)
            unlockedByItem(ModItems.ENDURING_FIBERS)
        }

        shaped(ModBlocks.REALITY_SPONGE) {
            pattern("XOX").pattern("OXO")
            pattern("XOX").define('X', ModItems.STABLE_FABRIC).define('O', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.LIMINAL_LINT)
        }

        shaped(ModItems.WORLD_THREAD_ARMOR.helmet) {
            pattern("XXX").pattern("X X")
            define('X', ModItems.WORLD_THREAD)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.WORLD_THREAD_ARMOR.chestplate) {
            pattern("X X")
            pattern("XXX")
            pattern("XXX")
            define('X', ModItems.WORLD_THREAD)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.WORLD_THREAD_ARMOR.leggings) {
            pattern("XXX")
            pattern("X X")
            pattern("X X")
            define('X', ModItems.WORLD_THREAD)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.WORLD_THREAD_ARMOR.boots) {
            pattern("X X")
            pattern("X X")
            define('X', ModItems.WORLD_THREAD)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.GARMENT_OF_REALITY_ARMOR.helmet) {
            pattern("XXX")
            pattern("XOX")
            define('X', ModItems.STABLE_FABRIC)
            define('O', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.INFRANGIBLE_FIBER)
        }

        shaped(ModItems.GARMENT_OF_REALITY_ARMOR.chestplate) {
            pattern("XOX")
            pattern("XXX")
            pattern("XXX")
            define('X', ModItems.STABLE_FABRIC)
            define('O', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.INFRANGIBLE_FIBER)
        }

        shaped(ModItems.GARMENT_OF_REALITY_ARMOR.leggings) {
            pattern("XXX")
            pattern("XOX")
            pattern("X X")
            define('X', ModItems.STABLE_FABRIC)
            define('O', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.INFRANGIBLE_FIBER)
        }

        shaped(ModItems.GARMENT_OF_REALITY_ARMOR.boots) {
            pattern("X X")
            pattern("XOX")
            define('X', ModItems.STABLE_FABRIC)
            define('O', ModItems.INFRANGIBLE_FIBER)
            unlockedByItem(ModItems.INFRANGIBLE_FIBER)
        }

        shaped(ModItems.FARSHOT) {
            pattern("XO ")
            pattern("X O")
            pattern("XO ")
            define('X', ModItems.WORLD_THREAD)
            define('O', ModBlocks.DRIFTWOOD_PLANKS)
            unlockedByItem(ModItems.WORLD_THREAD)
        }

        shaped(ModItems.BINDING_PEARL) {
            pattern("X X")
            pattern(" O ")
            pattern("X X")
            define('X', ModItems.MASK_SHARD)
            define('O', Items.ENDER_EYE)
            unlockedByItem(ModItems.MASK_SHARD)
        }

        shaped(ModItems.PORTABLE_RIFT) {
            pattern(" P ")
            pattern("PBP")
            pattern(" S ")
            define('P', ConventionalItemTags.GLASS_PANES)
            define('B', ModItems.BINDING_PEARL)
            define('S', Items.SHULKER_SHELL)
            unlockedByItem(ModItems.BINDING_PEARL)
        }
    }

    fun shapeless(
        item: ItemLike,
        name: String = item.asItem().builtInRegistryHolder().path,
        block: ShapelessTesselatingRecipeBuilder.() -> Unit
    ) {
        val builder = ShapelessTesselatingRecipeBuilder.shapeless(item)
        block(builder)
        builder.save(exporter, name)
    }

    fun shaped(
        item: ItemLike,
        count: Int = 1,
        name: String = item.asItem().builtInRegistryHolder().path,
        block: ShapedTesselatingRecipeJsonBuilder.() -> Unit
    ) {
        val builder = ShapedTesselatingRecipeJsonBuilder.shaped(item, count)
        block(builder)
        builder.save(exporter, name)
    }

    fun shaped(
        item: ItemLike,
        name: String = item.asItem().builtInRegistryHolder().path,
        block: ShapedTesselatingRecipeJsonBuilder.() -> Unit
    ) = shaped(item, 1, name, block)

}


fun TesselatingRecipeBuilder<*, *>.unlockedByItem(item: Item) {
    unlockedBy(item.builtInRegistryHolder().path, item.hasItems())
}

fun ItemLike.hasItems(): Criterion<InventoryChangeTrigger.TriggerInstance> = InventoryChangeTrigger.TriggerInstance.hasItems(this)

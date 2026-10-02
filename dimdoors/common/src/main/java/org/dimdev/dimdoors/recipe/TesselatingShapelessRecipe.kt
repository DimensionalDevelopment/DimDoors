package org.dimdev.dimdoors.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

class TesselatingShapelessRecipe(
    private val group: String,
    val result: ItemStack,
    private val ingredients: NonNullList<Ingredient>,
    override val weavingTime: Int
) : TesselatingRecipe {
    override fun getSerializer(): RecipeSerializer<*> {
        return ModRecipeSerializers.SHAPELESS_TESSELATING
    }

    override fun getGroup() = this.group

    override fun getResultItem(provider: HolderLookup.Provider) = this.result

    override fun getIngredients() = this.ingredients

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    override fun matches(craftingInput: CraftingInput, level: Level) =
        if (craftingInput.ingredientCount() != this.ingredients.size) {
        false
    } else {
        if (craftingInput.size() == 1 && this.ingredients.size == 1)
            this.ingredients.first().test(craftingInput.getItem(0))
        else
            craftingInput.stackedContents().canCraft(this, null)
    }

    override fun assemble(container: CraftingInput, registryAccess: HolderLookup.Provider) = this.result.copy()

    override fun canCraftInDimensions(width: Int, height: Int) = width * height >= this.ingredients.size

    class Serializer : RecipeSerializer<TesselatingShapelessRecipe> {
        override fun codec() = CODEC

        override fun streamCodec() = STREAM_CODEC

        companion object {
            private val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(TesselatingShapelessRecipe::group),
                    ItemStack.STRICT_CODEC.fieldOf("result").forGetter(TesselatingShapelessRecipe::result),
                    Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients")
                        .flatXmap({ list ->
                            val ingredients =
                                list.filter { ingredient -> !ingredient.isEmpty }.toTypedArray()
                            if (ingredients.isEmpty())
                                DataResult.error { "No ingredients for shapeless recipe" }
                            else if (ingredients.size > 9) DataResult.error { "Too many ingredients for shapeless recipe" } else
                                DataResult.success(NonNullList.of(Ingredient.EMPTY, *ingredients))
                        }, { result -> DataResult.success(result) })
                        .forGetter(TesselatingShapelessRecipe::ingredients),
                    ExtraCodecs.NON_NEGATIVE_INT.fieldOf("weaving_time").forGetter(TesselatingShapelessRecipe::weavingTime)
                ).apply(instance, ::TesselatingShapelessRecipe)
            }

            val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, TesselatingShapelessRecipe::group,
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), TesselatingShapelessRecipe::ingredients,
                ItemStack.STREAM_CODEC, TesselatingShapelessRecipe::result,
                ByteBufCodecs.INT, TesselatingShapelessRecipe::weavingTime
            ) { group, ingredients, result, weavingTime ->
                TesselatingShapelessRecipe(
                    group,
                    result,
                    NonNullList.of(Ingredient.EMPTY, *ingredients.toTypedArray()),
                    weavingTime
                )
            }
        }
    }
}


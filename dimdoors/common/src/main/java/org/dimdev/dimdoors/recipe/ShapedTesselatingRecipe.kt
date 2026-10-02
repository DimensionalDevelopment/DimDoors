package org.dimdev.dimdoors.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.HolderLookup
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.Level

class ShapedTesselatingRecipe(
    private val group: String,
    val pattern: ShapedRecipePattern,
    val result: ItemStack,
    override val weavingTime: Int,
    val showNotification: Boolean
) : TesselatingRecipe {
    override fun getSerializer() = ModRecipeSerializers.SHAPED_TESSELATING

    override fun getGroup() = this.group

    override fun getResultItem(provider: HolderLookup.Provider) = this.result

    override fun getIngredients() = this.pattern.ingredients()

    override fun showNotification(): Boolean = this.showNotification

    override fun canCraftInDimensions(width: Int, height: Int) = width >= this.pattern.width() && height >= this.pattern.height()

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    override fun matches(inv: CraftingInput, level: Level): Boolean = this.pattern.matches(inv)

    override fun assemble(container: CraftingInput, provider: HolderLookup.Provider) = this.getResultItem(provider).copy()

    val width: Int get() = this.pattern.width()

    val height: Int get() = this.pattern.height()

    override fun isIncomplete(): Boolean {
        val nonNullList = this.ingredients
        return nonNullList.isEmpty() || nonNullList
            .filter { ingredient -> !ingredient.isEmpty }
            .any { ingredient -> ingredient.items.size == 0 }
    }

    class Serializer : RecipeSerializer<ShapedTesselatingRecipe> {
        override fun codec() = CODEC

        override fun streamCodec() = STREAM_CODEC

        companion object {
            val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedTesselatingRecipe::group),
                    ShapedRecipePattern.MAP_CODEC.forGetter(ShapedTesselatingRecipe::pattern),
                    ItemStack.CODEC.fieldOf("result").forGetter(ShapedTesselatingRecipe::result),
                    Codec.INT.optionalFieldOf("weaving_time", 200).forGetter(ShapedTesselatingRecipe::weavingTime),
                    Codec.BOOL.optionalFieldOf("show_notification", true).forGetter { it.showNotification },
                ).apply(instance, ::ShapedTesselatingRecipe)
            }

            val STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedTesselatingRecipe::group,
                ShapedRecipePattern.STREAM_CODEC, ShapedTesselatingRecipe::pattern,
                ItemStack.STREAM_CODEC, ShapedTesselatingRecipe::result,
                ByteBufCodecs.INT, TesselatingRecipe::weavingTime,
                ByteBufCodecs.BOOL, { it.showNotification },
                ::ShapedTesselatingRecipe

            )
        }
    }
}


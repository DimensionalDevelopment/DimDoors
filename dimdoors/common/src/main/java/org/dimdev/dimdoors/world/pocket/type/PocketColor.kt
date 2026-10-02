package org.dimdev.dimdoors.world.pocket.type

import com.mojang.serialization.Codec
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import org.dimdev.dimdoors.tag.ModItemTags

enum class PocketColor(private val id: String, val color: DyeColor?) : StringRepresentable {
    WHITE("white", DyeColor.WHITE),
    ORANGE("orange", DyeColor.ORANGE),
    MAGENTA("magenta", DyeColor.MAGENTA),
    LIGHT_BLUE("light_blue", DyeColor.LIGHT_BLUE),
    YELLOW("yellow", DyeColor.YELLOW),
    LIME("lime", DyeColor.LIME),
    PINK("pink", DyeColor.PINK),
    GRAY("gray", DyeColor.GRAY),
    LIGHT_GRAY("light_gray", DyeColor.LIGHT_GRAY),
    CYAN("cyan", DyeColor.CYAN),
    PURPLE("purple", DyeColor.PURPLE),
    BLUE("blue", DyeColor.BLUE),
    BROWN("brown", DyeColor.BROWN),
    GREEN("green", DyeColor.GREEN),
    RED("red", DyeColor.RED),
    BLACK("black", DyeColor.BLACK),
    NONE("none", null);

    override fun getSerializedName() = id

    companion object {
        val CODEC: Codec<PocketColor> = StringRepresentable.fromValues(entries::toTypedArray)
        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, PocketColor> = ByteBufCodecs.idMapper(entries::get, PocketColor::ordinal).cast()

        fun from(stack: ItemStack): DyeColor? {
            for (dyColor in DyeColor.entries) {
                val tag = ModItemTags.DYES.getOrNull(dyColor.ordinal)
                if (tag != null && stack.`is`(tag)) return dyColor
            }

            return null
        }

        fun from(color: DyeColor?) = entries.firstOrNull { it.color === color } ?: NONE

    }
}

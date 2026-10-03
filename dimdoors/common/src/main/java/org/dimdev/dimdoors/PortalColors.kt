package org.dimdev.dimdoors

import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.util.GsonHelper
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.util.function.StreamUtils
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.util.Utils
import java.io.IOException
import java.util.*
import java.util.stream.IntStream
import java.util.stream.Stream

object PortalColors {
    val STRING_INT_CODEC: Codec<Int> = Codec.STRING.xmap(Integer::decode, Integer::toHexString)

    val INTEGER: Codec<Int> = Codec.withAlternative(STRING_INT_CODEC, Codec.INT)

    var COLORS_CODEC: Codec<IntArray> = INTEGER
        .listOf(16, 16)
        .xmap(MutableList<Int>::stream, Stream<Int>::toList)
        .xmap(StreamUtils::toIntStream, IntStream::boxed)
        .xmap(IntStream::toArray, Arrays::stream)

    val DYE_COLORS_CODEC: MapCodec<MutableMap<DyeColor, IntArray>> = DyeColor.CODEC.unboundedMap(COLORS_CODEC).fieldOf("dyes")

    val LEVEL_COLORS_CODEC: MapCodec<MutableMap<ResourceKey<Level>, IntArray>> = Level.RESOURCE_KEY_CODEC.unboundedMap(COLORS_CODEC).fieldOf("levels")
    val BASE_COLOR_CODEC: MapCodec<IntArray> = COLORS_CODEC.fieldOf("base_color")

    private var baseColor = intArrayOf(
        0X05191C, 0X031816, 0X071919, 0X0B1C1D,
        0X101E18, 0X10161F, 0X151C2A, 0X182717,
        0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
        0X322436, 0X0C5052, 0X34634D, 0X1450A8
    )

    private var dyes = mutableMapOf<DyeColor, IntArray>()
    private var levels = mutableMapOf<ResourceKey<Level>, IntArray>()

    private val PORTAL_COLORS = DimensionalDoors.id("portal_colors.json")

    fun dye(color: DyeColor?) = dyes[color]

    fun levels(level: ResourceKey<Level>) = levels[level]

    fun base(): IntArray = baseColor

    fun load(manager: ResourceManager) {
        val list = manager.getResourceStack(PORTAL_COLORS)

        var baseColor: IntArray? = null

        var baseColorLoaded = false
        val dyes = mutableMapOf<DyeColor, IntArray>()
        val levels = mutableMapOf<ResourceKey<Level>, IntArray>()

        for (i in list.indices.reversed()) {
            val resource = list[i]
            try {
                resource.openAsReader().use { reader ->
                    val json = GsonHelper.parse(reader)
                    val baseColorOptional = BASE_COLOR_CODEC.compressedDecode(JsonOps.INSTANCE, json).result()

                    if (!baseColorLoaded && baseColorOptional.isPresent) {
                        baseColor = baseColorOptional.get()
                        baseColorLoaded = true
                    }

                    DYE_COLORS_CODEC.compressedDecode(JsonOps.INSTANCE, json).result().ifPresent { dyeColorMap -> Utils.mergeMaps(dyes, dyeColorMap) }
                    LEVEL_COLORS_CODEC.compressedDecode(JsonOps.INSTANCE, json).result().ifPresent { levelMap -> Utils.mergeMaps(levels, levelMap) }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        if (!baseColorLoaded) baseColor = intArrayOf(
            0X05191C, 0X031816, 0X071919, 0X0B1C1D,
            0X101E18, 0X10161F, 0X151C2A, 0X182717,
            0X1B2131, 0X181C2F, 0X222325, 0X113E3C,
            0X322436, 0X0C5052, 0X34634D, 0X1450A8
        )

        PortalColors.baseColor = baseColor!!
        PortalColors.dyes = dyes
        PortalColors.levels = levels
    }
}
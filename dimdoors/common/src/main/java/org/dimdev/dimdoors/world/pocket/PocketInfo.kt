package org.dimdev.dimdoors.world.pocket

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.util.CodecUtils
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Supplier

data class PocketInfo(val world: ResourceKey<Level>, val id: Int) {
    override fun toString() = "${world.location()}#$id"

    companion object {
        @JvmField
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(PocketInfo::world),
                Codec.INT.fieldOf("id").forGetter(PocketInfo::id)
            ).apply(instance, ::PocketInfo)
        }

        val STRING_CODEC = Codec.STRING.comapFlatMap(::fromString, PocketInfo::toString)

        fun fromString(value: String): DataResult<PocketInfo> {
            val strings = value.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            return when {
                strings.size != 2 -> {
                    DataResult.error { "Value doesn't have # seperator." }
                }

                else -> {
                    ResourceLocation.read(strings[0]).map { loc ->
                        ResourceKey.create(Registries.DIMENSION, loc)

                    }.flatMap { world -> CodecUtils.parseIntString(strings[1]).map { id -> PocketInfo(world, id) } }
                }
            }
        }
    }
}

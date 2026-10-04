package org.dimdev.dimdoors.util

import com.mojang.datafixers.Products.P2
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.Music
import net.minecraft.sounds.SoundEvent
import net.minecraft.tags.TagKey
import org.dimdev.dimdoors.api.util.unboundedMap
import org.dimdev.dimdoors.world.decay.conditions.GenericDecayCondition
import java.util.*
import java.util.function.Function
import kotlin.jvm.optionals.getOrNull

object CodecUtils {

    public fun createMusic(sound: Holder<SoundEvent>): Music {
        return Music(sound, 0, 0, true)
    }

    @JvmField
    val GAME_MUSIC: Codec<Music> = Codec.withAlternative(Music.CODEC, SoundEvent.CODEC, ::createMusic)

    @JvmField
    val STRING_INT = Codec.STRING.xmap( { s -> s.toInt() }, { it.toString()})

    fun <T : GenericDecayCondition<*>, V> decayConditionFields(
        instance: RecordCodecBuilder.Instance<T>,
        key: ResourceKey<Registry<V>>
    ): P2<RecordCodecBuilder.Mu<T>, TagOrElementLocation<V>, Boolean> {
        return instance.group<TagOrElementLocation<V>, Boolean>(
            TagOrElementLocation.codec(key).fieldOf("entry").forGetter<T> { t: T -> t.tagOrElementLocation as TagOrElementLocation<V> },
            Codec.BOOL.optionalFieldOf("invert", false).forGetter { obj -> obj.invert() }
        )
    }

    fun <T : GenericDecayCondition<*>, V> createCodec(
        function: (TagOrElementLocation<V>, Boolean) -> T,
        key: ResourceKey<Registry<V>>
    ): MapCodec<T> = RecordCodecBuilder.mapCodec<T> { instance -> decayConditionFields<T, V>(instance, key).apply<T>(instance, function) }

    fun <K, V, M : MutableMap<K, V>> unboundedMap(
        keyCodec: Codec<K>,
        valueCodec: Codec<V>,
        mapMFunction: Function<MutableMap<K, V>, M>
    ): Codec<M> = keyCodec.unboundedMap(valueCodec).xmap<M>(mapMFunction, Function.identity<M>())

    @JvmStatic
    fun parseIntString(string: String): DataResult<Int> {
        try {
            val value = Integer.decode(string)
            return DataResult.success(value)
        } catch (e: Exception) {
            return DataResult.error(e::message)
        }
    }

    var INT_ARRAY_CODEC: Codec<IntArray> = Codec.INT_STREAM.xmap({ obj -> obj.toArray() }, { array -> Arrays.stream(array) })

    class TagOrElementLocation<T>(id: ResourceLocation, tag: Boolean, registryResourceKey: ResourceKey<Registry<T>>) {
        private var tag: TagKey<T>? = null
        private var key: ResourceKey<T>? = null

        init {
            if (tag) this.tag = TagKey.create(registryResourceKey, id)
            else this.key = ResourceKey.create(registryResourceKey, id)
        }

        override fun toString(): String = this.decoratedId()

        private fun decoratedId(): String =
            if (this.tag != null) "#${tag!!.location()}" else this.key?.location()?.toString() ?: "N/A"

        fun test(holder: Holder<T>): Boolean {
            return tag != null && holder.`is`(tag!!) || key != null && holder.`is`(key!!)
        }

        fun getValues(lookup: HolderLookup.RegistryLookup<T>): Set<ResourceKey<T>> {
            key?.let { return mutableSetOf(it) }
            return lookup.get(tag!!).getOrNull()?.mapNotNull { it.unwrapKey().getOrNull() }?.toSet() ?: emptySet()
        }

        companion object {
            fun <T> codec(key: ResourceKey<Registry<T>>): Codec<TagOrElementLocation<T>> {
                return Codec.STRING.comapFlatMap<TagOrElementLocation<T>>({ string ->
                    if (string.startsWith("#")) ResourceLocation.read(string.substring(1)).map<TagOrElementLocation<T>> { resourceLocation -> TagOrElementLocation(resourceLocation, true, key) } else ResourceLocation.read(string).map<TagOrElementLocation<T>> { resourceLocation ->
                        TagOrElementLocation(
                            resourceLocation,
                            false,
                            key
                        )
                    }
                }, Function { obj: TagOrElementLocation<T> -> obj.decoratedId() })
            }

            fun <T> of(tag: TagKey<T>, registry: ResourceKey<Registry<T>>): TagOrElementLocation<T> = TagOrElementLocation(tag.location(), true, registry)

            fun <T> of(tag: ResourceKey<T>, registry: ResourceKey<Registry<T>>): TagOrElementLocation<T> = TagOrElementLocation(tag.location(), false, registry)
        }
    }
}
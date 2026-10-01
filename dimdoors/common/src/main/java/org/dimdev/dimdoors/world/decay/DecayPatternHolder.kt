package org.dimdev.dimdoors.world.decay

import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.world.decay.pattern.DecayPattern
import java.util.function.Consumer

class DecayPatternHolder(val id: ResourceLocation, val value: DecayPattern) {
//    public static final StreamCodec<RegistryFriendlyByteBuf, DecayPatternHolder> STREAM_CODEC;

    override fun equals(other: Any?): Boolean {
        return this === other || other is DecayPatternHolder && this.id == other.id
    }

    override fun hashCode(): Int {
        return this.id.hashCode()
    }

    override fun toString(): String {
        return this.id.toString()
    }

    class Builder internal constructor(private val id: ResourceLocation) {
        private var pattern: DecayPattern.Builder<*>? = null

        fun pattern(pattern: DecayPattern.Builder<*>): Builder {
            this.pattern = pattern
            return this
        }

        fun build(provider: HolderLookup.Provider): DecayPatternHolder {
            val pattern = this.pattern ?: throw IllegalStateException("DecayPattern must not be null")

            return DecayPatternHolder(id, pattern.build(provider)!!)
        }

        fun accept(consumer: Consumer<DecayPatternHolder>, provider: HolderLookup.Provider) {
            consumer.accept(build(provider))
        }
    }

    companion object {
        @JvmStatic
        fun builder(id: ResourceLocation): Builder {
            return Builder(id)
        }

//        STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, RecipeHolder::id, Recipe.STREAM_CODEC, RecipeHolder::value, RecipeHolder::new);
    }
}

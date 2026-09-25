package org.dimdev.dimdoors.pockets.virtual.reference

import com.google.common.base.MoreObjects
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.HolderWeightedList
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import java.util.stream.Stream

class TagReference(
    weight: Equation,
    private val required: List<String>,
    private val blackList: List<String>,
    private val exact: Boolean
) : PocketGeneratorReference<TagReference>(weight) {
    private var pockets: HolderWeightedList<PocketGenerator<*>, PocketGenerationContext>? = null

    override val type get() = VirtualPockets.TAG_REFERENCE

    override fun peekReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>> = selectPocket(parameters, true)

    override fun getReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>> = selectPocket(parameters, false)

    private fun selectPocket(parameters: PocketGenerationContext, peek: Boolean): Holder<PocketGenerator<*>> {
        val pockets = this.pockets ?: getPocketsMatchingTags(parameters.provider.lookupOrThrow(ModRegistryKeys.POCKET_GENERATOR).listElements(), required, blackList, exact).also { this.pockets = it }
        return (if (peek) pockets.peekNextRandomWeighted(parameters) else pockets.getNextRandomWeighted(parameters))!!
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("weight", weight.asString())
            .add("required", required)
            .add("blackList", blackList)
            .add("exact", exact)
            .add("pockets", pockets)
            .toString()
    }

    companion object {
        val CODEC: MapCodec<TagReference> = RecordCodecBuilder.mapCodec { instance ->
            commonFields(instance)
                .and(Codec.STRING.listOf().optionalFieldOf("required", listOf()).forGetter(TagReference::required))
                .and(Codec.STRING.listOf().optionalFieldOf("blackList", listOf()).forGetter(TagReference::blackList))
                .and(Codec.BOOL.optionalFieldOf("exact", false).forGetter(TagReference::exact))
                .apply(instance, ::TagReference)
        }

        const val KEY: String = "tag"

        @JvmStatic
        fun getPocketsMatchingTags(references: Stream<Holder.Reference<PocketGenerator<*>>>, required: List<String>, blackList: List<String>, exact: Boolean): HolderWeightedList<PocketGenerator<*>, PocketGenerationContext> =
            HolderWeightedList(references.filter { it.value().checkTags(required, blackList, exact) }.toList())
    }
}

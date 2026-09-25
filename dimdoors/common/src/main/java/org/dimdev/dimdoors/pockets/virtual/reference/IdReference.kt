package org.dimdev.dimdoors.pockets.virtual.reference

import com.google.common.base.MoreObjects
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceKey
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.ModRegistryKeys
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.VirtualPockets
import org.dimdev.dimdoors.world.pocket.type.Pocket

class IdReference(weight: Equation,private val id: ResourceKey<PocketGenerator<*>>) : PocketGeneratorReference<IdReference>(weight) {
    override fun getWeight(parameters: PocketGenerationContext): Double {
        if (parameters.lookupHolderOptional(id) == null) {
            return 0.0
        }

        return super.getWeight(parameters)
    }

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>? {
        if (parameters.lookupHolderOptional(id) == null) {
            LOGGER.error("Skipping missing pocket generator reference {}.", id)
            return null
        }

        return super.prepareAndPlacePocket(parameters, setupLoot)
    }

    override fun peekReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>> {
        return getReferencedPocketGenerator(parameters)
    }

    override fun getReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>> {
        return parameters.lookupHolder(id)
    }

    override val type get() = VirtualPockets.ID_REFERENCE

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("weight", weight.asString())
            .toString()
    }

    companion object {
        val CODEC: MapCodec<IdReference> = RecordCodecBuilder.mapCodec { instance -> commonFields(instance)
            .and(ResourceKey.codec(ModRegistryKeys.POCKET_GENERATOR).fieldOf("id").forGetter(IdReference::id))
            .apply(instance, ::IdReference)
        }

        const val KEY: String = "id"
        private val LOGGER: Logger = LogManager.getLogger()
    }
}

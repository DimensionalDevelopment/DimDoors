package org.dimdev.dimdoors.pockets.generator

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.PocketLoader
import org.dimdev.dimdoors.pockets.PocketTemplate
import org.dimdev.dimdoors.pockets.modifier.Modifier
import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.subsystem.pocket.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.util.math.Equation
import kotlin.jvm.optionals.getOrNull

class SchematicGenerator(
    builder: Pocket.PocketBuilder<*, *>?,
    weight: Equation,
    setupLoot: Boolean?,
    modifiers: MutableList<Holder<Modifier>>,
    private val templateID: ResourceLocation
) : PocketGenerator<SchematicGenerator>(builder, weight, setupLoot, modifiers) {

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>): Pocket<*, *> {
        val world = parameters.world

        val template: PocketTemplate = PocketLoader.getTemplates()[templateID] ?: throw RuntimeException("Pocket template of id $templateID not found!")

        val pocket: Pocket<*, *> = instance.createPocket(world.dimension(), builder)
        val origin = pocket.origin

        LOGGER.info("Generating pocket from template {} at location {}", templateID, origin)

        template.place(pocket)

        return pocket
    }

    override val type get() = PocketGenerators.SCHEMATIC

    override fun getSize(parameters: PocketGenerationContext) = PocketLoader.getTemplates()[templateID]?.size ?: throw RuntimeException("Pocket template of id $templateID not found!")

    companion object {
        val CODEC: MapCodec<SchematicGenerator> = RecordCodecBuilder.mapCodec { instance -> commonFields(instance)
            .and(ResourceLocation.CODEC.fieldOf("id").forGetter { it.templateID })
            .apply(instance) { builder, weight, setupLoot, modifiers, id ->
                SchematicGenerator(builder.getOrNull(), weight, setupLoot.getOrNull(), modifiers, id)
            }
        }

        private val LOGGER: Logger = LogManager.getLogger()
        const val KEY: String = "schematic"
    }
}
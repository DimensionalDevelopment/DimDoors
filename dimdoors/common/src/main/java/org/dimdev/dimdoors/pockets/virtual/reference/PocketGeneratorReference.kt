package org.dimdev.dimdoors.pockets.virtual.reference

import com.mojang.datafixers.Products.P1
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.generator.PocketGenerator
import org.dimdev.dimdoors.pockets.virtual.ImplementedVirtualPocket
import org.dimdev.dimdoors.world.pocket.type.Pocket

abstract class PocketGeneratorReference<T : PocketGeneratorReference<T>>(
    protected val weight: Equation
) : ImplementedVirtualPocket<T> {
    override fun getWeight(parameters: PocketGenerationContext): Double {
        try {
            return weight.apply(parameters.toVariableMap(mutableMapOf()))
        } catch (e: RuntimeException) {
            LOGGER.error(this.toString())
            throw AssertionError(e)
        }
    }

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext): Pocket<*, *>? {
        return prepareAndPlacePocket(parameters, null)
    }

    override fun prepareAndPlacePocket(parameters: PocketGenerationContext, setupLoot: Boolean?): Pocket<*, *>? {
        val generator = getReferencedPocketGenerator(parameters).value()

        val builder: Pocket.PocketBuilder<*, *> = generator.pocketBuilder(parameters)
            .virtualLocation(parameters.sourceVirtualLocation) // TODO: virtualLocation thing still makes little sense
        generator.applyModifiers(parameters, builder)

        val pocket = generator.prepareAndPlacePocket(parameters, builder)

        val manager = generator.getRiftManager(pocket)

        generator.applyModifiers(parameters, manager)


        generator.setup(pocket, manager, parameters, setupLoot ?: generator.isSetupLoot())

        return pocket
    }

    override fun peekNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> = this

    override fun getNextPocketGeneratorReference(parameters: PocketGenerationContext): PocketGeneratorReference<*> = this

    abstract fun peekReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>>

    abstract fun getReferencedPocketGenerator(parameters: PocketGenerationContext): Holder<PocketGenerator<*>>

    abstract override fun toString(): String

    companion object {
        fun <T : PocketGeneratorReference<T>> commonFields(instance: RecordCodecBuilder.Instance<T>): P1<RecordCodecBuilder.Mu<T>, Equation> =
            instance.group(Equation.CODEC.optionalFieldOf("weight", Equation.FIVE).forGetter { it.weight })

        private val LOGGER: Logger = LogManager.getLogger()
    }
}

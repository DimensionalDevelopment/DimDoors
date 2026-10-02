package org.dimdev.dimdoors.pockets.generator

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.Vec3i
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.pockets.modifier.Modifier
import org.dimdev.dimdoors.rift.registry.PocketRegistry.Companion.instance
import org.dimdev.dimdoors.world.pocket.type.AbstractPocket
import org.dimdev.dimdoors.world.pocket.type.Pocket
import kotlin.jvm.optionals.getOrNull

class VoidGenerator(
    builder: AbstractPocket.AbstractPocketBuilder<*, *>?,
    weight: Equation,
    setupLoot: Boolean?,
    modifiers: MutableList<Holder<Modifier>>,
    tags: MutableList<String>,
    private val height: Equation,
    private val width: Equation,
    private val length: Equation
) : PocketGenerator<VoidGenerator>(builder, weight, setupLoot, modifiers, tags) {

    override fun prepareAndPlacePocket(
        parameters: PocketGenerationContext,
        builder: Pocket.PocketBuilder<*, *>
    ): Pocket<*, *> {
        val pocket: Pocket<*, *> = instance.createPocket(parameters.world.dimension(), builder)!!
        val variableMap = parameters.toVariableMap(mutableMapOf())
        pocket.setSize(
            width.apply(variableMap).toInt(),
            height.apply(variableMap).toInt(),
            length.apply(variableMap).toInt()
        )

        return pocket
    }

    override val type get() = PocketGenerators.VOID

    override fun getSize(parameters: PocketGenerationContext): Vec3i {
        val variableMap = parameters.toVariableMap(mutableMapOf())
        return Vec3i(
            width.apply(variableMap).toInt(),
            height.apply(variableMap).toInt(),
            length.apply(variableMap).toInt()
        )
    }

    companion object {
        val CODEC: MapCodec<VoidGenerator> = RecordCodecBuilder.mapCodec<VoidGenerator> { instance -> commonFields(instance)
                    .and(Equation.CODEC.fieldOf("height").forGetter(VoidGenerator::height))
                    .and(Equation.CODEC.fieldOf("width").forGetter(VoidGenerator::width))
                    .and(Equation.CODEC.fieldOf("length").forGetter(VoidGenerator::length))
                    .apply(instance) { builder, weight, setupLoot, modifiers, tags, height, width, length ->
                        VoidGenerator(builder.getOrNull(), weight, setupLoot.getOrNull(), modifiers, tags, height, width, length)
                    }
            }

        const val KEY: String = "void"
    }
}
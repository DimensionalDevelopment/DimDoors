package org.dimdev.dimdoors.pockets.modifier

import com.google.common.base.MoreObjects
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.util.CodecUtils.nullable
import org.dimdev.dimdoors.util.schematic.SchematicBlockPalette
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class ShellModifier(val layers: MutableList<Layer>, val boxToDrawAround: BoundingBox?) : Modifier {
    override val type get() = Modifiers.SHELL

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {
        val variableMap = parameters.toVariableMap(mutableMapOf<String, Double>())
        for (layer in layers) {
            val thickness = layer.getThickness(variableMap)
            builder.expandExpected(Vec3i(2 * thickness, 2 * thickness, 2 * thickness))
            builder.offsetOrigin(Vec3i(thickness, thickness, thickness))
        }
    }

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val pocket = manager.pocket
        val templateBox = boxToDrawAround?.let { a ->
            val origin = pocket.origin
            a.moved(origin.x, origin.y, origin.z)
        } ?: pocket.box

        val variableMap = pocket.toVariableMap(mutableMapOf())

        var cumulativeThickness = 0

        for (layer in layers) {
            val thickness = layer.getThickness(variableMap)

            drawLayer(layer, templateBox, cumulativeThickness, thickness, parameters.world)

            cumulativeThickness += thickness
        }
    }

    private fun drawLayer(layer: Layer, templateBox: BoundingBox, offset: Int, thickness: Int, world: ServerLevel) {
        val state = layer.blockState
        val pos = BlockPos.MutableBlockPos()

        val innerMinX = templateBox.minX() - offset
        val innerMinY = templateBox.minY() - offset
        val innerMinZ = templateBox.minZ() - offset
        val innerMaxX = templateBox.maxX() + offset
        val innerMaxY = templateBox.maxY() + offset
        val innerMaxZ = templateBox.maxZ() + offset

        val outerMinX = innerMinX - thickness
        val outerMinY = innerMinY - thickness
        val outerMinZ = innerMinZ - thickness
        val outerMaxX = innerMaxX + thickness
        val outerMaxY = innerMaxY + thickness
        val outerMaxZ = innerMaxZ + thickness

        // -X
        drawSide(world, pos, state, outerMinX, outerMinY, outerMinZ, innerMinX - 1, outerMaxY, outerMaxZ)

        // +X
        drawSide(world, pos, state, innerMaxX + 1, outerMinY, outerMinZ, outerMaxX, outerMaxY, outerMaxZ)

        // -Y
        drawSide(world, pos, state, innerMinX, outerMinY, outerMinZ, innerMaxX, innerMinY - 1, outerMaxZ)

        // +Y
        drawSide(world, pos, state, innerMinX, innerMaxY + 1, outerMinZ, innerMaxX, outerMaxY, outerMaxZ)

        // -Z
        drawSide(world, pos, state, innerMinX, innerMinY, outerMinZ, innerMaxX, innerMaxY, innerMinZ - 1)

        // +Z
        drawSide(world, pos, state, innerMinX, innerMinY, innerMaxZ + 1, innerMaxX, innerMaxY, outerMaxZ)
    }


    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("layers", layers)
            .toString()
    }


    @JvmRecord
    data class Layer(val blockState: BlockState, val thickness: Equation) {
        fun getThickness(variableMap: MutableMap<String, Double>): Int {
            return thickness.apply(variableMap).toInt()
        }

        companion object {
            val CODEC = RecordCodecBuilder.create { instance -> instance.group(
                    SchematicBlockPalette.Entry.CODEC.fieldOf("block_state").forGetter(Layer::blockState),
                    Equation.CODEC.optionalFieldOf("thickness", Equation.ONE).forGetter(Layer::thickness)
                ).apply(instance, ::Layer)
            }
        }
    }

    companion object {
        val CODEC: MapCodec<ShellModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Layer.CODEC.listOf().optionalFieldOf("layers", emptyList()).forGetter(ShellModifier::layers),
                BoundingBox.CODEC.optionalFieldOf("box_to_draw_around").nullable().forGetter(ShellModifier::boxToDrawAround)
            ).apply(instance, ::ShellModifier)
        }

        //TODO: use boxToDrawAround as an alternate cube to generate around in a pocket.
        const val KEY: String = "shell"

        private fun drawSide(
            world: ServerLevel,
            pos: BlockPos.MutableBlockPos,
            state: BlockState,
            minX: Int, minY: Int, minZ: Int,
            maxX: Int, maxY: Int, maxZ: Int
        ) {
            for (x in minX..maxX)
                for (y in minY..maxY)
                    for (z in minZ..maxZ)
                        world.setBlockAndUpdate(pos.set(x, y, z), state)
        }
    }
}
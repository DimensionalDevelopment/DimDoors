package org.dimdev.dimdoors.pockets.modifier

import com.google.common.base.MoreObjects
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import org.dimdev.dimdoors.api.util.math.Equation
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes
import org.dimdev.dimdoors.block.entity.RiftData
import org.dimdev.dimdoors.pockets.PocketGenerationContext
import org.dimdev.dimdoors.rift.targets.IdMarker
import org.dimdev.dimdoors.util.CodecUtils.nullableForGetter
import kotlin.jvm.optionals.getOrNull
import org.dimdev.dimdoors.world.pocket.type.Pocket

data class DimensionalDoorModifier(private val facing: Direction, private val doorType: Holder<Block>, private val doorData: Holder<RiftData>?, private val x: Equation, private val y: Equation, private val z: Equation) : Modifier {

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("facing", facing)
            .add("doorType", doorType)
            .add("doorData", doorData)
            .add("x", x)
            .add("y", y)
            .add("z", z)
            .toString()
    }

    override val type get() = Modifiers.DIMENSIONAL_DOOR

    override fun apply(parameters: PocketGenerationContext, manager: RiftManager) {
        val variableMap = manager.pocket.toVariableMap()
        val pocketOrigin = manager.pocket.origin
        val pos = BlockPos(
            (x.apply(variableMap) + pocketOrigin.x).toInt(),
            (y.apply(variableMap) + pocketOrigin.y).toInt(),
            (z.apply(variableMap) + pocketOrigin.z).toInt()
        )

        val state = doorType.value().defaultBlockState()

        val lower = state.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.FACING, facing)
        val upper = state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.FACING, facing)
        val rift: EntranceRiftBlockEntity<*> = ModBlockEntityTypes.ENTRANCE_RIFT.create(pos, lower)!!
        rift.setLevel(parameters.world)

        if (doorData == null) {
            rift.setDestination(IdMarker(manager.nextId()))
        } else {
            rift.data = doorData.value().copy()
        }

        manager.add(rift)

        val world = parameters.world

        world.setBlockAndUpdate(pos, lower)
        world.setBlockAndUpdate(pos.above(), upper)

        world.setBlockEntity(rift)
    }

    override fun apply(parameters: PocketGenerationContext, builder: Pocket.PocketBuilder<*, *>) {}

    companion object {
        val CODEC: MapCodec<DimensionalDoorModifier> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    Direction.CODEC.fieldOf("facing").flatXmap({ direction ->
                        if (direction.axis.isHorizontal) DataResult.success(direction) else DataResult.error { "Direction:${direction.name}is not horizontal." } },
                        DataResult<Direction>::success).forGetter(DimensionalDoorModifier::facing),
                    BuiltInRegistries.BLOCK.holderByNameCodec().validate { holder ->
                        if (holder.value() is DimensionalDoorBlock<*>) DataResult.success(holder)
                        else DataResult.error { "${holder.registeredName} is not an instance of DimensionalDoorBlock." }
                    }.fieldOf("door_type").forGetter(DimensionalDoorModifier::doorType),
                    RiftData.HOLDER_CODEC.optionalFieldOf("rift_data").nullableForGetter(DimensionalDoorModifier::doorData),
                    Equation.CODEC.fieldOf("x").forGetter(DimensionalDoorModifier::x),
                    Equation.CODEC.fieldOf("y").forGetter(DimensionalDoorModifier::y),
                    Equation.CODEC.fieldOf("z").forGetter(DimensionalDoorModifier::z)
                ).apply(instance) { facing, doorType, doorData, x, y, z -> DimensionalDoorModifier(facing, doorType, doorData.getOrNull(), x, y, z) }
        }

        const val KEY: String = "door"
    }
}
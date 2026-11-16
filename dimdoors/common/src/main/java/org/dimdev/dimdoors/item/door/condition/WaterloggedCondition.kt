package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity

data class WaterloggedCondition(val waterlogged: Boolean) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean =
        rift.level!!.getBlockState(rift.blockPos).getValue(BlockStateProperties.WATERLOGGED) == waterlogged

    override val type get() = Conditions.BIOME

    companion object {
        val CODEC: MapCodec<WaterloggedCondition> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("waterlogged").forGetter(WaterloggedCondition::waterlogged))
                .apply(instance, ::WaterloggedCondition
            )
        }
    }
}
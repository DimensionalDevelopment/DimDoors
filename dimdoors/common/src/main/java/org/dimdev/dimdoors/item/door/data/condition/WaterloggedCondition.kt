package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

data class WaterloggedCondition(val waterlogged: Boolean) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean =
        rift.riftLevel.getBlockState(rift.blockPos).getValue(BlockStateProperties.WATERLOGGED) == waterlogged

    override val type get() = Conditions.BIOME

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.BOOL.fieldOf("waterlogged").forGetter(WaterloggedCondition::waterlogged))
                .apply(instance, ::WaterloggedCondition
            )
        }
    }
}
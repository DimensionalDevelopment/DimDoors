package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity
import java.util.function.Function

@JvmRecord
data class WorldMatchCondition(val world: ResourceKey<Level>) : Condition {
    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean = rift.riftLevel.dimension() == this.world

    override val type get() = Conditions.WORLD_MATCH

    companion object {
        val CODEC = RecordCodecBuilder.mapCodec { instance -> instance.group(
                Level.RESOURCE_KEY_CODEC.fieldOf("world")
                    .forGetter(WorldMatchCondition::world)
            ).apply(
                instance,
                ::WorldMatchCondition
            )
        }
    }
}
package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.Codec
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity


interface Condition : MapCodecHasHolder<Condition> {
    fun matches(rift: EntranceRiftBlockEntity<*>): Boolean

    companion object {
        fun level(key: ResourceKey<Level>) = WorldMatchCondition(key)

        fun not(condition: Condition) = InverseCondition(condition)

        fun alwaysTrue() = AlwaysTrueCondition

        val CODEC = Codec.lazyInitialized { Conditions.codec }
    }
}

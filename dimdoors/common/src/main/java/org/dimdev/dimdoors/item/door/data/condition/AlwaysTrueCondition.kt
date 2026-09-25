package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import net.minecraft.core.Holder
import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

object AlwaysTrueCondition : Condition, SingletonInstance<AlwaysTrueCondition>() {

    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean {
        return true
    }

    override val type: Holder<out MapCodec<Condition>> get() = Conditions.ALWAYS_TRUE
}

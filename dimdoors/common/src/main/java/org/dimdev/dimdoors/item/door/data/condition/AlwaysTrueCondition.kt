package org.dimdev.dimdoors.item.door.data.condition

import org.dimdev.dimdoors.SingletonInstance
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity

object AlwaysTrueCondition : Condition, SingletonInstance<AlwaysTrueCondition>() {

    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean {
        return true
    }

    override val type get() = Conditions.ALWAYS_TRUE
}

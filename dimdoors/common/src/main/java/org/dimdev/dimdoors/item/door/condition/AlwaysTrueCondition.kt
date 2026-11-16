package org.dimdev.dimdoors.item.door.condition

import org.dimdev.dimdoors.blockentity.EntranceRiftBlockEntity
import org.dimdev.dimdoors.util.codec.SingletonInstance

object AlwaysTrueCondition : Condition, SingletonInstance<AlwaysTrueCondition>() {

    override fun matches(rift: EntranceRiftBlockEntity<*>): Boolean {
        return true
    }

    override val type get() = Conditions.ALWAYS_TRUE
}

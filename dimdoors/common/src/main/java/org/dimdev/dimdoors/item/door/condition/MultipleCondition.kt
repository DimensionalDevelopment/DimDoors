package org.dimdev.dimdoors.item.door.condition

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.util.ext.imutableList

abstract class MultipleCondition protected constructor(val conditions: List<Condition>) : Condition {

    companion object {
        val LIST: MapCodec<List<Condition>> = Condition.CODEC.imutableList().fieldOf("conditions")
    }
}

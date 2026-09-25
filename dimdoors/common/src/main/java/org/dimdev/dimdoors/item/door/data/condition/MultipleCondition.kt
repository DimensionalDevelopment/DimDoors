package org.dimdev.dimdoors.item.door.data.condition

import com.mojang.serialization.MapCodec
import org.dimdev.dimdoors.util.CodecUtils.imutableList

abstract class MultipleCondition protected constructor(val conditions: List<Condition>) : Condition {

    companion object {
        val LIST = Condition.CODEC.imutableList().fieldOf("conditions")
    }
}

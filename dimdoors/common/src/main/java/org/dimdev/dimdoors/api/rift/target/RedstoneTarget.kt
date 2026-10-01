package org.dimdev.dimdoors.api.rift.target

import org.dimdev.dimdoors.api.util.Location

interface RedstoneTarget : Target {
    fun recieveSignal(strength: Int, location: Location?): Boolean

    fun getSignal(location: Location?): Int = 0
}

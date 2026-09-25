package org.dimdev.dimdoors.api.rift.target

import org.dimdev.dimdoors.api.util.InstanceMap
import java.util.*
import java.util.function.Supplier

object DefaultTargets {
    private val DEFAULT_TARGETS = InstanceMap()

    fun <T : Target?> getDefaultTarget(type: Class<T>): T = DEFAULT_TARGETS[type] ?: throw RuntimeException("No default target for " + type.getCanonicalName() + " registered")

    fun <T : Target, U : T> registerDefaultTarget(type: Class<T>, impl: U) = DEFAULT_TARGETS.put(type, impl)
}

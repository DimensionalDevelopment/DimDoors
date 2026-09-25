package org.dimdev.dimcore.api.util

import java.util.function.Consumer
import java.util.function.Function

class SimpleEvent<T> private constructor(invokerFactory: (MutableList<T>) -> T) {
    private val listeners = mutableListOf<T>()
    private val invoker: T = invokerFactory.invoke(listeners)

    fun register(listener: T) {
        listeners.add(listener)
    }

    fun invoker(): T {
        return invoker
    }

    companion object {
        fun <T> of(factory: (MutableList<T>) -> T): SimpleEvent<T> = SimpleEvent(factory)

        @JvmStatic fun <T> consumerLoop(): SimpleEvent<(T) -> Unit> = SimpleEvent<(T) -> Unit> { consumers: MutableList<(T) -> Unit> -> { value: T -> consumers.forEach { it.invoke(value) } } }
    }
}

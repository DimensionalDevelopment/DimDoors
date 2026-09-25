package org.dimdev.dimcore.util

import java.util.function.UnaryOperator

interface DataValue<T> {
    fun get(obj: Any?): T?
    fun getOrDefault(obj: Any?, value: T): T = get(obj) ?: value

    fun getOrCreate(obj : Any?): T
    fun set(obj : Any?, value: T?)
    fun update(obj : Any?, defaultValue: T, operator: UnaryOperator<T?>)
    fun update(obj : Any?, operator: UnaryOperator<T?>)
    fun remove(obj : Any?)
    fun has(obj : Any?): Boolean
}

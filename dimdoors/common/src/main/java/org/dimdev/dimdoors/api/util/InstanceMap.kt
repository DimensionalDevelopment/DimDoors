package org.dimdev.dimdoors.api.util

class InstanceMap {
    // Type safe map between classes and instances
    private val uncheckedMap = mutableMapOf<Class<*>, Any>()

    fun <T> put(key: Class<T>, value: T) { this.uncheckedMap[key] = value as Any }

    operator fun <T> get(key: Class<T>): T? = key.cast(this.uncheckedMap[key])

    fun <T> remove(key: Class<T>): T? = key.cast(this.uncheckedMap.remove(key))

    fun clear() = this.uncheckedMap.clear()

    fun containsKey(key: Class<*>): Boolean = this.uncheckedMap.containsKey(key)

    fun containsValue(value: Any): Boolean = this.uncheckedMap.containsValue(value)
}

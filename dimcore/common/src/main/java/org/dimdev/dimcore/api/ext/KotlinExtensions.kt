package org.dimdev.dimcore.api.ext

fun <T : Any> Any.cast(): T = this as T

infix fun <T : Any> Any.cast(clazz: Class<T>): T? = this.takeIf(clazz::isInstance)?.let(clazz::cast)

inline fun <reified T: Any> Any.castOrNull(): T? = this as? T

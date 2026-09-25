package org.dimdev.dimcore.api

inline fun <reified T: Any> Any.castOrNull(): T? = this as? T
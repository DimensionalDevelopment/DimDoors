package org.dimdev.dimdoors.api.util

fun interface Weighted<P> {
    fun getWeight(parameters: P): Double
}

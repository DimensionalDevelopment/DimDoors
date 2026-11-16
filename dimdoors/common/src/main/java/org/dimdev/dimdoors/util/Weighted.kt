package org.dimdev.dimdoors.util

fun interface Weighted<P> {
    fun getWeight(parameters: P): Double
}

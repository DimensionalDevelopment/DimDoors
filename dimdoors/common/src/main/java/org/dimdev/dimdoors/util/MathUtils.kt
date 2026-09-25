package org.dimdev.dimdoors.util

object MathUtils {
    fun betewen(value: Int, min: Int, max: Int): Boolean {
        return value >= min && value <= max
    }

    fun betewen(value: Double, min: Double, max: Double): Boolean {
        return value >= min && value <= max
    }
}

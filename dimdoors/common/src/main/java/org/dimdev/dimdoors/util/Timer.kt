package org.dimdev.dimdoors.util

import kotlin.math.max

class Timer(val fadeInTicks: Double, val durationTicks: Double, val fadeOutTicks: Double,
            private var fadeInStartedAt: Double = Double.NaN,
            private var fadeInStartVisibility: Double = 0.0,
            private var fadeOutStartsAt: Double = Double.NaN
) {
    init {
        require(listOf(fadeInTicks, durationTicks, fadeOutTicks).all { it.isFinite() && it >= 0.0 }) { "Timer ticks must be finite and non-negative" }
    }

    fun trigger() {
        val now = currentTick
        when {
            !isRunning(now) -> restart(now, 0.0)
            now > fadeOutStartsAt -> restart(now, getVisibility(now))
            else -> fadeOutStartsAt = max(fadeOutStartsAt, max(fadeInStartedAt + fadeInTicks, now) + durationTicks)
        }
    }

    fun reset() = restart(Double.NaN, 0.0)

    private fun restart(now: Double, from: Double) {
        fadeInStartedAt = now
        fadeInStartVisibility = from
        fadeOutStartsAt = now + fadeInTicks + durationTicks
    }

    fun hasStarted() = !fadeInStartedAt.isNaN()

    fun isRunning(now: Double) = hasStarted() && now >= fadeInStartedAt && now < fadeOutStartsAt + fadeOutTicks

    val isRunning get() = isRunning(currentTick)

    val visibility get() = getVisibility(currentTick).toFloat()

    private fun getVisibility(now: Double): Double {
        val fadeInElapsed = now - fadeInStartedAt
        return when {
            !hasStarted() || fadeInElapsed < 0.0 -> 0.0
            fadeInTicks > 0.0 && fadeInElapsed < fadeInTicks -> fadeInStartVisibility + (1.0 - fadeInStartVisibility) * (fadeInElapsed / fadeInTicks)
            now < fadeOutStartsAt -> 1.0
            fadeOutTicks > 0.0 && now - fadeOutStartsAt < fadeOutTicks -> 1.0 - (now - fadeOutStartsAt) / fadeOutTicks
            else -> 0.0
        }
    }

    companion object {
        var currentTick = 0.0
            private set

        fun update(ticks: Long, deltaTick: Float) {
            currentTick = ticks + deltaTick.toDouble()
        }

        @JvmStatic
        fun create(fadeIn: Double, duration: Double, fadeOut: Double) = Timer(fadeIn, duration, fadeOut)
    }
}
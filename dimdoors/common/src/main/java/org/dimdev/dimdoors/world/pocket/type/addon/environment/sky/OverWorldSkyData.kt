package org.dimdev.dimdoors.world.pocket.type.addon.environment.sky

import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import kotlin.math.cos

interface OverWorldSkyData : SkyData {
    val sunAngle: Float get() = this.timeOfDay * (Math.PI.toFloat() * 2)

    val timeOfDay: Float get() = timeOfDay(this.dayTime)

    val dayTime: Long
    val moonPhase: Int

    val starBrightness: Float
        get() = (1.0f - (Mth.cos(this.timeOfDay * (Math.PI.toFloat() * 2)) * 2.0f + 0.25f))
                .let { Mth.clamp(it, 0f, 1f) }
                .let { it * it * 0.5f }

    val sunriseColors: FloatArray

    fun getSunriseColor(f: Float): FloatArray? {
        val h = 0.4f
        val i = Mth.cos(f * (Math.PI.toFloat() * 2f)) - 0.0f
        val j = -0.0f
        if (i >= -0.4f && i <= 0.4f) {
            val k = (i - -0.0f) / 0.4f * 0.5f + 0.5f
            var l = 1.0f - (1.0f - Mth.sin(k * Math.PI.toFloat())) * 0.99f
            l *= l

            val sunriseCol = this.sunriseColors

            sunriseCol[0] = k * 0.3f + 0.7f
            sunriseCol[1] = k * k * 0.7f + 0.2f
            sunriseCol[2] = k * k * 0.0f + 0.2f
            sunriseCol[3] = l
            return sunriseCol
        } else {
            return null
        }
    }

    val correctedSkyColor: Vec3
        get() {
            val g = this.timeOfDay
            val vec33 = this.skyColor
            var h = Mth.cos(g * (Math.PI.toFloat() * 2f)) * 2.0f + 0.5f
            h = Mth.clamp(h, 0.0f, 1.0f)
            var i = vec33.x.toFloat() * h
            var j = vec33.y.toFloat() * h
            var k = vec33.z.toFloat() * h
            val l = this.rainLevel
            if (l > 0.0f) {
                val m = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.6f
                val n = 1.0f - l * 0.75f
                i = i * n + m * (1.0f - n)
                j = j * n + m * (1.0f - n)
                k = k * n + m * (1.0f - n)
            }

            val m = this.thunderLevel
            if (m > 0.0f) {
                val n = (i * 0.3f + j * 0.59f + k * 0.11f) * 0.2f
                val o = 1.0f - m * 0.75f
                i = i * o + n * (1.0f - o)
                j = j * o + n * (1.0f - o)
                k = k * o + n * (1.0f - o)
            }

            //        int p = this.getSkyFlashTime();
//        if (p > 0) {
//            float o = (float)p - f;
//            if (o > 1.0F) {
//                o = 1.0F;
//            }
//
//            o *= 0.45F;
//            i = i * (1.0F - o) + 0.8F * o;
//            j = j * (1.0F - o) + 0.8F * o;
//            k = k * (1.0F - o) + 1.0F * o;
//        }
            return Vec3(i.toDouble(), j.toDouble(), k.toDouble())
        }

    val skyColor: Vec3
    val rainLevel: Float
    val thunderLevel: Float

    override val type : SkyDataHolder get() = SkyDatum.OVERWORLD

    companion object {
        fun timeOfDay(dayTime: Long): Float {
            val d = Mth.frac(dayTime.toDouble() / 24000.0 - 0.25)
            val e = 0.5 - cos(d * Math.PI) / 2.0
            return (d * 2.0 + e).toFloat() / 3.0f
        }
    }
}

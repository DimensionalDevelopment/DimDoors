package org.dimdev.dimdoors.api.util

import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.dimdev.dimdoors.util.Copyable
import java.util.function.Function


data class RGBA(val red: Float, val green: Float, val blue: Float, val alpha: Float) : Copyable<RGBA> {
    override fun copy() = RGBA(red, green, blue, alpha)

    companion object {
        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("red").forGetter(RGBA::red),
                Codec.FLOAT.fieldOf("green").forGetter(RGBA::green),
                Codec.FLOAT.fieldOf("blue").forGetter(RGBA::blue),
                Codec.FLOAT.fieldOf("alpha").forGetter(RGBA::alpha)
            ).apply(instance, ::RGBA)
        }

        val NONE: RGBA = RGBA(0f, 0f, 0f, 0f)
    }
}

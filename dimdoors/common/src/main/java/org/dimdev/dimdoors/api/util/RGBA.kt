package org.dimdev.dimdoors.api.util

import org.dimdev.dimcore.api.errata.schema.ElementIds
import org.dimdev.dimcore.api.errata.schema.Schemas
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.util.Copyable


data class RGBA(val red: Float, val green: Float, val blue: Float, val alpha: Float) : Copyable<RGBA> {
    override fun copy() = RGBA(red, green, blue, alpha)

    companion object {
        val CODEC = Schemas.record<RGBA>(DimensionalDoors.id("rgba")) { instance ->
            instance.group(
                Schemas.float(0f, 1f).fieldOf("red").forGetter(RGBA::red),
                Schemas.float(0f, 1f).fieldOf("green").forGetter(RGBA::green),
                Schemas.float(0f, 1f).fieldOf("blue").forGetter(RGBA::blue),
                Schemas.float(0f, 1f).fieldOf("alpha").forGetter(RGBA::alpha)
            ).apply(instance, ::RGBA)
        }.element(ElementIds.COLOR)

        val NONE: RGBA = RGBA(0f, 0f, 0f, 0f)
    }
}

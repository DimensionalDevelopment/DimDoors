package org.dimdev.dimdoors.client

import com.mojang.blaze3d.shaders.Uniform
import net.minecraft.client.renderer.ShaderInstance
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimdoors.PortalColors.base
import org.dimdev.dimdoors.api.client.UniformExt

object ModShaders {
    private var COLORS: Uniform? = null
    private lateinit var DIMENSIONAL_PORTAL: ShaderInstance
    private var current: IntArray? = null

    var dimensionalPortal: ShaderInstance
        get() = DIMENSIONAL_PORTAL
        set(dimensionalPortal) {
            DIMENSIONAL_PORTAL = dimensionalPortal

            COLORS = dimensionalPortal.getUniform("Colors").also {
                it?.cast<UniformExt>()?.`dimensionalDoors$set`(current ?: base())
            }
        }

    val portalColors: IntArray get() = current ?: base()

    fun setPortalColors(colors: IntArray): Boolean {
        current = colors.copyOf()
        return COLORS?.cast<UniformExt>()?.`dimensionalDoors$set`(colors) != null
    }
}
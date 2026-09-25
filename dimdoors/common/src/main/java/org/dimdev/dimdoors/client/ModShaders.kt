package org.dimdev.dimdoors.client

import com.mojang.blaze3d.shaders.Uniform
import net.minecraft.client.renderer.ShaderInstance
import org.dimdev.dimcore.api.cast
import org.dimdev.dimdoors.PortalColors.base
import org.dimdev.dimdoors.api.client.UniformExt

object ModShaders {
    private var COLORS: Uniform? = null
    private lateinit var DIMENSIONAL_PORTAL: ShaderInstance

    var dimensionalPortal: ShaderInstance
        get() = DIMENSIONAL_PORTAL
        set(dimensionalPortal) {
            DIMENSIONAL_PORTAL = dimensionalPortal

            COLORS = dimensionalPortal.getUniform("Colors").also {
                it?.cast<UniformExt>()?.`dimensionalDoors$set`(base())
            }
        }

    fun setPortalColors(colors: IntArray): Boolean = COLORS?.cast<UniformExt>()?.`dimensionalDoors$set`(colors) != null

    fun getPortalColors(target: IntArray): Boolean = COLORS?.also { it.intBuffer.get(0, target, 0, target.size) } != null
}

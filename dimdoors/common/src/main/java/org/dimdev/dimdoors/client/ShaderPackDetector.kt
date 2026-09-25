package org.dimdev.dimdoors.client

import net.minecraft.client.renderer.RenderType

fun interface ShaderPackDetector {
    fun wrap(type: (RenderType) -> Unit)
}

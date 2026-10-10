package org.dimdev.dimdoors.client.render.node.iris

import com.mojang.blaze3d.platform.GlStateManager
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer
import org.dimdev.dimdoors.client.render.node.NodeFramebuffer
import org.lwjgl.opengl.GL30

class IrisNodeFramebuffer(private val framebuffer: GlFramebuffer) : NodeFramebuffer {
    override val id: Int get() = framebuffer.id

    override fun bind() = framebuffer.bind()

    override fun status(): Int {
        bind()
        return GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
    }

    override fun addColorAttachment(index: Int, texture: Int) {}

    override fun addDepthAttachment(texture: Int) {}

    override fun destroy() {}
}

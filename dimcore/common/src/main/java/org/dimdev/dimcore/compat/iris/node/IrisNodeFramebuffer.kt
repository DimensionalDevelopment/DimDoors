package org.dimdev.dimcore.compat.iris.node

import com.mojang.blaze3d.platform.GlStateManager
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer
import org.dimdev.dimcore.client.node.NodeFramebuffer
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

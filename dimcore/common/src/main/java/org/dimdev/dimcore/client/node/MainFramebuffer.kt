package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL30

object MainFramebuffer : NodeFramebuffer {
    private val main get() = Minecraft.getInstance().mainRenderTarget

    override val id: Int get() = main.frameBufferId

    override fun bind() = main.bindWrite(false)

    override fun status(): Int {
        bind()
        return GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
    }

    override fun addColorAttachment(index: Int, texture: Int) {}

    override fun addDepthAttachment(texture: Int) {}

    override fun destroy() {}
}

package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL30

object ItemEntityFramebuffer : NodeFramebuffer {
    private val target get() = Minecraft.getInstance().levelRenderer.itemEntityTarget ?: Minecraft.getInstance().mainRenderTarget

    override val id: Int get() = target.frameBufferId

    override fun bind() = target.bindWrite(false)

    override fun status(): Int {
        bind()
        return GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
    }

    override fun addColorAttachment(index: Int, texture: Int) {}

    override fun addDepthAttachment(texture: Int) {}

    override fun destroy() {}
}

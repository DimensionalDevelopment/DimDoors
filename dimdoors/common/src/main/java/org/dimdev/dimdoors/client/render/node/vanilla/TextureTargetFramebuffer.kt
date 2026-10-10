package org.dimdev.dimdoors.client.render.node.vanilla

import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.GlStateManager
import net.minecraft.client.Minecraft
import org.dimdev.dimdoors.client.render.node.NodeFramebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30

class TextureTargetFramebuffer(width: Int, height: Int) : NodeFramebuffer {
    val target = TextureTarget(width, height, true, Minecraft.ON_OSX)

    override val id: Int get() = target.frameBufferId

    val colorTexture: Int get() = target.colorTextureId

    val depthTexture: Int get() = target.depthTextureId

    override fun bind() = target.bindWrite(false)

    override fun status(): Int {
        bind()
        return GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER)
    }

    override fun addColorAttachment(index: Int, texture: Int) {
        bind()
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + index, GL11.GL_TEXTURE_2D, texture, 0)
    }

    override fun addDepthAttachment(texture: Int) {
        bind()
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, texture, 0)
    }

    override fun destroy() = target.destroyBuffers()

    fun clear() = target.clear(Minecraft.ON_OSX)

    fun resize(width: Int, height: Int) = target.resize(width, height, Minecraft.ON_OSX)
}

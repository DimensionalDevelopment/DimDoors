package org.dimdev.dimcore.client.node.vanilla

import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.platform.TextureUtil
import net.minecraft.client.Minecraft
import org.dimdev.dimcore.client.node.NodeFramebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class TextureTargetFramebuffer(width: Int, height: Int, private val attachments: Int = 1) : NodeFramebuffer {
    val target = TextureTarget(width, height, true, Minecraft.ON_OSX)

    private var extras = IntArray(0)

    init {
        target.setClearColor(0f, 0f, 0f, 0f)
        createExtras()
    }

    override val id: Int get() = target.frameBufferId

    val colorTexture: Int get() = target.colorTextureId

    val depthTexture: Int get() = target.depthTextureId

    fun colorTexture(index: Int): Int = if (index == 0) colorTexture else extras[index - 1]

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

    override fun destroy() {
        deleteExtras()
        target.destroyBuffers()
    }

    fun clear() = target.clear(Minecraft.ON_OSX)

    fun resize(width: Int, height: Int) {
        deleteExtras()
        target.resize(width, height, Minecraft.ON_OSX)
        createExtras()
    }

    private fun createExtras() {
        if (attachments <= 1) return
        extras = IntArray(attachments - 1) { TextureUtil.generateTextureId() }
        extras.forEachIndexed { i, texture ->
            GlStateManager._bindTexture(texture)
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
            GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, target.width, target.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, null)
            addColorAttachment(i + 1, texture)
        }
        GlStateManager._bindTexture(0)
        GL20.glDrawBuffers(IntArray(attachments) { GL30.GL_COLOR_ATTACHMENT0 + it })
        target.unbindWrite()
    }

    private fun deleteExtras() {
        extras.forEach(TextureUtil::releaseTextureId)
        extras = IntArray(0)
    }
}

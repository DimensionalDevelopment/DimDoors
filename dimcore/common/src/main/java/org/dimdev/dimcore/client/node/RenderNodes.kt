package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.server.packs.resources.ResourceManager
import org.lwjgl.opengl.GL11

object RenderNodes {
    private val registered = mutableListOf<RenderNode>()

    val nodes: List<RenderNode> get() = registered

    var external: () -> Boolean = { false }

    fun register(node: RenderNode) {
        registered += node
    }

    @JvmStatic
    fun reloadAll(manager: ResourceManager) {
        val main = Minecraft.getInstance().mainRenderTarget
        registered.forEach { it.reload(main.width, main.height) }
    }

    @JvmStatic
    fun resize(width: Int, height: Int) {
        registered.forEach { it.resize(width, height) }
    }

    @JvmStatic
    fun composite() {
        registered.forEach { node ->
            val program = node.compositeProgram() ?: return@forEach
            node.target ?: return@forEach

            node.compositeFramebuffer().bind()
            RenderSystem.enableDepthTest()
            RenderSystem.depthFunc(GL11.GL_LEQUAL)
            RenderSystem.depthMask(true)
            RenderSystem.enableBlend()
            RenderSystem.blendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE)
            program.bind()
            BufferUploader.draw(quad())
            program.unbind()
            RenderSystem.disableBlend()
            RenderSystem.defaultBlendFunc()
        }
        MainFramebuffer.bind()
    }

    @JvmStatic
    fun clear() {
        registered.forEach(RenderNode::clear)
        MainFramebuffer.bind()
    }

    private fun quad(): MeshData {
        val quad = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        quad.addVertex(0f, 0f, 0f).setUv(0f, 0f)
        quad.addVertex(1f, 0f, 0f).setUv(1f, 0f)
        quad.addVertex(1f, 1f, 0f).setUv(1f, 1f)
        quad.addVertex(0f, 1f, 0f).setUv(0f, 1f)
        return quad.buildOrThrow()
    }
}

package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.client.node.vanilla.TextureTargetFramebuffer
import org.dimdev.dimcore.client.node.vanilla.VanillaNodeProgram

abstract class RenderNode(val id: ResourceLocation) {
    private var default: Pair<ShaderInstance, NodeProgram>? = null
    private var defaultComposite: Pair<ShaderInstance, NodeProgram>? = null

    var target: TextureTargetFramebuffer? = null
        private set

    val outShard = RenderStateShard.OutputStateShard("${id}_target", { framebuffer()?.bind() }, { MainFramebuffer.bind() })

    protected abstract val baseRenderType: RenderType

    val renderType: RenderType by lazy { NodeRenderType(this, baseRenderType) }

    open val attachments: Int = 1

    open val clearColor: FloatArray = floatArrayOf(0f, 0f, 0f, 0f)

    protected abstract fun shader(): ShaderInstance

    open fun uniforms(uniforms: NodeUniforms) {}

    protected open fun compositeShader(): ShaderInstance? = null

    open fun compositeUniforms(uniforms: NodeUniforms) {}

    fun buffer(source: MultiBufferSource): VertexConsumer = source.getBuffer(renderType)

    fun program(): NodeProgram? = if (NodeOverrides.bound(id)) NodeOverrides.program(id) else defaultProgram()

    fun compositeProgram(): NodeProgram? = if (NodeOverrides.compositeBound(id)) NodeOverrides.composite(id) else defaultCompositeProgram()

    fun framebuffer(): NodeFramebuffer? = NodeOverrides.framebuffer(id) ?: target

    fun compositeFramebuffer(): NodeFramebuffer = NodeOverrides.compositeFramebuffer(id) ?: MainFramebuffer

    private fun defaultProgram(): NodeProgram {
        val shader = shader()
        default?.let { (cached, program) -> if (cached === shader) return program else program.free() }
        return VanillaNodeProgram(shader, ::uniforms).also { default = shader to it }
    }

    private fun defaultCompositeProgram(): NodeProgram? {
        val shader = compositeShader() ?: return null
        defaultComposite?.let { (cached, program) -> if (cached === shader) return program else program.free() }
        return VanillaNodeProgram(shader, ::compositeUniforms).also { defaultComposite = shader to it }
    }

    fun reload(width: Int, height: Int) {
        target?.destroy()
        target = TextureTargetFramebuffer(width, height, attachments).also { it.target.setClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]) }
    }

    fun resize(width: Int, height: Int) {
        target?.resize(width, height)
    }

    fun clear() {
        target?.clear()
    }

    private class NodeRenderType(private val node: RenderNode, base: RenderType) : RenderType(
        node.id.toString(), base.format(), base.mode(), base.bufferSize(), base.affectsCrumbling(), true, base::setupRenderState, base::clearRenderState
    ) {
        override fun draw(mesh: MeshData) {
            val program = node.program() ?: return mesh.close()
            setupRenderState()
            program.bind()
            BufferUploader.draw(mesh)
            program.unbind()
            clearRenderState()
        }
    }
}

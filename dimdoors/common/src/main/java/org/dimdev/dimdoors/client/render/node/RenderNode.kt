package org.dimdev.dimdoors.client.render.node

import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimdoors.client.render.node.vanilla.MainFramebuffer
import org.dimdev.dimdoors.client.render.node.vanilla.TextureTargetFramebuffer
import org.dimdev.dimdoors.client.render.node.vanilla.VanillaNodeProgram
import java.util.EnumMap

abstract class RenderNode(val id: ResourceLocation, val stage: NodeStage, bufferSize: Int) {
    private val bytes = ByteBufferBuilder(bufferSize)
    private var builder: BufferBuilder? = null
    private val defaults = EnumMap<NodeProgramKind, Pair<ShaderInstance, NodeProgram>>(NodeProgramKind::class.java)

    var target: TextureTargetFramebuffer? = null
        private set

    val outShard = RenderStateShard.OutputStateShard("${id}_target", { framebuffer(NodeProgramKind.GEOMETRY)?.bind() }, { MainFramebuffer.bind() })

    private val compositeType: RenderType by lazy {
        RenderType.create(
            "${id}_composite", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.OutputStateShard("${id}_composite", { framebuffer(NodeProgramKind.COMPOSITE)?.bind() }, { MainFramebuffer.bind() }))
                .createCompositeState(false)
        )
    }

    abstract val renderType: RenderType

    protected abstract fun shader(kind: NodeProgramKind): ShaderInstance

    open fun uniforms(uniforms: NodeUniforms) {}

    fun buffer(): VertexConsumer = builder ?: BufferBuilder(bytes, renderType.mode(), renderType.format()).also { builder = it }

    fun program(kind: NodeProgramKind): NodeProgram = NodeOverrides.program(id, kind) ?: defaultProgram(kind)

    fun framebuffer(kind: NodeProgramKind): NodeFramebuffer? = NodeOverrides.framebuffer(id, kind) ?: when (kind) {
        NodeProgramKind.GEOMETRY -> target
        NodeProgramKind.COMPOSITE -> MainFramebuffer
    }

    private fun defaultProgram(kind: NodeProgramKind): NodeProgram {
        val shader = shader(kind)
        defaults[kind]?.let { (cached, program) -> if (cached === shader) return program else program.free() }
        return VanillaNodeProgram(shader, ::uniforms).also { defaults[kind] = shader to it }
    }

    fun reload(width: Int, height: Int) {
        target?.destroy()
        target = TextureTargetFramebuffer(width, height)
    }

    fun resize(width: Int, height: Int) {
        target?.resize(width, height)
    }

    fun discard() {
        builder?.build()?.close()
        builder = null
    }

    fun render(context: NodeRenderContext) {
        val mesh = builder?.build()
        builder = null
        mesh ?: return

        target?.clear()
        renderType.setupRenderState()
        program(NodeProgramKind.GEOMETRY).also {
            it.bind(context)
            BufferUploader.draw(mesh)
            it.unbind()
        }
        renderType.clearRenderState()

        val quad = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        quad.addVertex(0f, 0f, 0f).setUv(0f, 0f)
        quad.addVertex(1f, 0f, 0f).setUv(1f, 0f)
        quad.addVertex(1f, 1f, 0f).setUv(1f, 1f)
        quad.addVertex(0f, 1f, 0f).setUv(0f, 1f)

        compositeType.setupRenderState()
        program(NodeProgramKind.COMPOSITE).also {
            it.bind(context)
            BufferUploader.draw(quad.buildOrThrow())
            it.unbind()
        }
        compositeType.clearRenderState()
    }
}

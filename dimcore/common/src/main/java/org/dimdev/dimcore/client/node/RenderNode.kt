package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.client.node.vanilla.TextureTargetFramebuffer
import org.dimdev.dimcore.client.node.vanilla.VanillaNodeProgram
import java.util.*

abstract class RenderNode(val id: ResourceLocation, val stage: RenderStage, bufferSize: Int) {
    private val bytes = ByteBufferBuilder(bufferSize)
    private var builder: BufferBuilder? = null
    private val defaults = EnumMap<NodeProgramType, Pair<ShaderInstance, NodeProgram>>(NodeProgramType::class.java)

    var target: TextureTargetFramebuffer? = null
        private set

    val outShard = RenderStateShard.OutputStateShard("${id}_target", { framebuffer(NodeProgramType.GEOMETRY)?.bind() }, { MainFramebuffer.bind() })

    private val compositeType: RenderType by lazy<RenderType> {
        RenderType.create(
            "${id}_composite", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setOutputState(RenderStateShard.OutputStateShard("${id}_composite", { framebuffer(NodeProgramType.COMPOSITE)?.bind() }, { MainFramebuffer.bind() }))
                .createCompositeState(false)
        )
    }

    abstract val renderType: RenderType

    open val attachments: Int = 1

    val hasGeometry: Boolean get() = builder != null

    protected abstract fun shader(kind: NodeProgramType): ShaderInstance

    open fun uniforms(uniforms: NodeUniforms) {}

    abstract val shadowRenderType: RenderType

    fun buffer(source: MultiBufferSource): VertexConsumer =
        if (RenderNodes.shadowPass()) source.getBuffer(shadowRenderType)
        else builder ?: BufferBuilder(bytes, renderType.mode(), renderType.format()).also { builder = it }

    fun program(type: NodeProgramType): NodeProgram = NodeOverrides.program(id, type) ?: defaultProgram(type)

    fun framebuffer(kind: NodeProgramType): NodeFramebuffer? = NodeOverrides.framebuffer(id, kind) ?: when (kind) {
        NodeProgramType.GEOMETRY -> target
        NodeProgramType.COMPOSITE -> if (Minecraft.useShaderTransparency()) ItemEntityFramebuffer else MainFramebuffer
    }

    private fun defaultProgram(kind: NodeProgramType): NodeProgram {
        val shader = shader(kind)
        defaults[kind]?.let { (cached, program) -> if (cached === shader) return program else program.free() }
        return VanillaNodeProgram(shader, ::uniforms).also { defaults[kind] = shader to it }
    }

    fun reload(width: Int, height: Int) {
        target?.destroy()
        target = TextureTargetFramebuffer(width, height, attachments)
    }

    fun resize(width: Int, height: Int) {
        target?.resize(width, height)
    }

    fun render(context: NodeRenderContext) {
        val mesh = builder?.build()
        builder = null
        mesh ?: return

        target?.clear()
        renderType.setupRenderState()
        program(NodeProgramType.GEOMETRY).also {
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
        program(NodeProgramType.COMPOSITE).also {
            it.bind(context)
            BufferUploader.draw(quad.buildOrThrow())
            it.unbind()
        }
        compositeType.clearRenderState()
    }
}

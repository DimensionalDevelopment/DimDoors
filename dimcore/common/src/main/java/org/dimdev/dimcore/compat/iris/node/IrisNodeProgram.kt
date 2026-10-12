package org.dimdev.dimcore.compat.iris.node

import com.google.common.collect.ImmutableSet
import com.mojang.blaze3d.platform.GlStateManager
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.irisshaders.iris.gl.blending.AlphaTests
import net.irisshaders.iris.gl.blending.BlendModeOverride
import net.irisshaders.iris.gl.program.ProgramImages
import net.irisshaders.iris.gl.program.ProgramSamplers
import net.irisshaders.iris.gl.program.ProgramUniforms
import net.irisshaders.iris.gl.shader.GlShader
import net.irisshaders.iris.gl.state.FogMode
import net.irisshaders.iris.gl.state.ShaderAttributeInputs
import net.irisshaders.iris.pipeline.IrisRenderingPipeline
import net.irisshaders.iris.pipeline.transform.PatchShaderType
import net.irisshaders.iris.pipeline.transform.TransformPatcher
import net.irisshaders.iris.samplers.IrisSamplers
import net.irisshaders.iris.shaderpack.texture.TextureStage
import net.irisshaders.iris.uniforms.CommonUniforms
import net.irisshaders.iris.uniforms.builtin.BuiltinReplacementUniforms
import net.minecraft.client.Minecraft
import org.dimdev.dimcore.client.node.NodeProgram
import org.dimdev.dimcore.client.node.NodeRenderContext
import org.dimdev.dimcore.client.node.RenderNode
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import java.util.function.Supplier

class IrisNodeProgram private constructor(
    name: String,
    sources: Map<PatchShaderType, String>,
    format: VertexFormat,
    private val blend: BlendModeOverride?,
    pipeline: IrisRenderingPipeline,
    node: RenderNode,
    flipped: Supplier<ImmutableSet<Int>>,
    shadow: Boolean
) : NodeProgram {
    private val id = GL20.glCreateProgram()
    private val customUniforms = pipeline.customUniforms
    private val uniforms: ProgramUniforms
    private val samplers: ProgramSamplers
    private val images: ProgramImages
    private val locations: Map<String, Int>
    private val modelView = Matrix4f()
    private val projection = Matrix4f()

    init {
        format.elementAttributeNames.forEachIndexed { index, attribute ->
            GL20.glBindAttribLocation(id, index, attribute)
            GL20.glBindAttribLocation(id, index, "iris_$attribute")
        }
        val shaders = sources.map { (type, source) -> GlShader(type.glShaderType, "$name${type.extension}", source).also { GL20.glAttachShader(id, it.handle) } }
        GL20.glLinkProgram(id)
        shaders.forEach(GlShader::destroy)
        if (GL20.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            val log = GL20.glGetProgramInfoLog(id)
            GL20.glDeleteProgram(id)
            throw IllegalStateException("Failed to link $name: $log")
        }

        val uniformBuilder = ProgramUniforms.builder(name, id)
        val samplerBuilder = ProgramSamplers.builder(id, IrisSamplers.WORLD_RESERVED_TEXTURE_UNITS)
        val imageBuilder = ProgramImages.builder(id)
        CommonUniforms.addDynamicUniforms(uniformBuilder, FogMode.PER_VERTEX)
        customUniforms.assignTo(uniformBuilder)
        BuiltinReplacementUniforms.addBuiltinReplacementUniforms(uniformBuilder)
        node.uniforms(IrisUniforms(uniformBuilder, samplerBuilder))
        pipeline.addGbufferOrShadowSamplers(samplerBuilder, imageBuilder, flipped, shadow, true, true, true)
        customUniforms.mapholderToPass(uniformBuilder, this)
        uniforms = uniformBuilder.buildUniforms()
        samplers = samplerBuilder.build()
        images = imageBuilder.build()
        locations = REPLACEMENTS.associateWith { GL20.glGetUniformLocation(id, it) }.filterValues { it != -1 }
    }

    override fun bind(context: NodeRenderContext) {
        modelView.set(context.modelView)
        projection.set(context.projection)
        GlStateManager._glUseProgram(id)
        blend?.apply()

        MemoryStack.stackPush().use { stack ->
            locations["iris_ModelViewMat"]?.let { GL20.glUniformMatrix4fv(it, false, modelView.get(stack.mallocFloat(16))) }
            locations["iris_ModelViewMatInverse"]?.let { GL20.glUniformMatrix4fv(it, false, Matrix4f(modelView).invert().get(stack.mallocFloat(16))) }
            locations["iris_ProjMat"]?.let { GL20.glUniformMatrix4fv(it, false, projection.get(stack.mallocFloat(16))) }
            locations["iris_ProjMatInverse"]?.let { GL20.glUniformMatrix4fv(it, false, Matrix4f(projection).invert().get(stack.mallocFloat(16))) }
            locations["iris_TextureMat"]?.let { GL20.glUniformMatrix4fv(it, false, Matrix4f().get(stack.mallocFloat(16))) }
            locations["iris_NormalMat"]?.let { GL20.glUniformMatrix3fv(it, false, Matrix3f(modelView).invert().transpose().get(stack.mallocFloat(9))) }
        }
        locations["iris_ColorModulator"]?.let { GL20.glUniform4f(it, 1f, 1f, 1f, 1f) }
        locations["iris_ChunkOffset"]?.let { GL20.glUniform3f(it, 0f, 0f, 0f) }
        locations["iris_currentAlphaTest"]?.let { GL20.glUniform1f(it, AlphaTests.ONE_TENTH_ALPHA.reference()) }
        locations["iris_ScreenSize"]?.let { Minecraft.getInstance().window.let { window -> GL20.glUniform2f(it, window.width.toFloat(), window.height.toFloat()) } }
        locations["iris_FogStart"]?.let { GL20.glUniform1f(it, RenderSystem.getShaderFogStart()) }
        locations["iris_FogEnd"]?.let { GL20.glUniform1f(it, RenderSystem.getShaderFogEnd()) }
        locations["iris_FogColor"]?.let { RenderSystem.getShaderFogColor().let { color -> GL20.glUniform4f(it, color[0], color[1], color[2], color[3]) } }

        samplers.update()
        uniforms.update()
        customUniforms.push(this)
        images.update()
    }

    override fun unbind() {
        GlStateManager._glUseProgram(0)
        ProgramUniforms.clearActiveUniforms()
        ProgramSamplers.clearActiveSamplers()
        BlendModeOverride.restore()
    }

    override fun free() {
        uniforms.removeListeners()
        samplers.removeListeners()
        GL20.glDeleteProgram(id)
    }

    companion object {
        private val REPLACEMENTS = listOf(
            "iris_ModelViewMat", "iris_ModelViewMatInverse", "iris_ProjMat", "iris_ProjMatInverse", "iris_TextureMat", "iris_NormalMat",
            "iris_ColorModulator", "iris_ChunkOffset", "iris_currentAlphaTest", "iris_ScreenSize", "iris_FogStart", "iris_FogEnd", "iris_FogColor"
        )

        fun geometry(
            node: RenderNode, name: String, vertex: String, geometry: String?, tessControl: String?, tessEval: String?, fragment: String,
            blend: BlendModeOverride?, pipeline: IrisRenderingPipeline, flipped: Supplier<ImmutableSet<Int>>, shadow: Boolean
        ): IrisNodeProgram {
            val format = node.renderType.format()
            val sources = TransformPatcher.patchVanilla(
                name, vertex, geometry, tessControl, tessEval, fragment, AlphaTests.ONE_TENTH_ALPHA, false, false,
                ShaderAttributeInputs(format, false, false, false, false, false), pipeline.textureMap
            )
            return IrisNodeProgram(name, sources.filterValues { it != null }, format, blend, pipeline, node, flipped, shadow)
        }

        fun composite(
            node: RenderNode, name: String, vertex: String, geometry: String?, fragment: String,
            blend: BlendModeOverride?, pipeline: IrisRenderingPipeline, flipped: Supplier<ImmutableSet<Int>>, shadow: Boolean
        ): IrisNodeProgram {
            val sources = TransformPatcher.patchComposite(name, vertex, geometry, fragment, TextureStage.GBUFFERS_AND_SHADOW, pipeline.textureMap)
            return IrisNodeProgram(name, sources.filterValues { it != null }, DefaultVertexFormat.POSITION_TEX, blend, pipeline, node, flipped, shadow)
        }
    }
}

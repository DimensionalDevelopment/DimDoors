package org.dimdev.dimcore.compat.iris.node

import net.irisshaders.batchedentityrendering.impl.BlendingStateHolder
import net.irisshaders.batchedentityrendering.impl.TransparencyType
import net.irisshaders.iris.Iris
import net.irisshaders.iris.pipeline.IrisRenderingPipeline
import net.irisshaders.iris.shaderpack.programs.ProgramSet
import net.irisshaders.iris.shaderpack.programs.ProgramSource
import net.irisshaders.iris.shadows.ShadowRenderTargets
import net.irisshaders.iris.targets.RenderTargets
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.DimCore
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimcore.client.node.NodeOverrides
import org.dimdev.dimcore.client.node.RenderNode
import org.dimdev.dimcore.client.node.RenderNodes

class IrisNodeCompat(val pipeline: IrisRenderingPipeline, programSet: ProgramSet, private val renderTargets: RenderTargets) {
    private val programs = mutableMapOf<ResourceLocation, IrisNodeProgram>()
    private val compositePrograms = mutableMapOf<ResourceLocation, IrisNodeProgram>()
    private val compositeDrawBuffers = mutableMapOf<ResourceLocation, IntArray>()
    private val compositeFramebuffers = mutableMapOf<ResourceLocation, IrisNodeFramebuffer>()
    private val shadowPrograms = mutableMapOf<ResourceLocation, IrisNodeProgram>()
    private val shadowDrawBuffers = mutableMapOf<ResourceLocation, IntArray>()
    private val shadowFramebuffers = mutableMapOf<ResourceLocation, IrisNodeFramebuffer>()

    init {
        RenderNodes.external = { current() != null }
        RenderNodes.nodes.forEach { (it.renderType as BlendingStateHolder).setTransparencyType(TransparencyType.OPAQUE) }
        val ext = programSet.cast<ProgramSetExt>()
        RenderNodes.nodes.forEach { node ->
            listOf(GEOMETRY, SHADOW, COMPOSITE).forEach { pass ->
                runCatching { build(node, pass, ext.`dimensionalDoors$readProgram`(programName(node.id, pass))) }
                    .onFailure { DimCore.LOGGER.warn("Failed to build Iris program {}", programName(node.id, pass), it) }
            }
        }
    }

    private fun build(node: RenderNode, pass: String, source: ProgramSource?) {
        val name = programName(node.id, pass)
        val shadow = pass == SHADOW
        val flips = if (shadow) pipeline.flippedBeforeShadow else pipeline.flippedAfterPrepare
        val blend = source?.directives?.blendModeOverride?.orElse(null)

        val program = when {
            pass == COMPOSITE && source != null -> IrisNodeProgram.composite(
                node, name, source.vertexSource.get(), source.geometrySource.orElse(null), source.fragmentSource.get(), blend, pipeline, { flips }, false
            )
            pass == COMPOSITE -> IrisNodeProgram.composite(
                node, name, fallback(node.id, pass, "vsh") ?: return, fallback(node.id, pass, "gsh"), fallback(node.id, pass, "fsh") ?: return,
                null, pipeline, { flips }, false
            )
            source != null -> IrisNodeProgram.geometry(
                node, name, source.vertexSource.get(), source.geometrySource.orElse(null), source.tessControlSource.orElse(null),
                source.tessEvalSource.orElse(null), source.fragmentSource.get(), blend, pipeline, { flips }, shadow
            )
            else -> IrisNodeProgram.geometry(
                node, name, fallback(node.id, pass, "vsh") ?: return, fallback(node.id, pass, "gsh"), null,
                null, fallback(node.id, pass, "fsh") ?: return, null, pipeline, { flips }, shadow
            )
        }

        when (pass) {
            SHADOW -> {
                shadowPrograms[node.id] = program
                shadowDrawBuffers[node.id] = source?.directives?.drawBuffers ?: intArrayOf(0)
            }
            COMPOSITE -> {
                compositePrograms[node.id] = program
                compositeDrawBuffers[node.id] = source?.directives?.drawBuffers ?: intArrayOf(0)
            }
            else -> programs[node.id] = program
        }
    }

    private fun fallback(node: ResourceLocation, pass: String, extension: String): String? =
        Minecraft.getInstance().resourceManager
            .getResource(ResourceLocation.fromNamespaceAndPath(node.namespace, "shaders/iris/${node.path}_$pass.$extension"))
            .map { resource -> resource.openAsReader().use { it.readText() } }
            .orElse(null)

    fun bind() = RenderNodes.nodes.forEach { node ->
        programs[node.id]?.let { NodeOverrides.bind(node.id, it, null) } ?: NodeOverrides.unbind(node.id)
        compositePrograms[node.id]?.let {
            NodeOverrides.bindComposite(node.id, it, compositeFramebuffers.getOrPut(node.id) {
                IrisNodeFramebuffer(renderTargets.createGbufferFramebuffer(pipeline.flippedAfterPrepare, compositeDrawBuffers.getValue(node.id)))
            })
        }
    }

    fun bindShadow(targets: ShadowRenderTargets) = RenderNodes.nodes.forEach { node ->
        val program = shadowPrograms[node.id]
        NodeOverrides.bind(node.id, program, program?.let {
            shadowFramebuffers.getOrPut(node.id) { IrisNodeFramebuffer(targets.createShadowFramebuffer(pipeline.flippedBeforeShadow, shadowDrawBuffers.getValue(node.id))) }
        })
    }

    fun unbind() = RenderNodes.nodes.forEach { NodeOverrides.unbind(it.id) }

    fun clear() {
        unbind()
        programs.values.forEach(IrisNodeProgram::free)
        shadowPrograms.values.forEach(IrisNodeProgram::free)
        compositePrograms.values.forEach(IrisNodeProgram::free)
        programs.clear()
        shadowPrograms.clear()
        compositePrograms.clear()
        compositeDrawBuffers.clear()
        compositeFramebuffers.clear()
        shadowDrawBuffers.clear()
        shadowFramebuffers.clear()
    }

    companion object {
        private const val GEOMETRY = "geometry"
        private const val COMPOSITE = "composite"
        private const val SHADOW = "shadow"

        private var scannedNames: List<String>? = null

        fun programName(node: ResourceLocation, pass: String) = "${node.namespace}_${node.path}_$pass"

        private fun names(): List<String> = RenderNodes.nodes.flatMap { node ->
            listOf(GEOMETRY, SHADOW, COMPOSITE).flatMap { pass -> listOf("vsh", "tcs", "tes", "gsh", "fsh").map { "${programName(node.id, pass)}.$it" } }
        }

        @JvmStatic
        fun sourceNames(): List<String> = names().also { scannedNames = it }

        @JvmStatic
        fun reloadIfStale() {
            val scanned = scannedNames ?: return
            if (scanned == names()) return
            runCatching { Iris.reload() }.onFailure { DimCore.LOGGER.warn("Failed to reload Iris shader pack for render nodes", it) }
        }

        fun current(): IrisNodeCompat? = Iris.getPipelineManager().pipelineNullable?.castOrNull<IrisRenderingPipelineExt>()?.`dimensionalDoors$getNodeCompat`()
    }
}

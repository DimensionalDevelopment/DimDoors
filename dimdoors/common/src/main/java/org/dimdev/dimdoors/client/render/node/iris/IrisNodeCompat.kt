package org.dimdev.dimdoors.client.render.node.iris

import net.irisshaders.iris.Iris
import net.irisshaders.iris.pipeline.IrisRenderingPipeline
import net.irisshaders.iris.shaderpack.programs.ProgramSet
import net.irisshaders.iris.shaderpack.programs.ProgramSource
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.dimdev.dimdoors.client.render.node.NodeOverrides
import org.dimdev.dimdoors.client.render.node.NodeProgramKind
import org.dimdev.dimdoors.client.render.node.NodeStage
import org.dimdev.dimdoors.client.render.node.RenderNode
import org.dimdev.dimdoors.client.render.node.RenderNodes
import java.util.function.Supplier

class IrisNodeCompat(val pipeline: IrisRenderingPipeline, programSet: ProgramSet) {
    private val programs = mutableMapOf<Pair<ResourceLocation, NodeProgramKind>, IrisNodeProgram>()
    private val framebuffers = mutableMapOf<Pair<ResourceLocation, NodeProgramKind>, IrisNodeFramebuffer>()

    init {
        val ext = programSet.cast<ProgramSetExt>()
        RenderNodes.nodes.forEach { node ->
            NodeProgramKind.entries.forEach { kind ->
                runCatching { build(node, kind, ext.`dimensionalDoors$readProgram`(programName(node.id, kind))) }
                    .onFailure { DimensionalDoors.LOGGER.warn("Failed to build Iris program {}", programName(node.id, kind), it) }
            }
        }
    }

    private fun build(node: RenderNode, kind: NodeProgramKind, source: ProgramSource?) {
        if (node.stage == NodeStage.SHADOW) return
        val name = programName(node.id, kind)
        val flips = if (node.stage == NodeStage.TRANSLUCENT) pipeline.flippedAfterTranslucent else pipeline.flippedAfterPrepare
        val blend = source?.directives?.blendModeOverride?.orElse(null)

        when (kind) {
            NodeProgramKind.GEOMETRY -> {
                source ?: return
                programs[node.id to kind] = IrisNodeProgram.geometry(
                    node, name, source.vertexSource.get(), source.geometrySource.orElse(null), source.tessControlSource.orElse(null),
                    source.tessEvalSource.orElse(null), source.fragmentSource.get(), blend, pipeline, Supplier { flips }, false
                )
            }

            NodeProgramKind.COMPOSITE -> {
                val vertex = source?.vertexSource?.orElse(null) ?: fallback(node.id, kind, "vsh") ?: return
                val fragment = source?.fragmentSource?.orElse(null) ?: fallback(node.id, kind, "fsh") ?: return
                val drawBuffers = source?.directives?.drawBuffers ?: intArrayOf(0)
                programs[node.id to kind] = IrisNodeProgram.composite(node, name, vertex, source?.geometrySource?.orElse(null), fragment, blend, pipeline, Supplier { flips }, false)
                framebuffers[node.id to kind] = IrisNodeFramebuffer(pipeline.cast<IrisRenderingPipelineExt>().`dimensionalDoors$getRenderTargets`().createGbufferFramebuffer(flips, drawBuffers))
            }
        }
    }

    private fun fallback(node: ResourceLocation, kind: NodeProgramKind, extension: String): String? =
        Minecraft.getInstance().resourceManager
            .getResource(ResourceLocation.fromNamespaceAndPath(node.namespace, "shaders/iris/${node.path}_${kind.name.lowercase()}.$extension"))
            .map { resource -> resource.openAsReader().use { it.readText() } }
            .orElse(null)

    fun bind(stage: NodeStage) = RenderNodes.nodes.filter { it.stage == stage }.forEach { node ->
        NodeProgramKind.entries.forEach { kind ->
            programs[node.id to kind]?.let { NodeOverrides.bind(node.id, kind, it, framebuffers[node.id to kind]) }
        }
    }

    fun unbind(stage: NodeStage) = RenderNodes.nodes.filter { it.stage == stage }.forEach { node ->
        NodeProgramKind.entries.forEach { kind -> NodeOverrides.unbind(node.id, kind) }
    }

    fun clear() {
        programs.values.forEach(IrisNodeProgram::free)
        programs.clear()
        framebuffers.clear()
    }

    companion object {
        fun programName(node: ResourceLocation, kind: NodeProgramKind) = "${node.namespace}_${node.path}_${kind.name.lowercase()}"

        @JvmStatic
        fun sourceNames(): List<String> = RenderNodes.nodes.flatMap { node ->
            NodeProgramKind.entries.flatMap { kind -> listOf("vsh", "tcs", "tes", "gsh", "fsh").map { "${programName(node.id, kind)}.$it" } }
        }

        fun current(): IrisNodeCompat? = Iris.getPipelineManager().pipelineNullable?.castOrNull<IrisRenderingPipelineExt>()?.`dimensionalDoors$getNodeCompat`()
    }
}

package org.dimdev.dimdoors.client.render.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.server.packs.resources.ResourceManager

object RenderNodes {
    private val registered = mutableListOf<RenderNode>()

    val nodes: List<RenderNode> get() = registered

    var externalStages: () -> Boolean = { false }

    fun register(node: RenderNode) {
        registered += node
    }

    @JvmStatic
    fun reloadAll(manager: ResourceManager) {
        val main = Minecraft.getInstance().mainRenderTarget
        registered.forEach { it.reload(main.width, main.height) }
    }

    @JvmStatic
    fun resize(width: Int, height: Int) = registered.forEach { it.resize(width, height) }

    @JvmStatic
    fun renderLevel(partialTick: Float) {
        if (externalStages()) return
        render(NodeStage.GBUFFER, partialTick)
        render(NodeStage.TRANSLUCENT, partialTick)
    }

    @JvmStatic
    fun render(stage: NodeStage, partialTick: Float) {
        NodeEvents.beforeStage(stage)
        val projection = RenderSystem.getProjectionMatrix()
        val modelView = RenderSystem.getModelViewMatrix()
        registered.forEach {
            when {
                it.stage == stage -> it.render(NodeRenderContext(it, stage, projection, modelView, partialTick))
                stage == NodeStage.SHADOW -> it.discard()
            }
        }
        NodeEvents.afterStage(stage)
    }
}

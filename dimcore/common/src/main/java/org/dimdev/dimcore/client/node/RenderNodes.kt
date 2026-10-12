package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.server.packs.resources.ResourceManager
import org.dimdev.dimcore.client.node.vanilla.TextureTargetFramebuffer

object RenderNodes {
    private val registered = mutableListOf<RenderNode>()

    val nodes: List<RenderNode> get() = registered

    var externalStages: () -> Boolean = { false }

    var shadowPass: () -> Boolean = { false }

    var translucentLayer: TextureTargetFramebuffer? = null
        private set

    fun register(node: RenderNode) {
        registered += node
    }

    @JvmStatic
    fun reloadAll(manager: ResourceManager) {
        val main = Minecraft.getInstance().mainRenderTarget
        registered.forEach { it.reload(main.width, main.height) }
        translucentLayer?.destroy()
        translucentLayer = TextureTargetFramebuffer(main.width, main.height).also { it.target.setClearColor(0f, 0f, 0f, 0f) }
    }

    @JvmStatic
    fun resize(width: Int, height: Int) {
        registered.forEach { it.resize(width, height) }
        translucentLayer?.resize(width, height)
    }

    @JvmStatic
    fun renderLevel(partialTick: Float, translucent: Runnable) {
        if (externalStages()) return
        render(RenderStage.OPAQUE, partialTick)
        if (registered.any { it.stage == RenderStage.TRANSLUCENT && it.hasGeometry }) prepareTranslucentLayer(translucent)
        render(RenderStage.TRANSLUCENT, partialTick)
    }

    private fun prepareTranslucentLayer(translucent: Runnable) {
        val layer = translucentLayer ?: return
        layer.clear()
        if (Minecraft.useShaderTransparency()) return
        layer.target.copyDepthFrom(Minecraft.getInstance().mainRenderTarget)
        layer.bind()
        translucent.run()
        MainFramebuffer.bind()
    }

    @JvmStatic
    fun render(stage: RenderStage, partialTick: Float) {
        NodeEvents.beforeStage(stage)
        val projection = RenderSystem.getProjectionMatrix()
        val modelView = RenderSystem.getModelViewMatrix()
        registered.filter { it.stage == stage }.forEach { it.render(
            NodeRenderContext(
                it,
                stage,
                projection,
                modelView,
                partialTick
            )
        ) }
        NodeEvents.afterStage(stage)
    }
}

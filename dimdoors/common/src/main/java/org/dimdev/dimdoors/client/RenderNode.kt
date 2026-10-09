package org.dimdev.dimdoors.client

import com.mojang.blaze3d.pipeline.RenderTarget
import com.mojang.blaze3d.shaders.Uniform
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.ByteBufferBuilder
import com.mojang.blaze3d.vertex.VertexSorting
import net.irisshaders.iris.api.v0.IrisApi
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.PostChain
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import org.dimdev.dimcore.DimCore
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimdoors.DimensionalDoors
import org.joml.Matrix4f

open class RenderNode(val resource: ResourceLocation, bufferSize: Int, val targetName: String) {
    var chain: PostChain? = null
    var target: RenderTarget? = null
    val outShard = RenderStateShard.OutputStateShard("${targetName}_target", { target?.bindWrite(false) }, { main.bindWrite(false) })
    val buffers: MultiBufferSource.BufferSource = MultiBufferSource.immediate(ByteBufferBuilder(bufferSize))

    fun getUniform(stage: String, name: String): Uniform? =
        chain?.castOrNull<PostChainExt>()?.passes?.firstOrNull { it.name == stage }?.effect?.getUniform(name)

    open fun reload(manager: ResourceManager) {
        chain?.close()
        chain = runCatching { PostChain(Minecraft.getInstance().textureManager, manager, main, resource) }.onFailure { DimensionalDoors.LOGGER.warn("Failed to load shader: {}", resource, it) }.getOrNull()
        target = chain?.getTempTarget(targetName)
    }

    open fun process(partialTick: Float) {
        target?.clear(Minecraft.ON_OSX)
        target?.copyDepthFrom(main)
        buffers.endBatch()
        chain?.process(partialTick)
        target?.let { main.copyDepthFrom(it) }
        main.bindWrite(false)
    }

    companion object {
        private val main get() = Minecraft.getInstance().mainRenderTarget
        private val irisLoaded by lazy { DimCore.platform.isModLoaded("iris") }
        private val irisShaders get() = irisLoaded && IrisApi.getInstance().isShaderPackInUse()
        private val projection = Matrix4f()
        private val modelView = Matrix4f()
        private var sorting = VertexSorting.DISTANCE_TO_ORIGIN
        private var deferred = false

        private val nodes = mutableListOf<RenderNode>()

        @JvmStatic
        fun reloadAll(manager: ResourceManager) {
            val main = main
            val width = main.width
            val height = main.height
            nodes.forEach {
                it.reload(manager)
                it.chain?.resize(width, height)
            }
        }

        @JvmStatic
        fun resize(width: Int, height: Int) = nodes.forEach { it.chain?.resize(width, height) }

        @JvmStatic
        fun endLevel(partialTick: Float) {
            if (!irisShaders) return nodes.forEach { it.process(partialTick) }

            projection.set(RenderSystem.getProjectionMatrix())
            modelView.set(RenderSystem.getModelViewMatrix())
            sorting = RenderSystem.getVertexSorting()
            deferred = true
        }

        @JvmStatic
        fun afterLevel(partialTick: Float) {
            if (!deferred) return
            deferred = false
            RenderSystem.backupProjectionMatrix()
            RenderSystem.setProjectionMatrix(projection, sorting)
            val stack = RenderSystem.getModelViewStack()
            stack.pushMatrix().set(modelView)
            RenderSystem.applyModelViewMatrix()

            nodes.forEach { it.process(partialTick) }

            stack.popMatrix()
            RenderSystem.applyModelViewMatrix()
            RenderSystem.restoreProjectionMatrix()
        }

        fun register(renderNode: RenderNode) {
            this.nodes += renderNode
        }
    }
}
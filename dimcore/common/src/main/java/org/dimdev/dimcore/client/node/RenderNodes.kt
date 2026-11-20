package org.dimdev.dimcore.client.node

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.MeshData
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object RenderNodes {
    private val guiNodes = mutableMapOf<Item, RenderNode>()
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
        registered.forEach(RenderNode::composite)
        MainFramebuffer.bind()
    }

    @JvmStatic
    fun clear() {
        registered.forEach(RenderNode::clear)
        MainFramebuffer.bind()
    }

    fun quad(): MeshData {
        val quad = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX)
        quad.addVertex(0f, 0f, 0f).setUv(0f, 0f)
        quad.addVertex(1f, 0f, 0f).setUv(1f, 0f)
        quad.addVertex(1f, 1f, 0f).setUv(1f, 1f)
        quad.addVertex(0f, 1f, 0f).setUv(0f, 1f)
        return quad.buildOrThrow()
    }

    @JvmStatic
    fun compositeScissored() {
        registered.forEach {
            it.composite()
            if (it.drawn) it.target?.clear()
        }
        MainFramebuffer.bind()
    }

    @JvmStatic
    fun compositeGui(itemStack: ItemStack, graphics: GuiGraphics, x: Int, y: Int) {
        val node = guiNodes[itemStack.item] ?: return
        if (!node.drawn) return
        val pose = graphics.pose().last().pose()
        val left = (x + pose.m30()).toInt()
        val top = (y + pose.m31()).toInt()
        graphics.enableScissor(left, top, left + 16, top + 16)
        node.composite()
        node.target?.clear()
        MainFramebuffer.bind()
        graphics.disableScissor()
    }

    fun registerItem(item: Item, renderNode: RenderNode) {
        guiNodes[item] = renderNode
    }
}

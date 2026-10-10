package org.dimdev.dimdoors.client.render.node

import org.joml.Matrix4fc

data class NodeRenderContext(
    val node: RenderNode,
    val stage: NodeStage,
    val projection: Matrix4fc,
    val modelView: Matrix4fc,
    val partialTick: Float
)

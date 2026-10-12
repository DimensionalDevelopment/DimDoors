package org.dimdev.dimcore.client.node

import org.joml.Matrix4fc

data class NodeRenderContext(
    val node: RenderNode,
    val stage: RenderStage,
    val projection: Matrix4fc,
    val modelView: Matrix4fc,
    val partialTick: Float
)

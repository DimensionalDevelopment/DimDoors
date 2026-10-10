package org.dimdev.dimdoors.client.render.node

object NodeEvents {
    private val beforeListeners = mutableListOf<(NodeStage) -> Unit>()
    private val afterListeners = mutableListOf<(NodeStage) -> Unit>()

    fun onBeforeStage(listener: (NodeStage) -> Unit) {
        beforeListeners += listener
    }

    fun onAfterStage(listener: (NodeStage) -> Unit) {
        afterListeners += listener
    }

    @JvmStatic
    fun beforeStage(stage: NodeStage) = beforeListeners.forEach { it(stage) }

    @JvmStatic
    fun afterStage(stage: NodeStage) = afterListeners.forEach { it(stage) }
}

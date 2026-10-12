package org.dimdev.dimcore.client.node

object NodeEvents {
    private val beforeListeners = mutableListOf<(RenderStage) -> Unit>()
    private val afterListeners = mutableListOf<(RenderStage) -> Unit>()

    fun onBeforeStage(listener: (RenderStage) -> Unit) {
        beforeListeners += listener
    }

    fun onAfterStage(listener: (RenderStage) -> Unit) {
        afterListeners += listener
    }

    @JvmStatic
    fun beforeStage(stage: RenderStage) = beforeListeners.forEach { it(stage) }

    @JvmStatic
    fun afterStage(stage: RenderStage) = afterListeners.forEach { it(stage) }
}

package org.dimdev.dimdoors.client.render.node.iris

import org.dimdev.dimdoors.client.render.node.NodeEvents
import org.dimdev.dimdoors.client.render.node.RenderNodes

object IrisNodeEvents {
    fun register() {
        RenderNodes.externalStages = { IrisNodeCompat.current() != null }
        NodeEvents.onBeforeStage { IrisNodeCompat.current()?.bind(it) }
        NodeEvents.onAfterStage { IrisNodeCompat.current()?.unbind(it) }
    }
}

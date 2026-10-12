package org.dimdev.dimcore.compat.iris.node

import net.irisshaders.iris.api.v0.IrisApi
import org.dimdev.dimcore.client.node.NodeEvents
import org.dimdev.dimcore.client.node.RenderNodes

object IrisNodeEvents {
    fun register() {
        RenderNodes.externalStages = { IrisNodeCompat.current() != null }
        RenderNodes.shadowPass = { IrisApi.getInstance().isRenderingShadowPass }
        NodeEvents.onBeforeStage { IrisNodeCompat.current()?.bind(it) }
        NodeEvents.onAfterStage { IrisNodeCompat.current()?.unbind(it) }
    }
}

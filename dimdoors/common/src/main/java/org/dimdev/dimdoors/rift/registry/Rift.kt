package org.dimdev.dimdoors.rift.registry

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.rift.RiftUtils
import java.util.*

object Rift : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = RiftRegistry.instance.locationOf(id)

    override fun sourceGone(self: UUID, source: Vertex, location: Location?) {
        super.sourceGone(self, source, location)

        val self = source.providedLocation ?: return
        RiftUtils.runIfRiftAt(self) { rift ->
            if (source.type is Rift) {
                rift.handleSourceGone(location)
            }
        }
    }

    override fun targetGone(self: UUID, target: Vertex, location: Location?) {
        super.targetGone(self, target, location)

        val self = target.providedLocation ?: return
        RiftUtils.runIfRiftAt(self) { rift ->
            if (target.type is Rift && location != null) {
                rift.handleTargetGone(location)
            }
            rift.updateColor()
        }
    }

    override fun targetMoved(self: UUID, target: Vertex) {
        super.targetMoved(self, target)

        val self = target.providedLocation ?: return
        RiftUtils.runIfRiftAt(self) { rift ->
            if (target.type is Rift) {
                RiftRegistry.instance.locationOf(target.id)?.let(rift::handleSourceMoved)
            }
            rift.updateColor()
        }
    }

    override fun targetChanged(self: UUID, target: Vertex) {
        LOGGER.debug("Rift {} notified of target {} having changed. Updating color.", self, target)
        getLocation(self)?.let { RiftUtils.runIfRiftAt(it) { rift -> rift.updateColor() } }
    }

    override fun targetAdded(self: UUID, target: Vertex) {
        getLocation(self)?.let { RiftUtils.runIfRiftAt(it) { rift -> rift.updateColor() } }
    }

    override fun sourceAdded(self: UUID, source: Vertex) {
        getLocation(self)?.let { RiftUtils.runIfRiftAt(it) { rift -> rift.updateColor() } }
    }


    private val LOGGER: Logger = LogManager.getLogger()
}

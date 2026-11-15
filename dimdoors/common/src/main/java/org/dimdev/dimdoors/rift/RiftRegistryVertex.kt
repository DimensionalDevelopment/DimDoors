package org.dimdev.dimdoors.rift

import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.rift.registry.RegistryVertex
import org.dimdev.dimdoors.rift.registry.Vertex
import org.dimdev.dimdoors.rift.registry.SubSystem
import org.dimdev.dimdoors.api.util.Location
import java.util.*

abstract class RiftRegistryVertex<T: LocationProvider, R : RiftRegistry<T, R>, G: Rift<*, T, R>> : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = SubSystem.getInstance(riftRegistry)?.locationOf(id)?.providedLocation

    abstract fun getRift(self: UUID): G?

    override fun sourceGone(self: UUID, source: Vertex) {
        super.sourceGone(self, source)

        val t = SubSystem.getInstance(riftRegistry)?.locationOf(self) ?: return

        getRift(self)?.let { rift ->
            if (source.type is RiftRegistryVertex<*, *, *>) {
                rift.handleSourceGone(t)
            }
        }
    }

    override fun targetGone(self: UUID, target: Vertex) {
        super.targetGone(self, target)

        val t = SubSystem.getInstance(riftRegistry)?.locationOf(self) ?: return

        getRift(self)?.run {
            if(target.type is RiftRegistryVertex<*, *, *>) {
                handleTargetGone(t)

            }

            updateColor(t)
        }
    }

    override fun targetMoved(self: UUID, target: Vertex) {
        super.targetMoved(self, target)

        getLocation(self)?.let { RiftUtils.runIfRiftAt(it) { rift -> rift.updateColor() } }
    }

    override fun targetChanged(self: UUID, target: Vertex) {
        val t = SubSystem.getInstance(riftRegistry)?.locationOf(self) ?: return


//        logger.debug("Rift {} notified of target {} having changed. Updating color.", self, target)
        getRift(self)?.also { rift -> rift.updateColor(t) }
    }


    override fun targetAdded(self: UUID, target: Vertex) {
        val t = SubSystem.getInstance(riftRegistry)?.locationOf(self) ?: return

        getRift(self)?.also { rift -> rift.updateColor(t) }
    }

    override fun sourceAdded(self: UUID, source: Vertex) {
        val t = SubSystem.getInstance(riftRegistry)?.locationOf(self) ?: return

        getRift(self)?.also { rift -> rift.updateColor(t) }
    }

    abstract val riftRegistry: SubSystem.Type<R>
}
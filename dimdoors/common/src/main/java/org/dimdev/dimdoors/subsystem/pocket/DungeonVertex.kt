package org.dimdev.dimdoors.subsystem.pocket

import org.dimdev.dimdoors.pockets.type.Pocket
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex
import org.dimdev.dimdoors.util.Location
import java.util.*

object DungeonVertex : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = null

    override fun pocketChained(self: UUID, pocket: Pocket<*, *>) = DungeonRegistry.instance.addRoom(self, pocket)
}

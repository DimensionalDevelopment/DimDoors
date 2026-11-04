package org.dimdev.dimdoors.rift.registry

import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.world.pocket.type.Pocket
import java.util.*

object DungeonVertex : RegistryVertex() {
    override fun getLocation(id: UUID): Location? = null

    override fun pocketChained(self: UUID, pocket: Pocket<*, *>) = DungeonRegistry.instance.addRoom(self, pocket)
}

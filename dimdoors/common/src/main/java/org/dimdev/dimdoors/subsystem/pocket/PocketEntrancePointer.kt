package org.dimdev.dimdoors.subsystem.pocket

import java.util.*
import org.dimdev.dimdoors.subsystem.graph.RegistryVertex

object PocketEntrancePointer : RegistryVertex() {
    override fun getLocation(id: UUID) = PocketRegistry.instance.getPocketEntrance(id)
    override fun toString() = "PocketEntrancePointer"
}

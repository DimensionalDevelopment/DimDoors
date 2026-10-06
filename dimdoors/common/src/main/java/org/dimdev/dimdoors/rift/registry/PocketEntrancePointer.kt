package org.dimdev.dimdoors.rift.registry

import java.util.*

object PocketEntrancePointer : RegistryVertex() {
    override fun getLocation(id: UUID) = PocketRegistry.instance.getPocketEntrance(id)
    override fun toString() = "PocketEntrancePointer"
}

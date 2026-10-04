package org.dimdev.dimdoors.world.pocket.type.addon

interface AddonProvider {

    fun hasAddon(id: PocketAddonType<*, *>): Boolean

    fun <C : PocketAddon> addAddon(addon: C): Boolean
}

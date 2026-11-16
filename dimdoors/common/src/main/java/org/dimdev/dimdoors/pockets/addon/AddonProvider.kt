package org.dimdev.dimdoors.pockets.addon

interface AddonProvider {

    fun hasAddon(id: PocketAddonType<*, *>): Boolean

    fun <C : PocketAddon> addAddon(addon: C): Boolean
}

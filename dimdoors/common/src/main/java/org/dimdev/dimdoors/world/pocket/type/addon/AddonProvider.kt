package org.dimdev.dimdoors.world.pocket.type.addon

import net.minecraft.core.Holder

interface AddonProvider {

    fun hasAddon(id: Holder<out PocketAddonType>): Boolean

    fun <C : PocketAddon> addAddon(addon: C): Boolean
}

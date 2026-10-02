package org.dimdev.dimdoors.screen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.dimdev.dimcore.api.PlatformRegistry
import org.dimdev.dimdoors.DimensionalDoors.Companion.getSided

object ModScreenHandlerTypes : PlatformRegistry<MenuType<*>>(Registries.MENU, BuiltInRegistries.MENU, getSided()) {
    val TESSELATING_LOOM = create("tesselating", ::TessellatingContainer)

    fun <T : AbstractContainerMenu> create(name: String, menuType: MenuType.MenuSupplier<T>, flags: FeatureFlagSet = FeatureFlagSet.of()) = create(name) { MenuType(menuType, flags) }
}

package org.dimdev.dimdoors.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModScreenHandlerTypes {

    public static final MenuType<TessellatingContainer> TESSELATING_LOOM = register("tesselating", new MenuType<>(TessellatingContainer::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> menuType) {
        return DimensionalDoors.getSided().register(Registries.MENU, name, menuType);
    }

    public static void init() {}
}

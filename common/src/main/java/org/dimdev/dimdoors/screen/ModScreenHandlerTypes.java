package org.dimdev.dimdoors.screen;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;
import org.dimdev.dimdoors.util.Utils;

public class ModScreenHandlerTypes {

    public static final MenuType<TessellatingContainer> TESSELATING_LOOM = register("tesselating", new MenuType<>(TessellatingContainer::new, FeatureFlagSet.of()));

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> menuType) {
        return DimensionalDoors.getSided().register(Registries.MENU, name, menuType);
    }

    public static void init() {}
}

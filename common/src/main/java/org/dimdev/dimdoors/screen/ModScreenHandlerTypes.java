package org.dimdev.dimdoors.screen;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public class ModScreenHandlerTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<TessellatingContainer>> TESSELATING_LOOM = MENU_TYPES.register("tesselating", () -> new MenuType<>(TessellatingContainer::new, FeatureFlagSet.of()));

    public static void init() {
    MENU_TYPES.register();
    }
}

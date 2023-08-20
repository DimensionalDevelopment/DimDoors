package org.dimdev.dimdoors.screen;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModScreenHandlerTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.MENU);

	@SuppressWarnings("removal")
	public static final RegistrySupplier<MenuType<TessellatingContainer>> TESSELATING_LOOM = MENU_TYPES.register("tesselating", () -> MenuRegistry.of(TessellatingContainer::new));

	public static void init() {
		MENU_TYPES.register();
	}
}

package org.dimdev.dimdoors.screen;

import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.world.inventory.MenuType;

public class ModScreenHandlerTypes {

	@SuppressWarnings("removal")
	public static final MenuType<TesselatingScreenHandler> TESSELATING_LOOM = MenuRegistry.of(TesselatingScreenHandler::new);

	public static void init() {
	}
}

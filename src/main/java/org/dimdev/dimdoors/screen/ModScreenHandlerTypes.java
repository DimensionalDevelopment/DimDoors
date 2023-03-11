package org.dimdev.dimdoors.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModScreenHandlerTypes {

	public static final MenuType<TesselatingScreenHandler> TESSELATING_LOOM = ScreenHandlerRegistry.registerSimple(DimensionalDoors.id("tesselating_loom"), TesselatingScreenHandler::new);

	public static void init() {
	}
}

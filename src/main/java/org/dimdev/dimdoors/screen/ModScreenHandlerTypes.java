package org.dimdev.dimdoors.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerTypes {

	public static final ScreenHandlerType<TesselatingScreenHandler> TESSELATING_LOOM = ScreenHandlerRegistry.registerSimple(new Identifier("dimdoors", "tesselating_loom"), TesselatingScreenHandler::new);

	public static void init() {

	}
}

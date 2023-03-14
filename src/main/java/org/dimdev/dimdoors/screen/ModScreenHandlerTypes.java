package org.dimdev.dimdoors.screen;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.minecraft.world.inventory.MenuType;

import org.dimdev.dimdoors.Constants;

public class ModScreenHandlerTypes {
	private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Constants.MODID);

	public static final RegistryObject<MenuType<TesselatingScreenHandler>> TESSELATING_LOOM = MENU_TYPES.register("tesselating_loom", () -> new MenuType<>(TesselatingScreenHandler::new));

	public static void init(IEventBus bus) {
		MENU_TYPES.register(bus);
	}
}

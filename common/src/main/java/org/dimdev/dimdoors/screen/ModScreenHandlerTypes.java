package org.dimdev.dimdoors.screen;

import dev.architectury.registry.menu.MenuRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.block.entity.TesselatingLoomBlockEntity;

public class ModScreenHandlerTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.MENU);

	@SuppressWarnings("removal")
	public static final RegistrySupplier<MenuType<TessellatingContainer>> TESSELATING_LOOM = MENU_TYPES.register("tesselating", () -> MenuRegistry.ofExtended((id, inventory, buf) -> {
		if(Minecraft.getInstance().level != null) {
			if(Minecraft.getInstance().level.getBlockEntity(buf.readBlockPos()) instanceof TesselatingLoomBlockEntity tesselatingLoomBlockEntity) return tesselatingLoomBlockEntity.createMenu(id, inventory);
		}

		return null;
	}));

	public static void init() {
		MENU_TYPES.register();
	}
}

package org.dimdev.dimdoors.item;

import net.minecraft.item.Item;

public interface ItemExtensions {
	Item.Settings dimdoors_getSettings();

	static Item.Settings getSettings(Item item) {
		Item.Settings settings = ((ItemExtensions) item).dimdoors_getSettings();
		return ((SettingsExtensions) settings).clone();
	}

	interface SettingsExtensions extends Cloneable {
		Item.Settings clone();
	}
}

package org.dimdev.dimdoors.forge.item;

import net.minecraft.world.item.Item;

public interface ItemExtensions {
	Item.Properties dimdoors_getSettings();

	static Item.Properties getSettings(Item item) {
		Item.Properties settings = ((ItemExtensions) item).dimdoors_getSettings();
		return ((SettingsExtensions) settings).clone();
	}

	interface SettingsExtensions extends Cloneable {
		Item.Properties clone();
	}
}

package org.dimdev.dimdoors.forge.client;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ToolTipHelper {
	public static void processTranslation(List<Component> list, String key, Object... args) {
		if(I18n.exists(key)) {
			list.add(Component.translatable(key, args));
		} else {
			for (int i = 0; I18n.exists(key + i); i++) {
				list.add(Component.translatable(key + i, args));
			}
		}
	}
}

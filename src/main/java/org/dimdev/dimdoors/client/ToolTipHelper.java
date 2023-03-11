package org.dimdev.dimdoors.client;

import java.util.List;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ToolTipHelper {
	public static void processTranslation(List<Component> list, String key, Object... args) {
		if(I18n.exists(key)) {
			list.add(MutableComponent.create(new TranslatableContents(key, args)));
		} else {
			for (int i = 0; I18n.exists(key + i); i++) {
				list.add(MutableComponent.create(new TranslatableContents(key + i, args)));
			}
		}
	}
}

package org.dimdev.dimdoors.client;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.List;

public class ToolTipHelper {
	public static void processTranslation(List<Text> list, String key, Object... args) {
		if(I18n.hasTranslation(key)) {
			list.add(MutableText.of(new TranslatableTextContent(key, args)));
		} else {
			for (int i = 0; I18n.hasTranslation(key + i); i++) {
				list.add(MutableText.of(new TranslatableTextContent(key + i, args)));
			}
		}
	}
}

package org.dimdev.dimdoors.client;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class ToolTipHelper {
    public static void processTranslation(Consumer<Component> consumer, String key, Object... args) {
        if (I18n.exists(key)) {
            consumer.accept(Component.translatable(key, args));
        } else {
            for (int i = 0; I18n.exists(key + i); i++) {
                consumer.accept(Component.translatable(key + i, args));
            }
        }
    }
}

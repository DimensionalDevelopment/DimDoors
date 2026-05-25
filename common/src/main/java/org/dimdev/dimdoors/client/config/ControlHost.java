package org.dimdev.dimdoors.client.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public interface ControlHost {
    Font font();

    int rowHeight();

    int labelX();

    int valueX();

    int valueWidth();

    Component optionLabel(String key);

    String optionTooltip(String key);

    void addLabel(Component component, int x, int y, int color);

    void addWidget(AbstractWidget widget);

    void addTooltip(AbstractWidget widget, String optionKey);

    void rebuild();

    boolean validateAll();
}

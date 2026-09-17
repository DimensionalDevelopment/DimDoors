package org.dimdev.dimdoors.compat.imgui;

import foundry.imgui.api.ImGuiMC;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ImGuiScreen extends Screen {
    public ImGuiScreen() {
        super(Component.empty());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
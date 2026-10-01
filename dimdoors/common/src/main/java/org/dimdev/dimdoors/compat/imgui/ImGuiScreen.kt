package org.dimdev.dimdoors.compat.imgui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

class ImGuiScreen : Screen(Component.empty()) {
    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {}

    override fun isPauseScreen(): Boolean {
        return false
    }
}
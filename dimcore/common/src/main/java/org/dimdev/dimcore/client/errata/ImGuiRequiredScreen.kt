package org.dimdev.dimcore.client.errata

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import org.dimdev.dimcore.api.errata.session.SchemaMessages

class ImGuiRequiredScreen(title: Component) : Screen(title) {
    override fun init() {
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { onClose() }.bounds(width / 2 - 100, height / 2 + 10, 200, 20).build())
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(graphics, mouseX, mouseY, partialTick)
        graphics.drawCenteredString(font, title, width / 2, height / 2 - 30, 0xFFFFFF)
        graphics.drawCenteredString(font, SchemaMessages.NO_RENDERER, width / 2, height / 2 - 14, 0xAAAAAA)
    }

    override fun onClose() {
        ErrataClient.close()
        super.onClose()
    }

    object View : ErrataClient.View {
        override fun open() {
            ErrataClient.session?.let { Minecraft.getInstance().setScreen(ImGuiRequiredScreen(it.title)) }
        }

        override fun hide() {
            if (Minecraft.getInstance().screen is ImGuiRequiredScreen) Minecraft.getInstance().setScreen(null)
        }
    }
}

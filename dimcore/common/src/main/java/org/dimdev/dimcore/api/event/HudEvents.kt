package org.dimdev.dimcore.api.event

import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.player.Player
import org.dimdev.dimcore.api.util.SimpleEvent

object HudEvents {
    @JvmField val RENDER: SimpleEvent<(GuiGraphics, Player, DeltaTracker) -> Unit> = SimpleEvent.of { listeners -> { graphics, player, delta -> listeners.forEach { listener -> listener.invoke(graphics, player, delta) } } }
}

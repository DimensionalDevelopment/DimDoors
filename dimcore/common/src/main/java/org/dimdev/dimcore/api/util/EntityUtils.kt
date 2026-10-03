package org.dimdev.dimcore.api.util

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

object EntityUtils {
    @JvmStatic @JvmOverloads fun chat(entity: Entity?, text: Component, actionBar: Boolean = false) {
        if (entity is Player) entity.displayClientMessage(text, actionBar)
    }
}

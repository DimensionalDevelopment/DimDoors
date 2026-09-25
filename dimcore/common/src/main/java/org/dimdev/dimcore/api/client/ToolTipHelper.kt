package org.dimdev.dimcore.api.client

import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.Component

object ToolTipHelper {
    fun processTranslation(list: MutableList<Component>, key: String, vararg args: Any) {
        when {
            I18n.exists(key) -> {
                list.add(Component.translatable(key, *args))
            }

            else -> {
                generateSequence(0) { it + 1 }
                    .map { key + it }
                    .takeWhile { I18n.exists(it) }
                    .forEach { list.add(Component.translatable(it, *args)) }
            }
        }
    }
}

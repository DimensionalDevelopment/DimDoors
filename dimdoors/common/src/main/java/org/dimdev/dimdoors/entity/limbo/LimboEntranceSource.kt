package org.dimdev.dimdoors.entity.limbo

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.server.MinecraftServer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player
import org.dimdev.dimdoors.DimensionalDoors.Companion.config
import org.dimdev.dimdoors.api.util.translate

abstract class LimboEntranceSource {
    abstract fun getMessage(player: Player): Component

    fun broadcast(player: Player, server: MinecraftServer) {
        server.playerList.broadcastSystemMessage(this.getMessage(player), false)
    }

    class LimboDeathEntranceSource(private val damageSource: DamageSource) : LimboEntranceSource() {
        override fun getMessage(player: Player): Component {
            if (config.limboConfig.genericDeathMessages) {
                return this.damageSource.getLocalizedDeathMessage(player).copy()
                    .append("limbo.death.generic".translate())
            } else {
                val message = this.damageSource.getLocalizedDeathMessage(player).contents as TranslatableContents
                return "limbo.${message.key}".translate(*message.args)
            }
        }
    }

    companion object {
        @JvmStatic fun ofDamageSource(source: DamageSource): LimboDeathEntranceSource = LimboDeathEntranceSource(source)
    }
}

package org.dimdev.dimdoors.limbo

import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.player.Player
import org.dimdev.dimdoors.util.ext.translate
import java.util.*

enum class LimboExitReason : StringRepresentable {
    ETERNAL_FLUID,
    GENERIC,
    RIFT;

    override fun getSerializedName() = "limbo.exit.${this.name.lowercase(Locale.getDefault())}"

    fun broadcast(player: Player) {
        player.server?.playerList?.broadcastSystemMessage(
            getSerializedName().translate(player.gameProfile.name),
            false
        )
    }
}

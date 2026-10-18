package org.dimdev.dimcore.api.errata.session

import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.api.errata.subject.ConfigSubject

object ErrataPermissions {
    interface Hook {
        fun canOpen(player: ServerPlayer, subject: ConfigSubject): Boolean = player.hasPermissions(2)

        fun canWrite(player: ServerPlayer, subject: ConfigSubject, path: List<String>): Boolean = player.hasPermissions(2)

        fun canOverride(player: ServerPlayer, subject: ConfigSubject, path: List<String>): Boolean = player.hasPermissions(2)
    }

    var hook: Hook = object : Hook {}

    fun canOpen(player: ServerPlayer, subject: ConfigSubject) = hook.canOpen(player, subject)

    fun canWrite(player: ServerPlayer, subject: ConfigSubject, path: List<String>) = hook.canWrite(player, subject, path)

    fun canOverride(player: ServerPlayer, subject: ConfigSubject, path: List<String>) = hook.canOverride(player, subject, path)
}

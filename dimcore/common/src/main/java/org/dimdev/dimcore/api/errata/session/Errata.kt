package org.dimdev.dimcore.api.errata.session

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.PacketRegister
import org.dimdev.dimcore.api.errata.network.SessionAppliedS2C
import org.dimdev.dimcore.api.errata.network.SessionApplyC2S
import org.dimdev.dimcore.api.errata.network.SessionCloseC2S
import org.dimdev.dimcore.api.errata.network.SessionClosedS2C
import org.dimdev.dimcore.api.errata.network.SessionOpenS2C
import org.dimdev.dimcore.api.errata.network.SessionResolveC2S
import org.dimdev.dimcore.api.errata.network.SessionResolvedS2C
import org.dimdev.dimcore.api.errata.schema.ResolvedSchema
import org.dimdev.dimcore.api.errata.subject.ConfigSubject
import org.dimdev.dimcore.client.errata.ErrataClient
import java.util.*

object Errata {
    private val sessions = mutableMapOf<UUID, ErrataSession>()
    private val counters = mutableMapOf<UUID, Int>()

    fun init() {
        platform.onServerLevelTick(::tick)
        platform.onPlayerQuit {
            sessions.remove(it.uuid)
            counters.remove(it.uuid)
        }
        platform.onServerStopping {
            sessions.clear()
            counters.clear()
        }
    }

    fun registerPackets(register: PacketRegister) {
        register.registerClientPacket(SessionOpenS2C.TYPE, SessionOpenS2C.STREAM_CODEC) { ErrataClient.onOpen(it) }
        register.registerClientPacket(SessionAppliedS2C.TYPE, SessionAppliedS2C.STREAM_CODEC) { ErrataClient.onApplied(it) }
        register.registerClientPacket(SessionResolvedS2C.TYPE, SessionResolvedS2C.STREAM_CODEC) { ErrataClient.onResolved(it) }
        register.registerClientPacket(SessionClosedS2C.TYPE, SessionClosedS2C.STREAM_CODEC) { ErrataClient.onClosed(it) }

        register.registerServerPacket(SessionApplyC2S.TYPE, SessionApplyC2S.STREAM_CODEC) { packet, player -> player.server.execute { get(player, packet.id)?.apply(packet.value) }; null }
        register.registerServerPacket(SessionResolveC2S.TYPE, SessionResolveC2S.STREAM_CODEC) { packet, player -> player.server.execute { get(player, packet.id)?.resolve(packet.path, packet.source, packet.input) }; null }
        register.registerServerPacket(SessionCloseC2S.TYPE, SessionCloseC2S.STREAM_CODEC) { packet, player -> player.server.execute { if (get(player, packet.id) != null) sessions.remove(player.uuid) }; null }
    }

    fun open(player: ServerPlayer, subject: ConfigSubject): Boolean {
        if (!platform.canSend(player, SessionOpenS2C.TYPE)) return false.also { player.sendSystemMessage(SchemaMessages.CLIENT_MISSING) }
        if (!ErrataPermissions.canOpen(player, subject) || !subject.isValid(player)) return false

        val id = (counters[player.uuid] ?: 0) + 1
        counters[player.uuid] = id

        val session = ErrataSession(id, player, subject, ResolvedSchema.of(subject.schema(player)), subject.read(player))
        sessions[player.uuid] = session

        platform.sendPacket(player, SessionOpenS2C(id, subject.title, session.schema, session.options(), session.state()))
        return true
    }

    operator fun get(player: ServerPlayer, id: Int): ErrataSession? = sessions[player.uuid]?.takeIf { it.id == id && it.player === player }

    private fun tick(level: ServerLevel) {
        val iterator = sessions.values.iterator()

        while (iterator.hasNext()) {
            val session = iterator.next()
            val player = session.player

            if (player.level() !== level && !player.isRemoved) continue

            val reason = session.closeReason() ?: continue

            iterator.remove()
            level.server.playerList.getPlayer(player.uuid)?.let { platform.sendPacket(it, SessionClosedS2C(session.id, reason)) }
        }
    }
}

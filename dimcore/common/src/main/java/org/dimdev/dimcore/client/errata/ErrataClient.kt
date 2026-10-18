package org.dimdev.dimcore.client.errata

import net.minecraft.client.Minecraft
import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.errata.network.SessionAppliedS2C
import org.dimdev.dimcore.api.errata.network.SessionCloseC2S
import org.dimdev.dimcore.api.errata.network.SessionClosedS2C
import org.dimdev.dimcore.api.errata.network.SessionOpenS2C
import org.dimdev.dimcore.api.errata.network.SessionResolvedS2C

object ErrataClient {
    var session: ClientSession? = null
        private set

    var view: View = ImGuiRequiredScreen.View

    interface View {
        fun open()

        fun hide()
    }

    fun onOpen(packet: SessionOpenS2C) = Minecraft.getInstance().execute {
        session = ClientSession(packet.id, packet.title, packet.schema, packet.options, packet.state)
        view.open()
    }

    fun onApplied(packet: SessionAppliedS2C) = Minecraft.getInstance().execute { session?.takeIf { it.id == packet.id }?.applied(packet.state, packet.errors) }

    fun onResolved(packet: SessionResolvedS2C) = Minecraft.getInstance().execute { session?.takeIf { it.id == packet.id }?.resolved(packet.path, packet.entry, packet.input, packet.error) }

    fun onClosed(packet: SessionClosedS2C) = Minecraft.getInstance().execute {
        if (session?.id != packet.id) return@execute

        session = null
        view.hide()
        Minecraft.getInstance().player?.displayClientMessage(packet.reason, true)
    }

    fun close() {
        val session = session ?: return
        platform.sendPacket(SessionCloseC2S(session.id))
        this.session = null
    }
}

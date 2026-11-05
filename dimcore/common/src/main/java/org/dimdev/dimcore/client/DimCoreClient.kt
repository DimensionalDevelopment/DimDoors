package org.dimdev.dimcore.client

import org.dimdev.dimcore.DimCore.platform
import org.dimdev.dimcore.api.client.ClientReloadListenerProvider
import org.dimdev.dimcore.api.client.ClientReloadListenerRegister
import org.dimdev.dimcore.api.client.IClientSided
import org.dimdev.dimcore.api.client.ModClient
import org.dimdev.dimcore.api.client.SpriteOverrides
import org.dimdev.dimcore.client.errata.imgui.SchemaImGui
import org.dimdev.dimcore.client.node.RenderNodes

object DimCoreClient : ModClient<IClientSided<*>>, ClientReloadListenerProvider {

    override fun registerClientReloadListeners(register: ClientReloadListenerRegister) {
        register.register("render_nodes", RenderNodes::reloadAll)
        register.register("sprite_overrides", SpriteOverrides::reload)
    }

    override fun init(sided: IClientSided<*>) {
        if (platform.isModLoaded("imguimc")) SchemaImGui.init()
    }

    override val modId: String = "dimcore"
}
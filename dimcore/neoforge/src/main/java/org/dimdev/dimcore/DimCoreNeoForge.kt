package org.dimdev.dimcore

import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.transfer.NeoForgeTransfer
import org.dimdev.dimcore.util.DatapackLoader

@Mod("dimcore")
class DimCoreNeoForge(bus: IEventBus) {
    init {
        bus.addListener(DimCore.transfer.cast<NeoForgeTransfer>()::registerExposed)
        NeoForge.EVENT_BUS.addListener<AddReloadListenerEvent> { it.addListener(NeoforgeResourceLoader.Server(ResourceLocation.fromNamespaceAndPath(DimCore.MOD_ID, "datapack_loader"), DatapackLoader::reload)) }
    }
}

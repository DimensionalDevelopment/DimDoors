package org.dimdev.dimcore

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.transfer.NeoForgeTransfer

@Mod("dimcore")
class DimCoreNeoForge(bus: IEventBus) {
    init {
        bus.addListener(DimCore.transfer.cast<NeoForgeTransfer>()::registerExposed)
    }
}

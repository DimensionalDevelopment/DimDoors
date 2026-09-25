package org.dimdev.dimcore

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import org.dimdev.dimcore.api.cast
import org.dimdev.dimcore.transfer.NeoForgeTransfer
import java.util.function.Consumer

@Mod("dimcore")
class DimCoreNeoForge(bus: IEventBus) {
    init {
        bus.addListener(DimCore.transfer.cast<NeoForgeTransfer>()::registerExposed)
    }
}

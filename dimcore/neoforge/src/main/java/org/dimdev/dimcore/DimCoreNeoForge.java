package org.dimdev.dimcore;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.dimdev.dimcore.transfer.NeoForgeTransfer;

@Mod("dimcore")
public class DimCoreNeoForge {
    public DimCoreNeoForge(IEventBus bus) {
        bus.<RegisterCapabilitiesEvent>addListener(NeoForgeTransfer::registerExposed);
    }
}

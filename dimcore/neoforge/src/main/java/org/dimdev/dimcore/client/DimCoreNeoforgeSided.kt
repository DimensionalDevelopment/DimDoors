package org.dimdev.dimcore.client

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import org.dimdev.dimcore.DimCore

@Mod(value = DimCore.MOD_ID, dist = [Dist.CLIENT])
class DimCoreNeoForgeClient(bus: IEventBus) : NeoForgeClientSided<DimCoreNeoForgeClient, DimCoreClient>(bus, DimCoreClient) {
}
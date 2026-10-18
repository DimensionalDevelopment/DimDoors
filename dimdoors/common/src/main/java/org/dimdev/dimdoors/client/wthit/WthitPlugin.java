package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.block.entity.Rift;

// FIXME: is not actually client sided
public class WthitPlugin implements IWailaPlugin {
    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(EntranceRiftProvider.ID, true);
        registrar.addConfig(DetachedRiftProvider.ID, true);
    registrar.addComponent(EntranceRiftProvider.INSTANCE, TooltipPosition.BODY, Rift.class);
//    registrar.addComponent(DetachedRiftProvider.INSTANCE, TooltipPosition.BODY, DetachedRiftBlockEntity.class);
        registrar.addBlockData(DetachedRiftProvider.INSTANCE, DetachedRiftBlockEntity.class);
        registrar.addComponent(DetachedRiftProvider.INSTANCE, TooltipPosition.BODY, DetachedRiftBlockEntity.class);

    }
}

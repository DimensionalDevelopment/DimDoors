package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

public class WthitPlugin implements IWailaPlugin {
	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(EntranceRiftProvider.INSTANCE, TooltipPosition.TAIL, EntranceRiftBlockEntity.class);
	}
}

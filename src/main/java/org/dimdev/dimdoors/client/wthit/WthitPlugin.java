package org.dimdev.dimdoors.client.wthit;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlock;
import org.dimdev.dimdoors.block.entity.EntranceRiftBlockEntity;

// FIXME: is not actually client sided
public class WthitPlugin implements IWailaPlugin {
	@Override
	public void register(IRegistrar registrar) {
		registrar.registerComponentProvider(EntranceRiftProvider.INSTANCE, TooltipPosition.BODY, EntranceRiftBlockEntity.class);
	}
}

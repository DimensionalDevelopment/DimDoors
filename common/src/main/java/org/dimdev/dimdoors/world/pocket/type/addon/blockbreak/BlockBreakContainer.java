package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddonContainer;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

// TODO
public class BlockBreakContainer extends AutoSyncedAddonContainer<TryBlockBreakEventAddon> {
	public static ResourceLocation ID = DimensionalDoors.id("block_break_container");

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.BLOCK_BREAK_CONTAINER.get();
	}
}

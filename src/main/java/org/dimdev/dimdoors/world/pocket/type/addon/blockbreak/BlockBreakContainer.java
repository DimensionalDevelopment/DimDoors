package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.minecraft.util.Identifier;
import org.dimdev.dimdoors.Util;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddonContainer;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

// TODO
public class BlockBreakContainer extends AutoSyncedAddonContainer<TryBlockBreakEventAddon> {
	public static Identifier ID = Util.id("block_break_container");

	@Override
	public PocketAddonType<? extends PocketAddon> getType() {
		return PocketAddonType.BLOCK_BREAK_CONTAINER;
	}
}

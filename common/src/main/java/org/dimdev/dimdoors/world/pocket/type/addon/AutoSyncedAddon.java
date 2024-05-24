package org.dimdev.dimdoors.world.pocket.type.addon;

public interface AutoSyncedAddon extends PocketAddon {
	@Override
	default boolean syncs() {
		return true;
	}
}

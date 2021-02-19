package org.dimdev.dimdoors.world.pocket.type.addon.blockbreak;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.ContainedAddon;

// TODO
public interface TryBlockBreakEventAddon extends AttackBlockCallback, PlayerBlockBreakEvents.Before, ContainedAddon, AutoSyncedAddon {
}

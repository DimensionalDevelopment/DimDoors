package org.dimdev.dimdoors.forge.world.pocket.type.addon.blockbreak;

import dev.architectury.event.events.common.InteractionEvent;
import org.dimdev.dimdoors.forge.world.pocket.type.addon.AutoSyncedAddon;
import org.dimdev.dimdoors.forge.world.pocket.type.addon.ContainedAddon;

// TODO
public interface TryBlockBreakEventAddon extends InteractionEvent.LeftClickBlock, /*PlayerBlockBreakEvents.Before, TODO: Fix this bit*/ContainedAddon, AutoSyncedAddon {
}

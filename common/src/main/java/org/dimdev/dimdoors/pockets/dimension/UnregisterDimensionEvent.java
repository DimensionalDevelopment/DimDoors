package org.dimdev.dimdoors.pockets.dimension;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

/**
 * Fires when a dimension/level is about to be unregistered by Infiniverse.<br>
 * This event fires on {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS} and is not cancellable.<br>
 */

@FunctionalInterface
public interface UnregisterDimensionEvent {
    public Event<UnregisterDimensionEvent> EVENT = EventFactory.of(listeners -> (UnregisterDimensionEvent) level -> listeners.stream().allMatch(listener -> listener.shouldUnregister(level)));

    public boolean shouldUnregister(ServerLevel level);
}
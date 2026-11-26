package org.dimdev.dimcore.api.ext;

import org.dimdev.dimcore.api.event.PlayerTeleportEvents;

import java.util.function.BooleanSupplier;

public interface ServerPlayerExt {
    boolean dimcore$teleportAs(PlayerTeleportEvents.Cause cause, BooleanSupplier teleport);
}

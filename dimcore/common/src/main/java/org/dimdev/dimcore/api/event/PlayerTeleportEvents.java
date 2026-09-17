package org.dimdev.dimcore.api.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimcore.api.util.SimpleEvent;

public final class PlayerTeleportEvents {
    /** Fires before the move, so listeners can send state that should arrive before the client gets there. */
    public static final SimpleEvent<TriConsumer<ServerPlayer, ServerLevel, Vec3>> BEFORE = event();

    /** Fires once the player is actually there. */
    public static final SimpleEvent<TriConsumer<ServerPlayer, ServerLevel, Vec3>> AFTER = event();

    private PlayerTeleportEvents() {
    }

    private static SimpleEvent<TriConsumer<ServerPlayer, ServerLevel, Vec3>> event() {
        return SimpleEvent.of(listeners -> (player, level, pos) -> listeners.forEach(listener -> listener.accept(player, level, pos)));
    }
}

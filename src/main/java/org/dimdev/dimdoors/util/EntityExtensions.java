package org.dimdev.dimdoors.util;

import net.minecraft.world.TeleportTarget;

public interface EntityExtensions {
    boolean dimdoors_isReadyToTeleport();

    void dimdoors_setReadyToTeleport(boolean value);

    void dimdoors_setTeleportTarget(TeleportTarget target);
}

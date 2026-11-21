package org.dimdev.dimdoors.util;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerLevelAccess;

public interface GrindstoneAccessor {
    Container resultSlots();

    ContainerLevelAccess getAccess();
}

package org.dimdev.dimdoors.fabric.api.util;

import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;

public class TeleportUtilImpl {
    public static <E extends Entity> E teleport(E entity, ServerLevel world, PortalInfo portalInfo) {
        return FabricDimensions.teleport(entity, world, portalInfo);
    }
}
package org.dimdev.dimdoors.api.util.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TeleportUtilImpl {
    public static <E extends Entity> E teleport(E entity, ServerLevel world, PortalInfo portalInfo) {return (E) entity.changeDimension(world, new ITeleporter() {
        @Override
        public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
            return portalInfo;
        }
    });
    }
}

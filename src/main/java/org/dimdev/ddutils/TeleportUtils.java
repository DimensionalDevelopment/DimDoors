package org.dimdev.ddutils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;

public final class TeleportUtils {

    // <editor-fold defaultstate="collapsed" desc="Helper functions">
    private static final Field invulnerableDimensionChange;
    private static final Field thrower;
    private static final Field enteredNetherPosition;
    private static final Method captureCurrentPosition;
    private static final Method copyDataFromOld;
    private static final Method searchForOtherItemsNearby;
    private static final Method updateplayers;

    static {
        try {
            invulnerableDimensionChange = MCPReflection.getMCPField(EntityPlayerMP.class, "invulnerableDimensionChange", "field_184851_cj");
            thrower = MCPReflection.getMCPField(EntityThrowable.class, "thrower", "field_70192_c");
            enteredNetherPosition = MCPReflection.getMCPField(EntityPlayerMP.class, "enteredNetherPosition", "field_193110_cw");
            captureCurrentPosition = MCPReflection.getMCPMethod(NetHandlerPlayServer.class, "captureCurrentPosition", "func_184342_d");
            copyDataFromOld = MCPReflection.getMCPMethod(Entity.class, "copyDataFromOld", "func_180432_n", Entity.class);
            searchForOtherItemsNearby = MCPReflection.getMCPMethod(EntityItem.class, "searchForOtherItemsNearby", "func_85054_d");
            updateplayers = MCPReflection.getMCPMethod(DragonFightManager.class, "updatePlayers", "func_186100_j");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setInvulnerableDimensionChange(EntityPlayerMP player, boolean value) {
        try {
            invulnerableDimensionChange.set(player, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setThrower(EntityThrowable entity, EntityLivingBase value) {
        try {
            thrower.set(entity, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setEnteredNetherPosition(EntityPlayerMP player, Vec3d value) {
        try {
            enteredNetherPosition.set(player, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void captureCurrentPosition(NetHandlerPlayServer connection) {
        try {
            captureCurrentPosition.invoke(connection);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyDataFromOld(Entity newEntity, Entity oldEntity) {
        try {
            copyDataFromOld.invoke(newEntity, oldEntity);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void searchForOtherItemsNearby(EntityItem item) {
        try {
            searchForOtherItemsNearby.invoke(item);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private static void updateplayers(DragonFightManager dragonFightManager) {
        try {
            updateplayers.invoke(dragonFightManager);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public static Entity teleport(Entity entity, Location location) {
        return teleport(entity, location, entity.rotationYaw, entity.rotationPitch);
    }

    public static Entity teleport(Entity entity, Location location, float yaw, float pitch) {
        return teleport(entity, location.dim, location.getPos().getX() + .5, location.getPos().getY(), location.getPos().getZ() + .5, yaw, pitch);
    }

    public static Entity teleport(Entity entity, BlockPos pos, float yaw, float pitch) {
        return teleport(entity, WorldUtils.getDim(entity.getEntityWorld()), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
    }

    public static Entity teleport(Entity entity, double x, double y, double z, float yaw, float pitch) {
        return teleport(entity, WorldUtils.getDim(entity.getEntityWorld()), x, y, z, yaw, pitch);
    }

    // </editor-fold>

    /**
     * Teleports any kind of entity to any dimension, position, and pitch/yaw. Unlike the vanilla code, this doesn't do any
     * actions such as creating nether portals and end platforms or showing the credits when leaving the end, and ignores the
     * world moveFactor (ex. 8 in the nether). This code is safe to call from a block collision event.
     */
    public static Entity teleport(Entity entity, int newDimension, double x, double y, double z, float yaw, float pitch) {
        if (entity instanceof FakePlayer) return entity;
        if (entity.world.isRemote || entity.isDead) return null; // dead means inactive, not a dead player

        float adjustedYaw = MathHelper.wrapDegrees(yaw);
        float adjustedPitch = MathHelper.wrapDegrees(pitch);
        entity.dismountRidingEntity(); // TODO: would be nice to teleport them too
        entity.removePassengers();

        int oldDimension = entity.dimension;
        // int newDimension = dim;

        if (entity instanceof EntityPlayerMP) {
            // Workaround for https://bugs.mojang.com/browse/MC-123364. Disables player-in-block checking, but doesn't seem
            // to make the player actually noclip.
            entity.noClip = true;
            setInvulnerableDimensionChange((EntityPlayerMP) entity, true);
        }

        if (oldDimension == newDimension) { // Based on CommandTeleport.doTeleport
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                player.connection.setPlayerLocation(x, y, z, adjustedYaw, adjustedPitch, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class));
                // Fix for https://bugs.mojang.com/browse/MC-98153. See this comment: https://bugs.mojang.com/browse/MC-98153#comment-411524
                captureCurrentPosition(player.connection);
            } else {
                entity.setLocationAndAngles(x, y, z, adjustedYaw, adjustedPitch);
            }

            entity.setRotationYawHead(adjustedYaw);

            return entity;
        } else {
            entity.changeDimension(newDimension, (w, e, newYaw) -> e.moveToBlockPosAndAngles(new BlockPos(x, y, z), adjustedYaw, adjustedPitch));
            return entity;
        }
    }
}

package org.dimdev.ddutils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

public final class TeleportUtils {

    public static Entity teleport(Entity entity, Location location) {
        return teleport(entity, location, entity.rotationYaw, entity.rotationPitch);
    }

    public static Entity teleport(Entity entity, Location location, float yaw, float pitch) {
        return teleport(entity, location.getDim(), location.getPos().getX() + .5, location.getPos().getY(), location.getPos().getZ() + .5, yaw, pitch);
    }

    public static Entity teleport(Entity entity, BlockPos pos, float yaw, float pitch) {
        return teleport(entity, WorldUtils.getDim(entity.getEntityWorld()), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, yaw, pitch);
    }

    public static Entity teleport(Entity entity, double x, double y, double z, float yaw, float pitch) {
        return teleport(entity, WorldUtils.getDim(entity.getEntityWorld()), x, y, z, yaw, pitch);
    }

    public static Entity teleport(Entity entity, int newDimension, double x, double y, double z, float yaw, float pitch) {
        if (entity.world.isRemote || !(entity.world instanceof WorldServer) || entity.isDead) return entity; // dead means inactive, not a dead player

        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.wrapDegrees(pitch);

        entity.dismountRidingEntity(); // TODO: would be nice to teleport them too
        entity.removePassengers();

        int oldDimension = entity.dimension;
        // int newDimension = dim;

        if (entity instanceof EntityPlayerMP) {
            entity.noClip = true;
        }

        if (oldDimension == newDimension) { // Based on CommandTeleport.doTeleport
            if (entity instanceof EntityPlayerMP) {
                ((EntityPlayerMP) entity).connection.setPlayerLocation(
                        x,
                        y,
                        z,
                        yaw,
                        pitch,
                        EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class));
            } else {
                entity.setLocationAndAngles(x, y, z, yaw, pitch);
            }
            entity.setRotationYawHead(yaw);
            return entity;
        } else { // Based on EntityUtils.changeDimension
            MinecraftServer server = entity.getServer();
            WorldServer oldServer = server.getWorld(oldDimension);
            WorldServer newServer = server.getWorld(newDimension);

            // Allow other mods to cancel the event
            if (!ForgeHooks.onTravelToDimension(entity, newDimension)) return entity;

            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                try {
                    Field invulnerableDimensionChange = MCPReflection.getMCPField(EntityPlayerMP.class, "invulnerableDimensionChange", "field_184851_cj");
                    invulnerableDimensionChange.setBoolean(player, true); // Prevent Minecraft from cancelling the position change being too big if the player is not in creative
                } catch (NoSuchFieldException|IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                // player.enteredNetherPosition = null;
                player.dimension = newDimension;
                player.connection.sendPacket(new SPacketRespawn(player.dimension, newServer.getDifficulty(), newServer.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));

                // Remove from old world
                player.mcServer.getPlayerList().updatePermissionLevel(player);
                oldServer.removeEntityDangerously(player);
                player.isDead = false;

                // Move to new world
                oldServer.profiler.startSection("moving");
                player.setLocationAndAngles(x, y, z, yaw, pitch); // TODO: clamp to world border or -29999872, 29999872 like in original code?
                if (entity.isEntityAlive()) oldServer.updateEntityWithOptionalForce(entity, false);
                oldServer.profiler.endSection();

                oldServer.profiler.startSection("placing");
                newServer.spawnEntity(player);
                newServer.updateEntityWithOptionalForce(player, false);
                oldServer.profiler.endSection();
                player.setWorld(newServer);

                // Sync the player
                player.mcServer.getPlayerList().preparePlayer(player, oldServer);
                player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                player.interactionManager.setWorld(newServer);
                player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
                player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newServer);
                player.mcServer.getPlayerList().syncPlayerInventory(player);
                for (PotionEffect potioneffect : player.getActivePotionEffects()) {
                    player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
                }

                FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimension, newDimension);

                //player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false)); // TODO

                //player.prevBlockpos = null; // For frost walk. Is this needed? What about other fields?
                /*player.lastExperience = -1;
                player.lastHealth = -1.0F;
                player.lastFoodLevel = -1;*/
                return entity;
            } else {
                entity.world.profiler.startSection("changeDimension");
                entity.dimension = newDimension;
                entity.world.removeEntity(entity);
                entity.isDead = false;

                entity.world.profiler.startSection("reposition");
                oldServer.updateEntityWithOptionalForce(entity, false);

                entity.world.profiler.endStartSection("reloading");
                Entity newEntity = EntityList.newEntity(entity.getClass(), newServer);

                if (newEntity != null) {
                    try {
                        Method copyDataFromOld = MCPReflection.getMCPMethod(Entity.class,"copyDataFromOld", "func_180432_n", Entity.class);
                        copyDataFromOld.invoke(newEntity, entity);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    newEntity.setPositionAndRotation(x, y, z, yaw, pitch);
                    boolean oldForceSpawn = newEntity.forceSpawn;
                    newEntity.forceSpawn = true;
                    newServer.spawnEntity(newEntity);
                    newEntity.forceSpawn = oldForceSpawn;
                    newServer.updateEntityWithOptionalForce(newEntity, false);
                }

                entity.isDead = true;
                entity.world.profiler.endSection();

                oldServer.resetUpdateEntityTick();
                newServer.resetUpdateEntityTick();
                entity.world.profiler.endSection();

                return newEntity;
            }
        }
    }
}

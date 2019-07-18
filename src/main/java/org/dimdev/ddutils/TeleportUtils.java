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
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldServer;
import net.minecraft.world.end.DragonFightManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.FakePlayer;
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

        yaw = MathHelper.wrapDegrees(yaw);
        pitch = MathHelper.wrapDegrees(pitch);

        entity.dismountRidingEntity(); // TODO: would be nice to teleport them too
        entity.removePassengers();

        int oldDimension = entity.dimension;
        // int newDimension = dim;

        if (entity instanceof EntityPlayerMP) {
            // Workaround for https://bugs.mojang.com/browse/MC-123364. Disables player-in-block checking, but doesn't seem
            // to make the player actually noclip.
            entity.noClip = true;

            // Prevent Minecraft from cancelling the position change being too big if the player is not in creative
            // This has to be done when the teleport is done from the player moved function (so any block collision event too)
            // Not doing this will cause the player to be invisible for others.
            setInvulnerableDimensionChange((EntityPlayerMP) entity, true);
        }

        if (oldDimension == newDimension) { // Based on CommandTeleport.doTeleport
            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                player.connection.setPlayerLocation(x, y, z, yaw, pitch, EnumSet.noneOf(SPacketPlayerPosLook.EnumFlags.class));
                // Fix for https://bugs.mojang.com/browse/MC-98153. See this comment: https://bugs.mojang.com/browse/MC-98153#comment-411524
                captureCurrentPosition(player.connection);
            } else {
                entity.setLocationAndAngles(x, y, z, yaw, pitch);
            }
            entity.setRotationYawHead(yaw);

            return entity;
        } else { // Based on Entity.changeDimension
            MinecraftServer server = entity.getServer();
            WorldServer oldWorld = server.getWorld(oldDimension);
            WorldServer newWorld = server.getWorld(newDimension);

            // Allow other mods to cancel the event
            if (!ForgeHooks.onTravelToDimension(entity, newDimension)) return entity;

            if (entity instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) entity;

                // Setting this field seems to be useful for advancments. Adjusted dimension checks for non-vanilla
                // dimension support (entering the nether from any dimension should trigger it now).
                if (newDimension == -1) {
                    setEnteredNetherPosition(player, new Vec3d(player.posX, player.posY, player.posZ));
                } else if (oldDimension != -1 && newDimension != 0) {
                    setEnteredNetherPosition(player, null);
                }

                // Send respawn packets to the player
                player.dimension = newDimension;
                player.connection.sendPacket(new SPacketRespawn(player.dimension, newWorld.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
                player.server.getPlayerList().updatePermissionLevel(player); // Sends an SPacketEntityStatus

                // Remove player entity from the old world
                oldWorld.removeEntityDangerously(player);

                // Move the player entity to new world
                // We can't use PlayerList.transferEntityToWorld since for newDimension = 1, that would first teleport the
                // player to the dimension's spawn before quickly teleporting the player to the correct position. Unlike the vanilla
                // code, we don't use the world provider's moveFactor (ex. 8 blocks in the nether) and don't clip to the
                // world border.
                player.isDead = false;
                oldWorld.profiler.startSection("moving");
                player.setLocationAndAngles(x, y, z, yaw, pitch);
                // PlayerList.transferEntityToWorld does this for some reason when teleporting to the end, but it doesn't
                // make any sense (without it, there seems to be some flickering between two positions):
                if (entity.isEntityAlive()) oldWorld.updateEntityWithOptionalForce(entity, false);
                oldWorld.profiler.endSection();

                oldWorld.profiler.startSection("placing");
                newWorld.spawnEntity(player);
                newWorld.updateEntityWithOptionalForce(player, false);
                oldWorld.profiler.endSection();
                player.setWorld(newWorld);

                // Sync the player
                player.server.getPlayerList().preparePlayer(player, oldWorld);
                player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
                // Fix for https://bugs.mojang.com/browse/MC-98153. See this comment: https://bugs.mojang.com/browse/MC-98153#comment-411524
                captureCurrentPosition(player.connection);
                player.interactionManager.setWorld(newWorld);
                player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
                player.server.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
                player.server.getPlayerList().syncPlayerInventory(player);
                for (PotionEffect potioneffect : player.getActivePotionEffects()) {
                    player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
                }

                // Force WorldProviderEnd to check if end dragon bars should be removed. Duplicate end dragon bars even
                // happen when leaving the end using an end portal while the dragon is alive, so this might be a vanilla
                // or Forge bug (maybe the world is unloaded before checking players?). In vanilla, updateplayers is normally
                // called every second.
                if (oldWorld.provider instanceof WorldProviderEnd) {
                    DragonFightManager dragonFightManager = ((WorldProviderEnd) oldWorld.provider).getDragonFightManager();
                    updateplayers(dragonFightManager);
                }

                // Vanilla also plays SoundEvents.BLOCK_PORTAL_TRAVEL, we won't do this.

                FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimension, newDimension);

                //player.prevBlockpos = null; // For frost walk. Is this needed? What about other fields?
                /*player.lastExperience = -1;
                player.lastHealth = -1.0F;
                player.lastFoodLevel = -1;*/

                return entity;
            } else {
                if (entity instanceof EntityMinecartContainer) ((EntityMinecartContainer) entity).dropContentsWhenDead = false;
                if (entity instanceof EntityEnderPearl) setThrower((EntityThrowable) entity, null); // Otherwise the player will be teleported to the hit position but in the same dimension

                entity.world.profiler.startSection("changeDimension");
                entity.dimension = newDimension;
                entity.world.removeEntity(entity);
                entity.isDead = false;

                entity.world.profiler.startSection("reposition");
                oldWorld.updateEntityWithOptionalForce(entity, false);

                entity.world.profiler.endStartSection("reloading");
                Entity newEntity = EntityList.newEntity(entity.getClass(), newWorld);

                if (newEntity != null) {
                    copyDataFromOld(newEntity, entity);
                    newEntity.setPositionAndRotation(x, y, z, yaw, pitch);
                    boolean oldForceSpawn = newEntity.forceSpawn;
                    newEntity.forceSpawn = true;
                    newWorld.spawnEntity(newEntity);
                    newEntity.forceSpawn = oldForceSpawn;
                    newWorld.updateEntityWithOptionalForce(newEntity, false);
                }

                entity.isDead = true;
                entity.world.profiler.endSection();

                oldWorld.resetUpdateEntityTick();
                newWorld.resetUpdateEntityTick();
                entity.world.profiler.endSection();

                if (newEntity instanceof EntityItem) searchForOtherItemsNearby((EntityItem) newEntity); // TODO: This isn't in same-dimension teleportation in vanilla, but why?

                return newEntity;
            }
        }
    }
}

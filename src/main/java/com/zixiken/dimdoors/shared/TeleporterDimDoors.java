package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;

//ref: https://github.com/WayofTime/BloodMagic/blob/1.11/src/main/java/WayofTime/bloodmagic/ritual/portal/Teleports.java
public class TeleporterDimDoors extends Teleporter {

    /**
     * Teleporter isn't static, so TeleporterDimDoors can't be static, so we're
     * using the the Singleton Design Pattern instead
     */
    private static TeleporterDimDoors INSTANCE;

    private TeleporterDimDoors(WorldServer world) {
        super(world);
    }

    @Override
    public boolean makePortal(Entity entity) {
        return true;
    }

    @Override
    public void removeStalePortalLocations(long worldTime) {

    }

    @Override
    public boolean placeInExistingPortal(Entity entityIn, float rotationYaw) {
        return true;
    }

    public static TeleporterDimDoors instance() {
        if (INSTANCE == null) {
            INSTANCE = new TeleporterDimDoors(DimDoors.proxy.getWorldServer(0));
        }
        return INSTANCE;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
    }

    public boolean teleport(Entity entity, Location location) { //@todo add float playerRotationYaw as a parameter
        if (entity.world.isRemote) {
            return false;
        }

        BlockPos newPos = location.getPos();
        int oldDimID = entity.dimension;
        int newDimID = location.getDimensionID();
        //DimDoors.log(TeleportHelper.class, "Starting teleporting now:");
        if (oldDimID == newDimID) {
            teleportLocal(entity, newPos);
        } else {
            teleportDimensional(entity, newPos, newDimID);
        }
        entity.timeUntilPortal = 50;
        WorldServer worldServer = DimDoors.proxy.getWorldServer(oldDimID);
        worldServer.resetUpdateEntityTick();
        return true;
        //@todo set player angle in front of and facing away from the door
    }

    private void teleportDimensional(Entity entity, BlockPos pos, int newDimID) {
        int oldDimID = entity.dimension;
        WorldServer oldWorldserver = DimDoors.proxy.getWorldServer(oldDimID);
        WorldServer newWorldserver = DimDoors.proxy.getWorldServer(newDimID);
        if (entity instanceof EntityPlayer) {
            DimDoors.log(TeleporterDimDoors.class, "Teleporting Player to new dimension.");
            EntityPlayerMP player = (EntityPlayerMP) entity;
            float playerRotationYaw = player.rotationYaw; //@todo make this a parameter?

            player.dismountRidingEntity();
            processAchievements(player, newDimID);
            PlayerList playerList = player.mcServer.getPlayerList();
            player.dimension = newDimID;

            //Removing the player from the old world
            player.connection.sendPacket(new SPacketRespawn(player.dimension, newWorldserver.getDifficulty(), newWorldserver.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
            playerList.updatePermissionLevel(player);
            oldWorldserver.removeEntityDangerously(player);
            player.isDead = false;

            //Placing the player in the new world
            oldWorldserver.profiler.startSection("moving");
            player.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
            oldWorldserver.profiler.endSection();

            oldWorldserver.profiler.startSection("placing");
            if (player.isEntityAlive()) {
                DimDoors.log(this.getClass(), "Placing the player entity at " + pos.toString());
                player.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
                player.motionX = 0;
                player.motionZ = 0;
                newWorldserver.spawnEntity(player);
                newWorldserver.updateEntityWithOptionalForce(player, false);
            }
            oldWorldserver.profiler.endSection();

            player.setWorld(newWorldserver);

            //Synching the client
            playerList.preparePlayer(player, oldWorldserver);
            player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
            player.interactionManager.setWorld(newWorldserver);
            player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
            playerList.updateTimeAndWeatherForPlayer(player, newWorldserver);
            playerList.syncPlayerInventory(player);
            for (PotionEffect potioneffect : player.getActivePotionEffects()) {
                player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
            }
            net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, oldDimID, newDimID);

        } else {
            DimDoors.log(TeleporterDimDoors.class, "Teleporting non-Player to new dimension.");
            entity.changeDimension(newDimID); //@todo this will crash if we turn travel for normal entities on
        }
        oldWorldserver.resetUpdateEntityTick();
        newWorldserver.resetUpdateEntityTick();
    }

    private void teleportLocal(Entity entity, BlockPos pos) {
        WorldServer worldserver = (WorldServer) entity.world;

        if (entity instanceof EntityPlayer) {
            DimDoors.log(TeleporterDimDoors.class,
                    "Teleporting Player within same dimension.");
            EntityPlayerMP player = (EntityPlayerMP) entity;

            float playerRotationYaw = player.rotationYaw; //@todo make this a parameter?
            PlayerList playerList = player.mcServer.getPlayerList();

            player.dismountRidingEntity();
            worldserver.profiler.startSection("moving");
            player.setLocationAndAngles(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch);
            //playerList.preparePlayer(player, worldserver); //This makes the player stutter heavily on teleport
            player.connection.setPlayerLocation(pos.getX() + 0.5, pos.getY() + 0.05, pos.getZ() + 0.5, playerRotationYaw, player.rotationPitch, EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class
            ));
            worldserver.profiler.endSection();
            player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));

        } else {
            DimDoors.log(TeleporterDimDoors.class,
                    "Teleporting non-Player within same dimension.");

            entity.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            worldserver.resetUpdateEntityTick();
        }
    }

    private void processAchievements(EntityPlayerMP player, int dimID) {
        /*if (player.dimension == 1 && dimID == 1) {
            player.world.removeEntity(player);

            if (!player.playerConqueredTheEnd) {
                player.playerConqueredTheEnd = true;

                if (player.hasAchievement(AchievementList.THE_END2)) {
                    player.connection.sendPacket(new SPacketChangeGameState(4, 0.0F));
                } else {
                    player.addStat(AchievementList.THE_END2);
                    player.connection.sendPacket(new SPacketChangeGameState(4, 1.0F));
                }
            }
        } else if (player.dimension == 0 && dimID == 1) {
            player.addStat(AchievementList.THE_END);
        } else if (dimID == -1) {
            player.addStat(AchievementList.PORTAL);
        }*/
    }
}

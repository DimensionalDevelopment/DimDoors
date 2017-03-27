package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.stats.AchievementList;

//ref: https://github.com/WayofTime/BloodMagic/blob/1.11/src/main/java/WayofTime/bloodmagic/ritual/portal/Teleports.java
public class TeleporterDimDoors extends Teleporter {

    /**
     * Teleporter isn't static, so TeleporterDimDoors can't be static, so we're
     * using the the Singleton Design Pattern instead
     */
    private static TeleporterDimDoors instance;

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
        if (instance == null) {
            instance = new TeleporterDimDoors(DimDoors.proxy.getWorldServer(0));
        }
        return instance;
    }

    @Override
    public void placeInPortal(Entity entity, float rotationYaw) {
    }

    public boolean teleport(Entity entity, Location location) { //@todo add float playerRotationYaw as a parameter
        if (DimDoors.getDefWorld().isRemote) {
            return false;
        }

        entity.timeUntilPortal = 50;

        BlockPos newPos = location.getPos();
        int oldDimID = entity.dimension;
        int newDimID = location.getDimensionID();
        //DimDoors.log(TeleportHelper.class, "Starting teleporting now:");
        if (oldDimID == newDimID) {
            if (entity instanceof EntityPlayer) {
                DimDoors.log(TeleporterDimDoors.class, "Teleporting Player within same dimension.");
                EntityPlayerMP player = (EntityPlayerMP) entity;

                float playerRotationYaw = player.rotationYaw;

                player.dismountRidingEntity();
                ((EntityPlayerMP) player).connection.setPlayerLocation(newPos.getX() + 0.5, newPos.getY() + 0.05, newPos.getZ() + 0.5, playerRotationYaw, player.rotationPitch, EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class));
                //player.setRotationYawHead(f);
                //player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
            } else {
                DimDoors.log(TeleporterDimDoors.class, "Teleporting non-Player within same dimension.");
                WorldServer world = (WorldServer) entity.world;

                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                world.resetUpdateEntityTick();
            }
            entity.timeUntilPortal = 50;
        } else {
            WorldServer oldWorldServer = DimDoors.proxy.getWorldServer(oldDimID);
            WorldServer newWorldServer = DimDoors.proxy.getWorldServer(newDimID);
            if (entity instanceof EntityPlayer) {
                DimDoors.log(TeleporterDimDoors.class, "Teleporting Player to new dimension.");
                EntityPlayerMP player = (EntityPlayerMP) entity;

                float playerRotationYaw = player.rotationYaw;

                player.dismountRidingEntity();
                processAchievements(player, newDimID);
                player.mcServer.getPlayerList().transferPlayerToDimension(player, newDimID, this);
                player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
                player.removeExperienceLevel(0); //update experience
                player.setPlayerHealthUpdated(); //update health
                //updating food seems inpossible
                ((EntityPlayerMP) player).connection.setPlayerLocation(newPos.getX() + 0.5, newPos.getY() + 0.05, newPos.getZ() + 0.5, playerRotationYaw, player.rotationPitch, EnumSet.<SPacketPlayerPosLook.EnumFlags>noneOf(SPacketPlayerPosLook.EnumFlags.class));
                //player.setRotationYawHead(f);                
                //player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
            } else if (!entity.world.isRemote) {
                DimDoors.log(TeleporterDimDoors.class, "Teleporting non-Player to new dimension.");
                entity.changeDimension(newDimID);
                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                oldWorldServer.resetUpdateEntityTick();
                newWorldServer.resetUpdateEntityTick();
            } else {
                //does this statement ever get reached though?
                return false;
            }
            entity.timeUntilPortal = 100;
        }
        return true;
        //@todo set player angle in front of and facing away from the door
    }

    private void processAchievements(EntityPlayerMP player, int dimID) {
        if (player.dimension == 1 && dimID == 1) {
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
        }
    }
}

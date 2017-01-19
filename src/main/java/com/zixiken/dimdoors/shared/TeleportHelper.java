package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleportHelper extends Teleporter {

    private final Location location;

    public TeleportHelper(Location location) {//@todo make TeleportHelper static
        super((WorldServer) location.getWorld());
        this.location = location;
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        BlockPos pos = location.getPos();
        entityIn.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .05, pos.getZ() + .5);
    }

    public static void teleport(Entity entity, Location location) {
        if (entity instanceof EntityPlayerSP) {
            return;
        }

        BlockPos newPos = location.getPos();
        int oldDimID = entity.dimension;
        int newDimID = location.getDimensionID();
        WorldServer oldWorldServer = DimDoors.proxy.getWorldServer(oldDimID);
        WorldServer newWorldServer = DimDoors.proxy.getWorldServer(newDimID);

        if (oldDimID == newDimID) {
            if (entity instanceof EntityPlayer) {
                EntityPlayerMP player = (EntityPlayerMP) entity;

                player.setPositionAndUpdate(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
                player.timeUntilPortal = 150;
            } else {
                WorldServer world = (WorldServer) entity.world;

                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                entity.timeUntilPortal = 150;
                world.resetUpdateEntityTick();
            }
        } else {
            if (entity instanceof EntityPlayer) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                player.changeDimension(newDimID);
                player.setPositionAndUpdate(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
            } else if (!entity.world.isRemote) {
                entity.changeDimension(newDimID);
                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                oldWorldServer.resetUpdateEntityTick();
                newWorldServer.resetUpdateEntityTick();
            }
        }
        //@todo set player angle in front of and facing away from the door
    }
}

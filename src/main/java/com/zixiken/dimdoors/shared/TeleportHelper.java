package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.DimDoors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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

    public static boolean teleport(Entity entity, Location newLocation) {
        if (DimDoors.getDefWorld().isRemote) {
            return false;
        }
        entity.timeUntilPortal = 50;

        BlockPos newPos = newLocation.getPos();
        int oldDimID = entity.dimension;
        int newDimID = newLocation.getDimensionID();
        WorldServer oldWorldServer = DimDoors.proxy.getWorldServer(oldDimID);
        WorldServer newWorldServer = DimDoors.proxy.getWorldServer(newDimID);
        //DimDoors.log(TeleportHelper.class, "Starting teleporting now:");
        if (oldDimID == newDimID) {
            if (entity instanceof EntityPlayer) {
                DimDoors.log(TeleportHelper.class, "Teleporting Player within same dimension.");
                EntityPlayerMP player = (EntityPlayerMP) entity;

                //player.setLocationAndAngles(newPos.getX() + 0.5, newPos.getY() + 0.05, newPos.getZ() + 0.5, player.getRotationYawHead(), player.getRotatedYaw(Rotation.CLOCKWISE_180)); //@todo, instead of following line
                player.setPositionAndUpdate(newPos.getX() + 0.5, newPos.getY() + 0.05, newPos.getZ() + 0.5);
                player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
            } else {
                DimDoors.log(TeleportHelper.class, "Teleporting non-Player within same dimension.");
                WorldServer world = (WorldServer) entity.world;

                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                entity.timeUntilPortal = 50;
                world.resetUpdateEntityTick();
            }
            entity.timeUntilPortal = 50;
        } else {
            if (entity instanceof EntityPlayer) {
                DimDoors.log(TeleportHelper.class, "Teleporting Player to new dimension.");
                EntityPlayerMP player = (EntityPlayerMP) entity;
                player.changeDimension(newDimID); //@todo, this only works for Vanilla dimensions, I've heard?
                player.setPositionAndUpdate(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                player.world.updateEntityWithOptionalForce(player, false);
                //player.connection.sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
            } else if (!entity.world.isRemote) {
                DimDoors.log(TeleportHelper.class, "Teleporting non-Player to new dimension.");
                entity.changeDimension(newDimID);
                entity.setPosition(newPos.getX() + 0.5, newPos.getY() + 0.5, newPos.getZ() + 0.5);
                oldWorldServer.resetUpdateEntityTick();
                newWorldServer.resetUpdateEntityTick();
            } else {
                //does this statement ever get reached though?
                return false;
            }
            entity.timeUntilPortal = 150;
        }
        return true;
        //@todo set player angle in front of and facing away from the door
    }
}

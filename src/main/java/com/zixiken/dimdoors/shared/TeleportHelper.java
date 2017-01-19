package com.zixiken.dimdoors.shared;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleportHelper extends Teleporter {

    Location location;

    public TeleportHelper(Location location) {
        super((WorldServer) location.getWorld());
    }

    @Override
    public void placeInPortal(Entity entityIn, float rotationYaw) {
        BlockPos pos = location.getPos();
        entityIn.setPositionAndUpdate(pos.getX() + .5, pos.getY() + .05, pos.getZ() + .5);
    }

    public static void teleport(Entity entity, Location location) {
        Teleporter tele = new TeleportHelper(location);
        if (entity instanceof EntityPlayerMP) {
            location.getWorld().getMinecraftServer().getPlayerList().transferPlayerToDimension((EntityPlayerMP) entity, location.getDimensionID(), tele);
        } else {
            location.getWorld().getMinecraftServer().getPlayerList().transferEntityToWorld(entity, entity.world.provider.getDimension(), (WorldServer) entity.world, (WorldServer) location.getWorld(), tele);
        }
    }
}
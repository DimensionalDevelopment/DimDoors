package org.dimdev.ddutils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.*;

import java.util.UUID;

public final class EntityUtils {

    public static UUID getEntityOwnerUUID(Entity entity) { // TODO: make this recursive
        if (entity instanceof EntityThrowable) entity = ((EntityThrowable) entity).getThrower();
        if (entity instanceof EntityArrow) entity = ((EntityArrow) entity).shootingEntity;
        if (entity instanceof EntityFireball) entity = ((EntityFireball) entity).shootingEntity;
        if (entity instanceof EntityLlamaSpit) entity = ((EntityLlamaSpit) entity).owner; // Llamas are ownable
        if (entity.getControllingPassenger() != null && !(entity instanceof EntityPlayer)) entity = entity.getControllingPassenger();
        if (entity.getPassengers().size() > 0) entity.getPassengers().get(0);
        if (entity instanceof EntityFishHook) entity = ((EntityFishHook) entity).getAngler();
        if (entity instanceof EntityLiving && ((EntityLiving) entity).getLeashed()) entity = ((EntityLiving) entity).getLeashHolder();
        if (entity instanceof EntityItem) {
            String playerName = ((EntityItem) entity).getThrower();
            EntityPlayer player = null;
            if (playerName != null) player = entity.world.getPlayerEntityByName(((EntityItem) entity).getThrower());
            if (player != null) entity = player;
        }

        if (entity instanceof IEntityOwnable && ((IEntityOwnable) entity).getOwnerId() != null) return ((IEntityOwnable) entity).getOwnerId();
        if (entity instanceof EntityPlayer) return entity.getUniqueID(); // ownable players shouldn't be a problem, but just in case we have a slave mod, check their owner's uuid first to send them to their owner's pocket :)
        return null;
    }
}

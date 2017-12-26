package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.rifts.RiftRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public final class EventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST) // don't let other mods do something based on the event
    public static void onLivingHurt(LivingHurtEvent event) {
        Entity entity = event.getEntity();
        if (entity.dimension == DimDoorDimensions.limbo.getId() && event.getSource() == DamageSource.FALL) {
            event.setCanceled(true);// no fall damage in limbo
        }
    }

    @SubscribeEvent
    public static void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            World world = entity.world;
            int dimID = world.provider.getDimension();
            if (!world.isRemote && !player.isDead && DimDoorDimensions.isPocketDimension(dimID) && !PocketRegistry.getForDim(dimID).isPlayerAllowedToBeHere(player, player.getPosition())) {
                // TODO: Avoid players even getting here by making a maximum build distance that's smaller than the pocket size
                // TODO: This doesn't really work yet.
                DimDoors.chat(player, "You travelled too far into the void and have been sent to Limbo.");
                // PocketRegistry.sendToLimbo(player); // TODO
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (DimDoorDimensions.isPocketDimension(event.fromDim) && !DimDoorDimensions.isPocketDimension(event.toDim)) {
            RiftRegistry.setOverworldRift(event.player.getCachedUniqueIdString(), null);
        }
    }
}

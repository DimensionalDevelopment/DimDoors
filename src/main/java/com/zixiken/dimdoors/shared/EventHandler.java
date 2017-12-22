package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.pockets.PocketRegistry;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof EntityPlayer && !entity.world.isRemote) { // check that it's a player first to avoid calling String.contains for every entity
            if (!DDConfig.HAVE_CONFIG_DEFAULTS_BEEN_CHECKED_FOR_CORRECTNESS && !DimDoors.VERSION.contains("a")) { // default values were not checked in non-alpha version
                EntityPlayer player = (EntityPlayer) entity;
                DimDoors.chat(player, "The default values for the config files for this non-alpha version of DimDoors have not been sufficiently checked on correctness. Please notify the developer about this ONLY IF no newer version of this mod is available.");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST) // don't let other mods do something based on the event
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
            if (!world.isRemote && !player.isDead && DimDoorDimensions.isPocketDimension(dimID) && !PocketRegistry.getForDim(dimID).isPlayerAllowedToBeHere(player, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ())) {
                // TODO: Avoid players even getting here by making a maximum build distance that's smaller than the pocket size
                // TODO: This doesn't really work yet.
                // DimDoors.chat(player, "You travelled too far into the void and have been sent to Limbo.");
                // PocketRegistry.sendToLimbo(player); // TODO
            }
        }
    }
}

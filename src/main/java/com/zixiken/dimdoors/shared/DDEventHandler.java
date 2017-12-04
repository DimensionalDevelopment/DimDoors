/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import com.zixiken.dimdoors.shared.util.Location;
import com.zixiken.dimdoors.shared.world.DimDoorDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

/**
 *
 * @author Robijnvogel
 */
public class DDEventHandler {

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        //check if config default values have been checked
        if (!DDConfig.HAVE_CONFIG_DEFAULTS_BEEN_CHECKED_FOR_CORRECTNESS) {
            if (!DimDoors.VERSION.contains("a")) { //if it is not an alpha version
                Entity entity = event.getEntity();
                World world = entity.world;
                if (!world.isRemote) {
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer) entity;
                        DimDoors.chat(player, "The default values for the config files fo this non-alpha version of DimDoors have not been sufficiently checked on correctness. Please notify the developer about this IF no newer version of this mod is available.");
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        Entity entity = event.getEntity();
        if (entity.dimension == DimDoorDimensions.LIMBO.getId()) {
            event.setCanceled(true); // no fall damage in limbo
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDim(PlayerChangedDimensionEvent event) {
        EntityPlayer player = event.player;
        int dimID = event.toDim;
        World world = player.world;
        if (!world.isRemote && DimDoorDimensions.isPocketDimensionID(dimID)) {
            if (player instanceof EntityPlayerMP) {
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                checkPlayerLocationPermission(playerMP);
            }
        }
    }
    
    @SubscribeEvent
    public void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) {
            World world = entity.world;
            if (!world.isRemote && DimDoorDimensions.isPocketDimensionID(world.provider.getDimension())) {
                EntityPlayerMP player = (EntityPlayerMP) entity;
                checkPlayerLocationPermission(player);
            }
        }
    }

    /**
     *
     * @param player the player entity to check for permissions
     * @pre {@code (entity instanceof EntityPlayerMP)}
     */
    private void checkPlayerLocationPermission(EntityPlayerMP player) {
        if (!player.isDead && !(player.getPosition().getY() < 1)) {
            Location location = Location.getLocation(player);
            DimDoors.log(this.getClass(), "A player just entered a new chunk in a DimDoors dimension.");
            if (!PocketRegistry.INSTANCE.isPlayerAllowedToBeHere(player, location)) {
                //@todo this doesn't really work yet.
                //DimDoors.chat(player, "You are not supposed to be here. In future version of this mod, you will be teleported to Limbo if you go here.");
            }
        }
    }
}

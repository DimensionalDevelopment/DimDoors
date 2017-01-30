/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.DimDoors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 *
 * @author s101426
 */
public class DDEventHandler {

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) { //@todo, probably move this to another class
        //check if config default values have been checked
        if (!DDConfig.haveConfigDefaultsBeenCheckedForCorrectness) {
            if (!DimDoors.VERSION.contains("a")) { //if it is not an alpha version
                Entity entity = event.getEntity();
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    DimDoors.chat(player, "The default values for the config files fo this non-alpha version of DimDoors have not been sufficiently checked on correctness. Please notify the developer about this IF no newer version of this mod is available.");
                }
            }
        }
    }
}

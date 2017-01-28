/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 *
 * @author Robijnvogel
 */
public interface IDDProxy {

    public boolean isClient();

    public void onPreInitialization(FMLPreInitializationEvent event);

    public void onInitialization(FMLInitializationEvent event);

    public EntityPlayer getLocalPlayer();
    
    public WorldServer getWorldServer(int dimId);

    public World getDefWorld();
}

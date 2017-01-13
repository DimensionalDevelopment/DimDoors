/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.server;

import com.zixiken.dimdoors.DDProxyCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

/**
 *
 * @author Robijnvogel
 */
public class DDProxyServer extends DDProxyCommon {

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public EntityPlayer getLocalPlayer() {
        return null;
    }

    @Override
    public World getDefWorld() {
        return DimensionManager.getWorld(0); //gets the server world dim 0 handler
    }

}

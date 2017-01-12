/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author Robijnvogel
 */
class Pocket {

    private int ID;
    private int size; //in chunks
    private int depth;
    private int typeID;
    private Object coords; //0,0 should be 0,0, 1,1 should be 128,128 etc
    private final List<String> playerUUIDs;
    private final List<Integer> doorIDs;

    Pocket() {
        playerUUIDs = new ArrayList();
        doorIDs = new ArrayList();
    }

    static Pocket readFromNBT(int ID, NBTTagCompound pocketNBT) {
        Pocket pocket = new Pocket();
        pocket.ID = ID;
        pocket.size = pocketNBT.getInteger("size");
        pocket.depth = pocketNBT.getInteger("depth");
        pocket.typeID = pocketNBT.getInteger("typeID");
        
        //@todo pocket.coords = pocketNBT.get;
        NBTTagCompound playersNBT = pocketNBT.getCompoundTag("players");
        NBTTagCompound doorsNBT = pocketNBT.getCompoundTag("doors");
        //@todo iterate through above two compound tags
        
        return pocket;
    }
    
    static NBTBase writeToNBT(Pocket pocket) {
        NBTTagCompound pocketNBT = new NBTTagCompound();
        
        //@todo implement shit;
        
        return pocketNBT;
    }

}

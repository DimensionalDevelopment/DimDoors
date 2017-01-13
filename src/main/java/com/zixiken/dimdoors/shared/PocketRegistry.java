/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class PocketRegistry {

    public static final PocketRegistry Instance = new PocketRegistry();

    // Privates
    private int nextUnusedID;
    private final Map<Integer, Pocket> pocketList;

    // Methods
    public PocketRegistry() {
        nextUnusedID = 0;
        pocketList = new HashMap();
    }

    public void reset() {
        nextUnusedID = 0;
        pocketList.clear();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        nextUnusedID = nbt.getInteger("nextUnusedID");
        if (nbt.hasKey("pocketData")) {
            NBTTagCompound pocketsNBT = nbt.getCompoundTag("pocketData");
            int i = 1;
            String tag = "" + i;
            while (pocketsNBT.hasKey(tag)) {
                NBTTagCompound pocketNBT = pocketsNBT.getCompoundTag(tag);
                Pocket pocket = Pocket.readFromNBT(i, pocketNBT);
                pocketList.put(i, pocket);

                i++;
                tag = "" + i;
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("nextUnusedID", nextUnusedID);
        NBTTagCompound pocketsNBT = new NBTTagCompound();
        for (Map.Entry<Integer, Pocket> entry : pocketList.entrySet()) {
            pocketsNBT.setTag("" + entry.getKey(), Pocket.writeToNBT(entry.getValue()));
        }
        nbt.setTag("pocketData", pocketsNBT);
    }

    public int registerNewPocket(Pocket pocket, World world) {
        pocketList.put(nextUnusedID, pocket);

        nextUnusedID++;
        PocketSavedData.get(world).markDirty(); //Notify that this needs to be saved on world save
        return nextUnusedID - 1;
    }

    public void removePocket(int pocketID, World world) {
        if (pocketList.containsKey(pocketID)) {
            pocketList.remove(pocketID);
            PocketSavedData.get(world).markDirty(); //Notify that this needs to be saved on world save
        }
    }

    public Pocket getPocket(int ID) {
        return pocketList.get(ID);
    }
}

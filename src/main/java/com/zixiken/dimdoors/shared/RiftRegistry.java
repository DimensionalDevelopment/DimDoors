/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.tileentities.DDTileEntityBase;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 *
 * @author Robijnvogel
 */
public class RiftRegistry {
    
    public static final RiftRegistry Instance = new RiftRegistry();

    // Privates
    private int nextUnusedID;
    private final Map<Integer, DDTileEntityBase> riftList;

    // Methods
    public RiftRegistry() {
        nextUnusedID = 0;
        riftList = new HashMap();
    }

    public void reset() {
        nextUnusedID = 0;
        riftList.clear();
    }

    public void readFromNBT(NBTTagCompound nbt) {
        nextUnusedID = nbt.getInteger("nextUnusedID");
        if (nbt.hasKey("riftData")) {
            NBTTagCompound riftsNBT = nbt.getCompoundTag("riftData");
            int i = 1;
            String tag = "" + i;
            while (riftsNBT.hasKey(tag)) {
                NBTTagCompound riftNBT = riftsNBT.getCompoundTag(tag);
                DDTileEntityBase rift = DDTileEntityBase.readFromNBT(i, riftNBT);
                riftList.put(i, rift);
                
                i++;
                tag = "" + i;
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("nextUnusedID", nextUnusedID);
        NBTTagCompound riftsNBT = new NBTTagCompound();
        for (Map.Entry<Integer, DDTileEntityBase> entry : riftList.entrySet()) {
            riftsNBT.setTag("" + entry.getKey(), DDTileEntityBase.writeToNBT(entry.getValue()));
        }
        nbt.setTag("riftData", riftsNBT);
    }

    public int registerNewRift(DDTileEntityBase rift, World world) {
        riftList.put(nextUnusedID, rift);        
        
        nextUnusedID++;
        RiftSavedData.get(world).markDirty(); //Notify that this needs to be saved on world save
        return nextUnusedID -1;
    }

    public void removeRift(int riftID, World world) {
        if (riftList.containsKey(riftID)) {
            riftList.remove(riftID);
            RiftSavedData.get(world).markDirty(); //Notify that this needs to be saved on world save
        }
    }

    public DDTileEntityBase getRift(int ID) {
        return riftList.get(ID);
    }
}

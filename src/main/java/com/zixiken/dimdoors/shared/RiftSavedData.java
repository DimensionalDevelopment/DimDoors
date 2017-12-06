/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;

/**
 *
 * @author Robijnvogel
 */
public class RiftSavedData extends DDSavedData {

    private static final String DATA_NAME = "dimdoors_RiftSavedData";

    public RiftSavedData() {
        super(DATA_NAME);
    }

    public RiftSavedData(String s) {
        super(s);
    }

    @Override
    public File getSaveLocation(World world) {
        return new File(super.getSaveLocation(world), "rifts.nbt");
    }

    public static RiftSavedData get(World world) {
        MapStorage storage = world.getMapStorage();
        RiftSavedData instance = (RiftSavedData) storage.getOrLoadData(RiftSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new RiftSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

        NBTTagCompound rifts = new NBTTagCompound();
        RiftRegistry.INSTANCE.writeToNBT(rifts);
        nbt.setTag("rifts", rifts);
        
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        // Load NBT
        if (nbt != null) {
            if (nbt.hasKey("rifts")) {
                NBTTagCompound rifts = nbt.getCompoundTag("rifts");
                RiftRegistry.INSTANCE.readFromNBT(rifts);
            }
        }
    }
}

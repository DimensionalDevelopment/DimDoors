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
public class PocketSavedData extends DDSavedData {

    private static final String DATA_NAME = "dimdoors_PocketSavedData";

    public PocketSavedData() {
        super(DATA_NAME);
    }

    public PocketSavedData(String s) {
        super(s);
    }

    @Override
    public File getSaveLocation(World world) {
        return new File(super.getSaveLocation(world), "pockets.nbt");
    }

    public static PocketSavedData get(World world) {
        MapStorage storage = world.getMapStorage();
        PocketSavedData instance = (PocketSavedData) storage.getOrLoadData(PocketSavedData.class, DATA_NAME);

        if (instance == null) {
            instance = new PocketSavedData();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound pocketnbt) {

        NBTTagCompound pockets = new NBTTagCompound();
        PocketRegistry.Instance.writeToNBT(pockets);
        pocketnbt.setTag("pockets", pockets);

        return pocketnbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound pocketnbt) {
        // Load NBT
        if (pocketnbt != null) {
            if (pocketnbt.hasKey("pockets")) {
                NBTTagCompound pockets = pocketnbt.getCompoundTag("pockets");
                PocketRegistry.Instance.readFromNBT(pockets);
            }
        }
    }
}

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
    public NBTTagCompound writeToNBT(NBTTagCompound riftnbt) {

        NBTTagCompound rifts = new NBTTagCompound();
        RiftRegistry.Instance.writeToNBT(rifts);
        riftnbt.setTag("rifts", rifts);

        //@todo? saveNBTToPath(getSaveLocation(DimDoors.getDefWorld()), riftnbt);
        return riftnbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound riftnbt) {
        // Reset
        RiftRegistry.Instance.reset();

        // Load NBT
        if (riftnbt != null) {
            if (riftnbt.hasKey("rifts")) {
                NBTTagCompound rifts = riftnbt.getCompoundTag("rifts");
                RiftRegistry.Instance.readFromNBT(rifts);
            }
        }
    }
}

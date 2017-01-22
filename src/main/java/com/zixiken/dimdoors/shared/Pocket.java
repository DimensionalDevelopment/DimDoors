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
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 *
 * @author Robijnvogel
 */
class Pocket {

    private final int ID;
    private final int size; //in chunks
    private final int depth;
    private final EnumPocketType typeID; // dungeon, pocket, or personal pocket
    private final int x; //pocket-relative 0 coordinate, should be at x * PocketRegistry.Instance.gridSize * 16
    private final int z; //pocket-relative 0 coordinate, should be at z * PocketRegistry.Instance.gridSize * 16
    private final List<String> playerUUIDs;
    private final List<Integer> doorIDs; //first one of these should be the entrance door? Does that even matter?
    private final int entranceDoorID;
    //when adding any new variables, don't forget to add them to the write and load functions

    public Pocket(int ID, int size, int depth, EnumPocketType typeID, int x, int z, int entranceDoorID) {
        this.ID = ID;
        this.size = size;
        this.depth = depth;
        this.typeID = typeID;
        this.x = x;
        this.z = z;
        this.entranceDoorID = entranceDoorID; //keeping this stored after pocket generation for personal pocket dimensions mostly
        playerUUIDs = new ArrayList();
        doorIDs = new ArrayList();
    }

    public int getID() {
        return ID;
    }
    
    public int getX() {
        return x;
    }
    
    public int getZ() {
        return z;
    }

    int getEntranceDoorID() {
        return entranceDoorID;
    }

    static Pocket readFromNBT(NBTTagCompound pocketNBT) {
        int ID = pocketNBT.getInteger("ID");;
        int size = pocketNBT.getInteger("size");
        int depth = pocketNBT.getInteger("depth");
        EnumPocketType typeID = EnumPocketType.getFromInt(pocketNBT.getInteger("typeID"));
        int x = pocketNBT.getInteger("x");
        int z = pocketNBT.getInteger("z");
        int entranceDoorID = pocketNBT.getInteger("entranceDoorID");
        Pocket pocket = new Pocket(ID, size, depth, typeID, x, z, entranceDoorID);

        NBTTagList playersTagList = (NBTTagList) pocketNBT.getTag("playerUUIDs");
        for (int i = 0; i < playersTagList.tagCount(); i++) {
            String playerUUID = playersTagList.getStringTagAt(i);
            pocket.playerUUIDs.add(playerUUID);
        }

        NBTTagList doorsTagList = (NBTTagList) pocketNBT.getTag("doorIDs");
        for (int i = 0; i < doorsTagList.tagCount(); i++) {
            int doorID = doorsTagList.getIntAt(i);
            pocket.doorIDs.add(doorID);
        }

        return pocket;
    }

    static NBTBase writeToNBT(Pocket pocket) {
        NBTTagCompound pocketNBT = new NBTTagCompound();

        pocketNBT.setInteger("ID", pocket.ID);
        pocketNBT.setInteger("size", pocket.size);
        pocketNBT.setInteger("depth", pocket.depth);
        pocketNBT.setInteger("typeID", pocket.typeID.getIntValue());
        pocketNBT.setInteger("x", pocket.x);
        pocketNBT.setInteger("z", pocket.z);
        pocketNBT.setInteger("entranceDoorID", pocket.entranceDoorID);

        NBTTagList playersTagList = new NBTTagList();
        for (int i = 0; i < pocket.playerUUIDs.size(); i++) {
            NBTTagString playerTag = new NBTTagString(pocket.playerUUIDs.get(i));
            playersTagList.appendTag(playerTag);
        }
        pocketNBT.setTag("playerUUIDs", playersTagList);

        NBTTagList doorsTagList = new NBTTagList();
        for (int i = 0; i < pocket.doorIDs.size(); i++) {
            NBTTagInt doorTag = new NBTTagInt(pocket.doorIDs.get(i));
            doorsTagList.appendTag(doorTag);
        }
        pocketNBT.setTag("doorIDs", doorsTagList);

        return pocketNBT;
    }
}

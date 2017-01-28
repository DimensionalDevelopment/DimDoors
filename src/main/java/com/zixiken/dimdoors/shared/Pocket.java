/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zixiken.dimdoors.shared;

import com.zixiken.dimdoors.shared.tileentities.DDTileEntityBase;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private int ID; //this gets reset every server-load
    private final int size; //in chunks
    private final int depth;
    private final EnumPocketType typeID; // dungeon, pocket, or personal pocket
    private final int x; //pocket-relative 0 coordinate, should be at x * PocketRegistry.Instance.gridSize * 16
    private final int z; //pocket-relative 0 coordinate, should be at z * PocketRegistry.Instance.gridSize * 16
    private final List<String> playerUUIDs;
    private final List<Integer> riftIDs; //@todo first one of these should be the entrance door id? Does that even matter?
    //when adding any new variables, don't forget to add them to the write and load functions

    public Pocket(int size, int depth, EnumPocketType typeID, int x, int z, List<Integer> riftIDs) {
        this.size = size;
        this.depth = depth;
        this.typeID = typeID;
        this.x = x;
        this.z = z;
        this.riftIDs = riftIDs;
        playerUUIDs = new ArrayList();
        PocketRegistry.Instance.registerNewPocket(this);

        for (int riftID : riftIDs) {
            DDTileEntityBase rift = (DDTileEntityBase) RiftRegistry.Instance.getRiftLocation(riftID).getTileEntity();
            rift.setPocketID(this.ID); //set the rift's pocket ID to this pocket's pocket ID;
        }
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
        if (riftIDs.isEmpty()) {
            return -1;
        } else if (riftIDs.size() == 1) {
            return riftIDs.get(0);
        } else {
            Random random = new Random();
            int index = random.nextInt(riftIDs.size());
            return riftIDs.get(index);
        }
    }

    public void setID(int newID) {
        ID = newID;
    }

    static void readFromNBT(NBTTagCompound pocketNBT) {
        int size = pocketNBT.getInteger("size");
        int depth = pocketNBT.getInteger("depth");
        EnumPocketType typeID = EnumPocketType.getFromInt(pocketNBT.getInteger("typeID"));
        int x = pocketNBT.getInteger("x");
        int z = pocketNBT.getInteger("z");
        List<Integer> riftIDs = new ArrayList();
        NBTTagList doorsTagList = (NBTTagList) pocketNBT.getTag("doorIDs");
        for (int i = 0; i < doorsTagList.tagCount(); i++) {
            int doorID = doorsTagList.getIntAt(i);
            riftIDs.add(doorID);
        }
        Pocket pocket = new Pocket(size, depth, typeID, x, z, riftIDs);

        NBTTagList playersTagList = (NBTTagList) pocketNBT.getTag("playerUUIDs"); //@todo, maybe it is bad practice to put this behind the creation statement of the Pocket?
        for (int i = 0; i < playersTagList.tagCount(); i++) {
            String playerUUID = playersTagList.getStringTagAt(i);
            pocket.playerUUIDs.add(playerUUID);
        }

        PocketRegistry.Instance.registerNewPocket(pocket);
    }

    static NBTBase writeToNBT(Pocket pocket) {
        NBTTagCompound pocketNBT = new NBTTagCompound();
        pocketNBT.setInteger("size", pocket.size);
        pocketNBT.setInteger("depth", pocket.depth);
        pocketNBT.setInteger("typeID", pocket.typeID.getIntValue());
        pocketNBT.setInteger("x", pocket.x);
        pocketNBT.setInteger("z", pocket.z);

        NBTTagList doorsTagList = new NBTTagList();
        for (int i = 0; i < pocket.riftIDs.size(); i++) {
            NBTTagInt doorTag = new NBTTagInt(pocket.riftIDs.get(i));
            doorsTagList.appendTag(doorTag);
        }
        pocketNBT.setTag("doorIDs", doorsTagList);

        NBTTagList playersTagList = new NBTTagList();
        for (int i = 0; i < pocket.playerUUIDs.size(); i++) {
            NBTTagString playerTag = new NBTTagString(pocket.playerUUIDs.get(i));
            playersTagList.appendTag(playerTag);
        }

        pocketNBT.setTag("playerUUIDs", playersTagList);
        return pocketNBT;
    }
}

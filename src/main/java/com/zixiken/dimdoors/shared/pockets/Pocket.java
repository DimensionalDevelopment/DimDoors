package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.shared.util.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 *
 * @author Robijnvogel
 */
public class Pocket { // TODO: better visibilities

    @Getter int id; // Not saved
    @Getter /*private*/ int dimID; // Not saved

    @Getter private int x; // Grid x
    @Getter private int z; // Grid y
    @Getter private int depth;
    @Getter @Setter private int size; // In chunks TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @Getter @Setter private int originalDim; // The non-pocket dimension from which this dungeon was created
    private List<Integer> riftIDs;
    private List<String> playerUUIDs;

    private Pocket() {}

    Pocket(int id, int dimID, int x, int z, int depth) {
        this.id = id;
        this.dimID = dimID;
        this.x = x;
        this.z = z;
        this.depth = depth;
        riftIDs = new ArrayList<>();
        playerUUIDs  = new ArrayList<>();
    }

    static Pocket readFromNBT(NBTTagCompound pocketNBT) {
        Pocket pocket = new Pocket();
        pocket.id = pocketNBT.getInteger("id");
        pocket.x = pocketNBT.getInteger("x");
        pocket.z = pocketNBT.getInteger("z");
        pocket.depth = pocketNBT.getInteger("depth");
        pocket.size = pocketNBT.getInteger("size");
        pocket.originalDim = pocketNBT.getInteger("originalDim");

        pocket.riftIDs = new ArrayList<>();
        NBTTagList riftIDsTagList = (NBTTagList) pocketNBT.getTag("riftIDs");
        for (int i = 0; i < riftIDsTagList.tagCount(); i++) {
            pocket.riftIDs.add(riftIDsTagList.getIntAt(i));
        }

        pocket.playerUUIDs  = new ArrayList<>();
        NBTTagList playersTagList = (NBTTagList) pocketNBT.getTag("playerUUIDs");
        for (int i = 0; i < playersTagList.tagCount(); i++) {
            pocket.playerUUIDs.add(playersTagList.getStringTagAt(i));
        }

        return pocket;
    }

    static NBTBase writeToNBT(Pocket pocket) {
        NBTTagCompound pocketNBT = new NBTTagCompound();
        pocketNBT.setInteger("id", pocket.id);
        pocketNBT.setInteger("x", pocket.x);
        pocketNBT.setInteger("z", pocket.z);
        pocketNBT.setInteger("depth", pocket.depth);
        pocketNBT.setInteger("size", pocket.size);
        pocketNBT.setInteger("originalDim", pocket.originalDim);

        NBTTagList riftIDsTagList = new NBTTagList();
        for (int i = 0; i < pocket.riftIDs.size(); i++) {
            NBTTagInt doorTag = new NBTTagInt(pocket.riftIDs.get(i));
            riftIDsTagList.appendTag(doorTag);
        }
        pocketNBT.setTag("riftIDs", riftIDsTagList);

        NBTTagList playersTagList = new NBTTagList();
        for (int i = 0; i < pocket.playerUUIDs.size(); i++) {
            NBTTagString playerTag = new NBTTagString(pocket.playerUUIDs.get(i));
            playersTagList.appendTag(playerTag);
        }
        pocketNBT.setTag("playerUUIDs", playersTagList);

        return pocketNBT;
    }

    public void addRiftID(int id) {
        riftIDs.add(id);
    }

    public int getEntranceRiftID() {
        if (riftIDs.isEmpty()) {
            return -1;
        } else if (riftIDs.size() == 1) {
            return riftIDs.get(0);
        } else {
            for (Integer riftID : riftIDs) {
                //TileEntity tileEntity = RiftRegistry.INSTANCE.getRiftLocation(riftID).getTileEntity(); // TODO rift
                //if (tileEntity instanceof TileEntityDimDoorWarp) {
                //    return riftID;
                //}
            }
            Random random = new Random(); // TODO: weighted random?
            return riftIDs.get(random.nextInt(riftIDs.size()));
        }
    }

    boolean isLocationWithinPocketBounds(final Location location, final int gridSize) {
        int locX = location.getPos().getX();
        int locZ = location.getPos().getY();
        // pocket bounds
        int pocMinX = x * gridSize;
        int pocMinZ = z * gridSize;
        int pocMaxX = pocMinX + (size + 1) * 16;
        int pocMaxZ = pocMinX + (size + 1) * 16;
        return pocMinX <= locX && pocMinZ <= locZ && locX < pocMaxX && locZ < pocMaxZ;
    }

    public void validatePlayerEntry(EntityPlayer player) { // TODO
        String playerUUID = player.getCachedUniqueIdString();
        if (!playerUUIDs.contains(playerUUID)) {
            playerUUIDs.add(playerUUID);
        }
    }

    public boolean isPlayerAllowedInPocket(EntityPlayer player) { // TODO
        String playerUUID = player.getCachedUniqueIdString();
        return playerUUIDs.contains(playerUUID);
    }
}

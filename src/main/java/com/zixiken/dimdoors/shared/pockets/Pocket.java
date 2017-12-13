package com.zixiken.dimdoors.shared.pockets;

import com.zixiken.dimdoors.shared.VirtualLocation;
import com.zixiken.dimdoors.shared.rifts.RiftDestination;
import com.zixiken.dimdoors.shared.rifts.TileEntityRift;
import com.zixiken.dimdoors.shared.rifts.WeightedRiftDestination;
import com.zixiken.dimdoors.shared.util.Location;

import java.util.*;

import com.zixiken.dimdoors.shared.util.MathUtils;
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
    @Getter @Setter private VirtualLocation virtualLocation; // The non-pocket dimension from which this dungeon was created
    private List<Integer> riftIDs;
    private List<String> playerUUIDs;
    @Getter Location entrance;

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
        pocket.virtualLocation = VirtualLocation.readFromNBT(pocketNBT.getCompoundTag("originalDim"));

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
        pocketNBT.setTag("originalDim", pocket.virtualLocation.writeToNBT());

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

    boolean isLocationWithinPocketBounds(int locX, int locY, int locZ, int gridSize) {
        // pocket bounds
        int pocMinX = x * gridSize;
        int pocMinZ = z * gridSize;
        int pocMaxX = pocMinX + (size + 1) * 16;
        int pocMaxZ = pocMinX + (size + 1) * 16;
        return pocMinX <= locX && pocMinZ <= locZ && locX < pocMaxX && locZ < pocMaxZ;
    }

    // TODO better allow/deny player system. Just because a player is allowed in two adjacent pockets doesn't mean he should be able to cross through the void to the other pocket
    public void allowPlayer(EntityPlayer player) {
        String playerUUID = player.getCachedUniqueIdString();
        if (!playerUUIDs.contains(playerUUID)) {
            playerUUIDs.add(playerUUID);
        }
    }

    public boolean isPlayerAllowedInPocket(EntityPlayer player) { // TODO
        String playerUUID = player.getCachedUniqueIdString();
        return playerUUIDs.contains(playerUUID);
    }

    public List<TileEntityRift> getRifts() {
        return null; // TODO
    }

    public void selectEntrance() { // Always call after creating a pocket except when building the pocket TODO: more general setup method
        List<TileEntityRift> rifts = getRifts();

        HashMap<Integer, Float> entranceIndexWeights = new HashMap<>();

        int index = 0;
        for (TileEntityRift rift : rifts) { // Find an entrance
            for (WeightedRiftDestination weightedPocketEntranceDest : rift.getDestinations()) {
                if (weightedPocketEntranceDest.getDestination().getType() == RiftDestination.DestinationType.POCKET_ENTRANCE) {
                    entranceIndexWeights.put(index, weightedPocketEntranceDest.getWeight());
                    rift.markDirty();
                    index++;
                }
            }
        }
        if (entranceIndexWeights.size() == 0) return;
        int selectedEntranceIndex = MathUtils.weightedRandom(entranceIndexWeights);

        index = 0;
        for (TileEntityRift rift : rifts) { // Replace entrances with appropriate items
            Iterator<WeightedRiftDestination> destIterator = rift.getDestinations().iterator();
            while (destIterator.hasNext()) {
                WeightedRiftDestination wdest = destIterator.next();
                RiftDestination dest = wdest.getDestination();
                if (dest.getType() == RiftDestination.DestinationType.POCKET_ENTRANCE) {
                    destIterator.remove();
                    if (index == selectedEntranceIndex) {
                        entrance = new Location(rift.getWorld(), rift.getPos());
                        List<WeightedRiftDestination> ifDestinations = ((RiftDestination.PocketEntranceDestination) dest).getIfDestinations();
                        for (WeightedRiftDestination ifDestination : ifDestinations) {
                            rift.addDestination(ifDestination.getDestination(), ifDestination.getWeight() / wdest.getWeight(), ifDestination.getGroup());
                        }
                    } else {
                        List<WeightedRiftDestination> otherwiseDestinations = ((RiftDestination.PocketEntranceDestination) dest).getOtherwiseDestinations();
                        for (WeightedRiftDestination otherwiseDestination : otherwiseDestinations) {
                            rift.addDestination(otherwiseDestination.getDestination(), otherwiseDestination.getWeight() / wdest.getWeight(), otherwiseDestination.getGroup());
                        }
                    }
                    index++;
                }
            }
        }
    }

    public void linkPocketTo(RiftDestination linkTo) {
        List<TileEntityRift> rifts = getRifts();

        for (TileEntityRift rift : rifts) { // Link pocket exits back
            Iterator<WeightedRiftDestination> destIterator = rift.getDestinations().iterator();
            while (destIterator.hasNext()) {
                WeightedRiftDestination wdest = destIterator.next();
                RiftDestination dest = wdest.getDestination();
                if (dest.getType() == RiftDestination.DestinationType.POCKET_EXIT) {
                    destIterator.remove();
                    linkTo.withOldDestination(dest);
                    rift.addDestination(linkTo, wdest.getWeight(), wdest.getGroup());
                }
            }
        }
    }

    public void unlinkPocket() {
        // TODO
    }
}

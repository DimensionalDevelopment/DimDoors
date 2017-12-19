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
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;

/**
 *
 * @author Robijnvogel
 */
public class Pocket { // TODO: better visibilities

    @Getter int id; // Not saved
    @Getter /*private*/ int dimID; // Not saved

    @Getter private int x; // Grid x TODO: rename to gridX and gridY
    @Getter private int z; // Grid y
    @Getter @Setter private int size; // In chunks TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @Getter @Setter private VirtualLocation virtualLocation; // The non-pocket dimension from which this dungeon was created
    private List<String> playerUUIDs;
    @Getter Location entrance;
    @Getter List<Location> riftLocations;

    private Pocket() {}

    Pocket(int id, int dimID, int x, int z) {
        this.id = id;
        this.dimID = dimID;
        this.x = x;
        this.z = z;
        playerUUIDs  = new ArrayList<>();
        riftLocations = new ArrayList<>();
    }

    static Pocket readFromNBT(NBTTagCompound nbt) {
        Pocket pocket = new Pocket();
        pocket.id = nbt.getInteger("id");
        pocket.x = nbt.getInteger("x");
        pocket.z = nbt.getInteger("z");
        pocket.size = nbt.getInteger("size");
        if (nbt.hasKey("virtualLocation")) pocket.virtualLocation = VirtualLocation.readFromNBT(nbt.getCompoundTag("virtualLocation"));
        if (nbt.hasKey("entrance")) pocket.entrance = Location.readFromNBT(nbt.getCompoundTag("entrance"));

        pocket.playerUUIDs = new ArrayList<>();
        NBTTagList playerUUIDsNBT = (NBTTagList) nbt.getTag("playerUUIDs");
        for (int i = 0; i < playerUUIDsNBT.tagCount(); i++) { // TODO: convert to foreach
            pocket.playerUUIDs.add(playerUUIDsNBT.getStringTagAt(i));
        }

        pocket.riftLocations = new ArrayList<>();
        NBTTagList riftLocationsNBT = (NBTTagList) nbt.getTag("riftLocations");
        for (NBTBase riftLocationNBT : riftLocationsNBT) {
            pocket.riftLocations.add(Location.readFromNBT((NBTTagCompound) riftLocationNBT));
        }

        return pocket;
    }

    static NBTBase writeToNBT(Pocket pocket) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("id", pocket.id);
        nbt.setInteger("x", pocket.x);
        nbt.setInteger("z", pocket.z);
        nbt.setInteger("size", pocket.size);
        if (pocket.virtualLocation != null) nbt.setTag("virtualLocation", pocket.virtualLocation.writeToNBT());
        if (pocket.entrance != null) nbt.setTag("entrance", Location.writeToNBT(pocket.entrance));

        NBTTagList playerUUIDsNBT = new NBTTagList();
        for (int i = 0; i < pocket.playerUUIDs.size(); i++) {
            playerUUIDsNBT.appendTag(new NBTTagString(pocket.playerUUIDs.get(i)));
        }
        nbt.setTag("playerUUIDs", playerUUIDsNBT);

        NBTTagList riftLocationsNBT = new NBTTagList();
        for (Location loc : pocket.riftLocations) {
            riftLocationsNBT.appendTag(Location.writeToNBT(loc));
        }
        nbt.setTag("riftLocations", riftLocationsNBT);

        return nbt;
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
        List<TileEntityRift> rifts = new ArrayList<>(riftLocations.size()); // TODO: make immutable
        for (Location riftLocation : riftLocations) {
            TileEntity tileEntity = riftLocation.getWorld().getTileEntity(riftLocation.getPos());
            if (tileEntity instanceof TileEntityRift) {
                rifts.add((TileEntityRift) tileEntity);
            } else {
                throw new RuntimeException("The rift locations array was wrong, check the schematic placing function to see why!");
            }
        }
        return rifts;
    }

    public void setup() { // Always call after creating a pocket except when building the pocket
        List<TileEntityRift> rifts = getRifts();

        HashMap<Integer, Float> entranceIndexWeights = new HashMap<>();

        int index = 0;
        for (TileEntityRift rift : rifts) { // Find an entrance
            for (WeightedRiftDestination weightedPocketEntranceDest : rift.getDestinations()) {
                if (weightedPocketEntranceDest.getDestination().getType() == RiftDestination.EnumType.POCKET_ENTRANCE) {
                    entranceIndexWeights.put(index, weightedPocketEntranceDest.getWeight());
                    rift.markDirty();
                    index++;
                }
            }
        }
        if (entranceIndexWeights.size() == 0) return;
        int selectedEntranceIndex = MathUtils.weightedRandom(entranceIndexWeights);

        // Replace entrances with appropriate destinations
        index = 0;
        for (TileEntityRift rift : rifts) {
            ListIterator<WeightedRiftDestination> destIterator = rift.getDestinations().listIterator();
            while (destIterator.hasNext()) {
                WeightedRiftDestination wdest = destIterator.next();
                RiftDestination dest = wdest.getDestination();
                if (dest.getType() == RiftDestination.EnumType.POCKET_ENTRANCE) {
                    destIterator.remove();
                    if (index == selectedEntranceIndex) {
                        entrance = new Location(rift.getWorld(), rift.getPos());
                        PocketRegistry.getForDim(dimID).markDirty();
                        List<WeightedRiftDestination> ifDestinations = ((RiftDestination.PocketEntranceDestination) dest).getIfDestinations();
                        for (WeightedRiftDestination ifDestination : ifDestinations) {
                            destIterator.add(new WeightedRiftDestination(ifDestination.getDestination(), ifDestination.getWeight() / wdest.getWeight(), ifDestination.getGroup()));
                            destIterator.previous(); // An entrance destination shouldn't be in an if/otherwise destination, but just in case, pass over it too
                        }
                    } else {
                        List<WeightedRiftDestination> otherwiseDestinations = ((RiftDestination.PocketEntranceDestination) dest).getOtherwiseDestinations();
                        for (WeightedRiftDestination otherwiseDestination : otherwiseDestinations) {
                            destIterator.add(new WeightedRiftDestination(otherwiseDestination.getDestination(), otherwiseDestination.getWeight() / wdest.getWeight(), otherwiseDestination.getGroup()));
                            destIterator.previous(); // An entrance destination shouldn't be in an if/otherwise destination, but just in case, pass over it too
                        }
                    }
                    index++;
                }
            }
        }

        // set virtual locations and register rifts
        for (TileEntityRift rift : rifts) {
            // TODO: put a rift on door break?
            rift.setVirtualLocation(virtualLocation);
            rift.register();
        }
    }

    public void linkPocketTo(RiftDestination linkTo) {
        List<TileEntityRift> rifts = getRifts();

        // Link pocket exits back
        for (TileEntityRift rift : rifts) {
            ListIterator<WeightedRiftDestination> destIterator = rift.getDestinations().listIterator();
            while (destIterator.hasNext()) {
                WeightedRiftDestination wdest = destIterator.next();
                RiftDestination dest = wdest.getDestination();
                if (dest.getType() == RiftDestination.EnumType.POCKET_EXIT) {
                    destIterator.remove();
                    destIterator.add(new WeightedRiftDestination(linkTo.withOldDestination(dest), wdest.getWeight(), wdest.getGroup()));
                    rift.markStateChanged();
                    rift.markDirty();
                }
            }
        }
    }

    public void unlinkPocket() {
        // TODO
    }

    // TODO: method to erase a pocket
}

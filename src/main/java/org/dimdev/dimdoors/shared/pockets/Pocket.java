package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.ddutils.nbt.SavedToNBT;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.rifts.*;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.ddutils.Location;

import java.util.*;

import org.dimdev.ddutils.math.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

@SavedToNBT public class Pocket implements INBTStorable { // TODO: better visibilities

    @SavedToNBT @Getter /*package-private*/ int id;
    @SavedToNBT @Getter /*package-private*/ int x; // Grid x TODO: rename to gridX and gridY
    @SavedToNBT @Getter /*package-private*/ int z; // Grid y
    @SavedToNBT @Getter @Setter /*package-private*/ int size; // In chunks TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @SavedToNBT @Getter @Setter /*package-private*/ VirtualLocation virtualLocation; // The non-pocket dimension from which this dungeon was created
    @SavedToNBT @Getter @Setter /*package-private*/ Location entrance;
    @SavedToNBT @Getter /*package-private*/ List<Location> riftLocations;

    @Getter int dim; // Not saved

    public Pocket() {}

    public Pocket(int id, int dim, int x, int z) {
        this.id = id;
        this.dim = dim;
        this.x = x;
        this.z = z;
        riftLocations = new ArrayList<>();
    }

    // TODO: make these static?
    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    boolean isInBounds(BlockPos pos) {
        // pocket bounds
        int gridSize = PocketRegistry.getForDim(dim).getGridSize();
        int minX = x * gridSize;
        int minZ = z * gridSize;
        int maxX = minX + (size + 1) * 16;
        int maxZ = minX + (size + 1) * 16;
        return minX <= pos.getX() && minZ <= pos.getZ() && pos.getX() < maxX && pos.getZ() < maxZ;
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
                if (weightedPocketEntranceDest.getDestination() instanceof PocketEntranceDestination) {
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
                if (dest instanceof PocketEntranceDestination) {
                    destIterator.remove();
                    if (index == selectedEntranceIndex) {
                        entrance = new Location(rift.getWorld(), rift.getPos());
                        PocketRegistry.getForDim(dim).markDirty();
                        List<WeightedRiftDestination> ifDestinations = ((PocketEntranceDestination) dest).getIfDestinations();
                        for (WeightedRiftDestination ifDestination : ifDestinations) {
                            destIterator.add(new WeightedRiftDestination(ifDestination.getDestination(), ifDestination.getWeight() / wdest.getWeight(), ifDestination.getGroup()));
                            destIterator.previous(); // An entrance destination shouldn't be in an if/otherwise destination, but just in case, pass over it too
                        }
                    } else {
                        List<WeightedRiftDestination> otherwiseDestinations = ((PocketEntranceDestination) dest).getOtherwiseDestinations();
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
                if (dest instanceof PocketExitDestination) {
                    destIterator.remove();
                    if (rift.isRegistered()) dest.unregister(rift);
                    destIterator.add(new WeightedRiftDestination(linkTo, wdest.getWeight(), wdest.getGroup(), dest));
                    if (rift.isRegistered()) linkTo.register(rift);
                    if (rift instanceof TileEntityEntranceRift && !rift.isAlwaysDelete()) {
                        ((TileEntityEntranceRift) rift).setPlaceRiftOnBreak(true); // We modified the door's state
                    }
                    rift.markDirty();
                }
            }
        }
    }

    public void unlinkPocket() {
        // TODO
    }

    public BlockPos getOrigin() {
        int gridSize = PocketRegistry.getForDim(dim).getGridSize();
        return new BlockPos(x * gridSize * 16, 0, z * gridSize * 16); // TODO: configurable yBase?
    }

    // TODO: method to erase a pocket
}

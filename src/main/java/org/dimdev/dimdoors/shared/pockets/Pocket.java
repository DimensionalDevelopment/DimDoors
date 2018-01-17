package org.dimdev.dimdoors.shared.pockets;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.shared.VirtualLocation;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketEntranceDestination;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketExitDestination;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NBTSerializable public class Pocket implements INBTStorable { // TODO: better visibilities

    @Saved @Getter protected int id;
    @Saved @Getter protected int x; // Grid x TODO: rename to gridX and gridY, or just convert to non-grid dependant coordinates
    @Saved @Getter protected int z; // Grid y
    @Saved @Getter @Setter protected int size; // In chunks TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @Saved @Getter @Setter protected VirtualLocation virtualLocation;
    @Saved @Getter @Setter protected Location entrance; // TODO: move this to the rift registry (pocketlib)
    @Saved @Getter protected List<Location> riftLocations; // TODO: convert to a list of all tile entities (for chests, and to make it independant of pocketlib)

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
        int gridSize = PocketRegistry.instance(dim).getGridSize();
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

        HashMap<TileEntityRift, Float> entranceIndexWeights = new HashMap<>();

        for (TileEntityRift rift : rifts) { // Find an entrance
            if (rift.getDestination() instanceof PocketEntranceDestination) {
                entranceIndexWeights.put(rift, ((PocketEntranceDestination) rift.getDestination()).getWeight());
                rift.markDirty();
            }
        }

        if (entranceIndexWeights.size() == 0) return;
        TileEntityRift selectedEntrance = MathUtils.weightedRandom(entranceIndexWeights);

        // Replace entrances with appropriate destinations
        for (TileEntityRift rift : rifts) {
            RiftDestination dest = rift.getDestination();
            if (dest instanceof PocketEntranceDestination) {
                if (rift == selectedEntrance) {
                    entrance = new Location(rift.getWorld(), rift.getPos());
                    PocketRegistry.instance(dim).markDirty();
                    rift.setDestination(((PocketEntranceDestination) dest).getIfDestination());
                } else {
                    rift.setDestination(((PocketEntranceDestination) dest).getOtherwiseDestination());
                }
            }
        }

        // register the rifts
        for (TileEntityRift rift : rifts) {
            rift.register();
        }
    }

    public void linkPocketTo(RiftDestination linkTo, LinkProperties linkProperties) {
        List<TileEntityRift> rifts = getRifts();

        // Link pocket exits back
        for (TileEntityRift rift : rifts) {
            RiftDestination dest = rift.getDestination();
            if (dest instanceof PocketExitDestination) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                rift.setDestination(linkTo);
                if (rift instanceof TileEntityEntranceRift && !rift.isAlwaysDelete()) {
                    ((TileEntityEntranceRift) rift).setPlaceRiftOnBreak(true); // We modified the door's state
                }
                rift.markDirty();
            }
        }
    }

    public BlockPos getOrigin() {
        int gridSize = PocketRegistry.instance(dim).getGridSize();
        return new BlockPos(x * gridSize * 16, 0, z * gridSize * 16); // TODO: configurable yBase?
    }
}

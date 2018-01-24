package org.dimdev.dimdoors.shared.pockets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.math.MathUtils;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.rifts.RiftDestination;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketEntranceMarker;
import org.dimdev.dimdoors.shared.rifts.destinations.PocketExitMarker;
import org.dimdev.dimdoors.shared.rifts.registry.LinkProperties;
import org.dimdev.dimdoors.shared.rifts.registry.RiftRegistry;
import org.dimdev.dimdoors.shared.tileentities.TileEntityEntranceRift;
import org.dimdev.pocketlib.Pocket;
import org.dimdev.pocketlib.PocketRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Robijnvogel
 */
@AllArgsConstructor @RequiredArgsConstructor
public class PocketTemplate {

    @Getter private final String group;
    @Getter private final String id;
    @Getter private final String type;
    @Getter private final String name;
    @Getter private final String author;
    @Getter @Setter private Schematic schematic;
    @Getter private final int size; // number of chunks (16 blocks) on each side - 1
    @Getter private final int baseWeight;

    public float getWeight(int depth) {
        //noinspection IfStatementWithIdenticalBranches
        if (depth == -1) {
            return baseWeight;
        } else {
            return baseWeight; // TODO: make this actually dependend on the depth
        }
    }

    public void place(Pocket pocket) {
        pocket.setSize(size);
        int gridSize = PocketRegistry.instance(pocket.getDim()).getGridSize();
        int dim = pocket.getDim();
        WorldServer world = WorldUtils.getWorld(dim);
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;

        // Place the schematic
        DimDoors.log.info("Placing new pocket using schematic " + id + " at x = " + xBase + ", z = " + zBase);
        Schematic.place(schematic, world, xBase, yBase, zBase);
    }

    public void setup(Pocket pocket, RiftDestination linkTo, LinkProperties linkProperties) {
        int gridSize = PocketRegistry.instance(pocket.getDim()).getGridSize();
        int dim = pocket.getDim();
        WorldServer world = WorldUtils.getWorld(dim);
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;

        List<TileEntityRift> rifts = new ArrayList<>();

        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(
                    xBase + tileEntityNBT.getInteger("x"),
                    yBase + tileEntityNBT.getInteger("y"),
                    zBase + tileEntityNBT.getInteger("z"));
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof TileEntityRift) {
                DimDoors.log.info("Rift found in schematic at " + pos);
                rifts.add((TileEntityRift) tile);
            } else if (tile instanceof IInventory) {
                IInventory inventory = (IInventory) tile;
                if (inventory.isEmpty()) {
                    if (tile instanceof TileEntityChest || tile instanceof TileEntityDispenser) {
                        LootTable table;
                        if (tile instanceof TileEntityChest) {
                            DimDoors.log.info("Now populating chest.");
                            table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dungeon_chest"));
                        } else { //(tile instanceof TileEntityDispenser)
                            DimDoors.log.info("Now populating dispenser.");
                            table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dispenser_projectiles"));
                        }
                        LootContext ctx = new LootContext.Builder(world).build();
                        table.fillInventory(inventory, world.rand, ctx);
                        DimDoors.log.info("Inventory should be populated now. Chest is: " + (inventory.isEmpty() ? "emtpy." : "filled."));
                        if (inventory.isEmpty()) {
                            DimDoors.log.error(", however Inventory is: emtpy!");
                        }
                    }
                }
            }
        }

        // Find an entrance

        HashMap<TileEntityRift, Float> entranceWeights = new HashMap<>();

        for (TileEntityRift rift : rifts) { // Find an entrance
            if (rift.getDestination() instanceof PocketEntranceMarker) {
                entranceWeights.put(rift, ((PocketEntranceMarker) rift.getDestination()).getWeight());
            }
        }

        if (entranceWeights.size() == 0) {
            DimDoors.log.warn("Pocket had no possible entrance in schematic!");
            return;
        }
        TileEntityRift selectedEntrance = MathUtils.weightedRandom(entranceWeights);

        // Replace entrances with appropriate destinations
        for (TileEntityRift rift : rifts) {
            RiftDestination dest = rift.getDestination();
            if (dest instanceof PocketEntranceMarker) {
                if (rift == selectedEntrance) {
                    PocketRegistry.instance(dim).markDirty();
                    rift.setDestination(((PocketEntranceMarker) dest).getIfDestination());
                    rift.register();
                    RiftRegistry.instance().addPocketEntrance(pocket, new Location(rift.getWorld(), rift.getPos()));
                } else {
                    rift.setDestination(((PocketEntranceMarker) dest).getOtherwiseDestination());
                }
            }
        }

        // Link pocket exits back
        for (TileEntityRift rift : rifts) {
            RiftDestination dest = rift.getDestination();
            if (dest instanceof PocketExitMarker) {
                if (linkProperties != null) rift.setProperties(linkProperties);
                rift.setDestination(rift.getProperties() == null || !rift.getProperties().oneWay ? linkTo : null);
                if (rift instanceof TileEntityEntranceRift && !rift.isAlwaysDelete()) {
                    ((TileEntityEntranceRift) rift).setPlaceRiftOnBreak(true); // We modified the door's state
                }
            }
        }

        // register the rifts
        for (TileEntityRift rift : rifts) {
            rift.register();
            rift.markDirty();
        }
    }
}

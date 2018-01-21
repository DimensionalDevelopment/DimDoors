package org.dimdev.dimdoors.shared.pockets;

import org.dimdev.ddutils.WorldUtils;
import org.dimdev.dimdoors.shared.rifts.TileEntityRift;
import org.dimdev.ddutils.Location;
import org.dimdev.ddutils.schem.Schematic;
import org.dimdev.dimdoors.DimDoors;

import java.util.ArrayList;

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
    @Getter private final int size; // size in chunks (n*n chunks)
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
        int gridSize = PocketRegistry.instance(pocket.dim).getGridSize();
        int dim = pocket.dim;
        int xBase = pocket.getX() * gridSize * 16;
        int yBase = 0;
        int zBase = pocket.getZ() * gridSize * 16;
        DimDoors.log.info("Placing new pocket using schematic " + schematic.name + " at x = " + xBase + ", z = " + zBase);

        WorldServer world = WorldUtils.getWorld(dim);
        Schematic.place(schematic, world, xBase, yBase, zBase);

        // Set pocket riftLocations
        pocket.riftLocations = new ArrayList<>();
        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(
                    xBase + tileEntityNBT.getInteger("x"),
                    yBase + tileEntityNBT.getInteger("y"),
                    zBase + tileEntityNBT.getInteger("z"));
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityRift) {
                DimDoors.log.info("Rift found in schematic at " + pos);
                pocket.riftLocations.add(new Location(world, pos));
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
    }
}

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
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
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
            } else if (tile instanceof TileEntityChest) {
                DimDoors.log.info("Now populating chest.");
                TileEntityChest chest = (TileEntityChest) tile;
                LootTable table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dungeon_chest"));
                LootContext ctx = new LootContext.Builder(world).build();
                table.fillInventory(chest, world.rand, ctx);
                DimDoors.log.info("Chest should be populated now. Chest is: " + (chest.isEmpty() ? "emtpy." : "filled."));
            } else if (tile instanceof TileEntityDispenser) {
                DimDoors.log.info("Now populating dispenser.");
                TileEntityDispenser dispenser = (TileEntityDispenser) tile;
                IBlockState blockState = world.getBlockState(pos);
                if (!blockState.getBlock().equals(Blocks.DISPENSER)) {
                    DimDoors.log.error("Wanted to place a TileEntityDispenser at a position, after generating a schematic, but the block doesn't seem to be a dispenser. Something is terribly wrong!");
                } else {
                    LootTable table;
                    IBlockState fireState1 = Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN).withProperty(BlockDispenser.TRIGGERED, false);
                    IBlockState fireState2 = Blocks.DISPENSER.getDefaultState().withProperty(BlockDispenser.FACING, EnumFacing.DOWN).withProperty(BlockDispenser.TRIGGERED, true);
                    if (blockState.equals(fireState1) || blockState.equals(fireState2)) {
                        table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dispenser_fire"));
                    } else {
                        table = world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(DimDoors.MODID + ":dispenser_projectiles"));
                    }
                    LootContext ctx = new LootContext.Builder(world).build();
                    table.fillInventory(dispenser, world.rand, ctx);
                }
                DimDoors.log.info("Dispenser should be populated now. Dispenser is: " + (dispenser.isEmpty() ? "emtpy." : "filled."));
            }
        }
    }
}

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

/**
 *
 * @author Robijnvogel
 */
@AllArgsConstructor @RequiredArgsConstructor// TODO: use @Builder?
public class PocketTemplate {

    @Getter private final String groupName;
    @Getter private final String name;
    @Getter @Setter private Schematic schematic;
    @Getter private final int size; // size in chunks (n*n chunks)
    @Getter private final int minDepth;
    @Getter private final int maxDepth;
    private final float[] weights; // weight per-level

    public float getWeight(int depth) {
        if (depth < 0) return 100; // TODO: get rid of this later
        if (maxDepth - minDepth + 1 != weights.length) throw new IllegalStateException("This PocetTemplate wasn't set up correctly!");
        if (depth < minDepth) return 0;
        if (depth > maxDepth) return weights[weights.length - 1];
        return weights[depth - minDepth];
    }

    public void place(Pocket pocket, int yBase) {
        pocket.setSize(size);
        int gridSize = PocketRegistry.getForDim(pocket.dim).getGridSize();
        int dim = pocket.dim;
        int xBase = pocket.getX() * gridSize * 16;
        int zBase = pocket.getZ() * gridSize * 16;
        DimDoors.log.info("Placing new pocket using schematic " + schematic.schematicName + " at x = " + xBase + ", z = " + zBase);

        WorldServer world = WorldUtils.getWorld(dim);
        Schematic.place(schematic, world, xBase, yBase, zBase);

        // Set pocket riftLocations
        pocket.riftLocations = new ArrayList<>();
        for (NBTTagCompound tileEntityNBT : schematic.tileEntities) {
            BlockPos pos = new BlockPos(
                    xBase + tileEntityNBT.getInteger("x"),
                    yBase + tileEntityNBT.getInteger("y"),
                    zBase + tileEntityNBT.getInteger("z"));
            if (world.getTileEntity(pos) instanceof TileEntityRift) {
                DimDoors.log.info("Rift found in schematic at " + pos);
                pocket.riftLocations.add(new Location(world, pos));
            }
        }
    }
}

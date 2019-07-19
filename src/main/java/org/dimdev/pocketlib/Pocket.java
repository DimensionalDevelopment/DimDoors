package org.dimdev.pocketlib;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.NBTSerializable;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.ddutils.WorldUtils;
import org.dimdev.ddutils.nbt.INBTStorable;
import org.dimdev.ddutils.nbt.NBTUtils;
import org.dimdev.dimdoors.DimDoors;
import org.dimdev.dimdoors.shared.blocks.ModBlocks;

import java.util.Arrays;
import java.util.Collections;

@NBTSerializable public class Pocket implements INBTStorable {
    @Saved @Getter protected int id;
    @Saved @Getter protected int x; // Grid x TODO: convert to non-grid dependant coordinates
    @Saved @Getter protected int z; // Grid y
    @Saved @Getter @Setter protected int size; // TODO: non chunk-based size, better bounds such as minX, minZ, maxX, maxZ, etc.
    @Saved @Getter @Setter protected VirtualLocation virtualLocation;
    @Saved protected EnumDyeColor dyedColor = EnumDyeColor.WHITE;
    @Saved protected EnumDyeColor color;
    @Saved protected int count = 0;

    @Getter int dim; // Not saved

    public Pocket() {}

    public Pocket(int id, int dim, int x, int z) {
        this.id = id;
        this.dim = dim;
        this.x = x;
        this.z = z;
    }

    @Override public void readFromNBT(NBTTagCompound nbt) { NBTUtils.readFromNBT(this, nbt); }
    @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) { return NBTUtils.writeToNBT(this, nbt); }

    boolean isInBounds(BlockPos pos) {
        // pockets bounds
        int gridSize = PocketRegistry.instance(dim).getGridSize();
        int minX = x * gridSize;
        int minZ = z * gridSize;
        int maxX = minX + (size + 1) * 16;
        int maxZ = minX + (size + 1) * 16;
        return minX <= pos.getX() && minZ <= pos.getZ() && pos.getX() < maxX && pos.getZ() < maxZ;
    }

    public BlockPos getOrigin() {
        int gridSize = PocketRegistry.instance(dim).getGridSize();
        return new BlockPos(x * gridSize * 16, 0, z * gridSize * 16);
    }

    public boolean addDye(Entity entity, EnumDyeColor color) {
        int maxDye = amountOfDyeRequiredToColor(this);

        if(this.dyedColor == color) {
            DimDoors.sendTranslatedMessage(entity, "dimdoors.pockets.dyeAlreadyAbsorbed");
            return false;
        }

        if(this.color != null && this.color == color) {
            if(count+1 > amountOfDyeRequiredToColor(this)) {
                dyedColor = color;
                this.color = null;
                this.count = 0;
                DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.pocketHasBeenDyed", dyedColor);
                return true;
            } else {
                count++;
                DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.remainingNeededDyes", count, maxDye, color);
                return true;
            }
        } else {
            this.color = color;
            count = 1;
            DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.remainingNeededDyes", count, maxDye, color);
            return true;
        }
    }

    /*private void repaint(EnumDyeColor dyeColor) {
        short size = (short) ((this.size + 1) * 16 - 1);
        BlockPos origin = getOrigin();
        World world = WorldUtils.getWorld(dim);
        IBlockState innerWall = ModBlocks.FABRIC.getDefaultState()..withProperty(..., dyeColor); // <-- forgot the exact name of the color property
        IBlockState outerWall = ModBlocks.ETERNAL_FABRIC.getDefaultState().withProperty(..., dyeColor);

        for (int x = origin.getX(); x < origin.getX() + size; x++) {
            for (int y = origin.getY(); y < origin.getY() + size; y++) {
                for (int z = origin.getZ(); z < origin.getZ() + size; z++) {
                    int layer = Collections.min(Arrays.asList(x, y, z, size - 1 - x, size - 1 - y, size - 1 - z));
                    if (layer == 0) {
                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.ETERNAL_FABRIC) {
                            world.setBlockState(x, y, z, outerWall);
                        }
                    } else if (layer < 5) {
                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.FABRIC) {
                            world.setBlockState(x, y, z, innerWall);
                        }
                    }
                }
            }
        }

        return schematic;
    }*/

    private static int amountOfDyeRequiredToColor(Pocket pocket) {
        int s = 16 * pocket.getSize();

        return (int) ((Math.pow(s, 3) - Math.pow(s - 10, 3))/1106);
    }
}

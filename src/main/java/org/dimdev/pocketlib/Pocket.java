package org.dimdev.pocketlib;

import net.minecraft.entity.Entity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.DimDoors;

public final class Pocket {
    @Saved public final int id;
    @Saved public BlockBox box;
    @Saved public VirtualLocation virtualLocation;
    @Saved public DyeColor dyeColor = DyeColor.WHITE;
    @Saved public DyeColor nextDyeColor = null;
    @Saved public int count = 0;
    public World world;

    boolean isInBounds(BlockPos pos) {
        return box.contains(pos);
    }

    public BlockPos getOrigin() {
        return new BlockPos(box.minX, box.minY, box.minZ);
    }

    public boolean addDye(Entity entity, DyeColor color) {
        int maxDye = amountOfDyeRequiredToColor(this);

        if (dyeColor == color) {
            DimDoors.sendTranslatedMessage(entity, "dimdoors.pockets.dyeAlreadyAbsorbed");
            return false;
        }

        if (this.nextDyeColor != null && this.nextDyeColor == color) {
            if (count + 1 > amountOfDyeRequiredToColor(this)) {
                dyeColor = color;
                this.nextDyeColor = null;
                count = 0;
                DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.pocketHasBeenDyed", dyeColor);
                return true;
            } else {
                count++;
                DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.remainingNeededDyes", count, maxDye, color);
                return true;
            }
        } else {
            this.nextDyeColor = color;
            count = 1;
            DimDoors.sendTranslatedMessage(entity, "dimdoors.pocket.remainingNeededDyes", count, maxDye, color);
            return true;
        }
    }

    /*private void repaint(DyeColor dyeColor) {
        short size = (short) ((this.size + 1) * 16 - 1);
        BlockPos origin = getOrigin();
        World world = WorldUtils.getWorld(dim);
        BlockState innerWall = ModBlocks.FABRIC.getDefaultState()..withProperty(..., dyeColor); // <-- forgot the exact name of the color property
        BlockState outerWall = ModBlocks.ETERNAL_FABRIC.getDefaultState().withProperty(..., dyeColor);

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

        return (int) ((Math.pow(s, 3) - Math.pow(s - 10, 3)) / 1106);
    }
}

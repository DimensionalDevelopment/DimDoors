package org.dimdev.dimdoors.world.pocket;

import com.flowpowered.math.vector.Vector3i;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.dimdoors.util.Codecs;
import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public final class Pocket {
    public static final Codec<Pocket> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Codec.INT.fieldOf("id").forGetter(a -> a.id),
                Codecs.BLOCK_BOX.fieldOf("box").forGetter(a -> a.box),
                VirtualLocation.CODEC.fieldOf("virtualLocation").forGetter(a -> a.virtualLocation),
                Codecs.DYE_COLOR.fieldOf("dyeColor").forGetter(a -> a.dyeColor),
                Codecs.DYE_COLOR.optionalFieldOf("nextDyeColor", null).forGetter(a -> a.nextDyeColor),
                Codec.INT.fieldOf("count").forGetter(a -> a.count)
        ).apply(instance, Pocket::new);
    });
    private static final int BLOCKS_PAINTED_PER_DYE = 1106;

    @Saved
    public final int id;
    @Saved
    public BlockBox box;
    @Saved
    public VirtualLocation virtualLocation;
    @Saved
    public DyeColor dyeColor = DyeColor.WHITE;
    @Saved
    public DyeColor nextDyeColor = null;
    @Saved
    public int count = 0;

    public RegistryKey<World> world;

    private Pocket(int id, BlockBox box, VirtualLocation virtualLocation, DyeColor dyeColor, DyeColor nextDyeColor, int count) {
        this.id = id;
        this.box = box;
        this.virtualLocation = virtualLocation;
        this.dyeColor = dyeColor;
        this.nextDyeColor = nextDyeColor;
        this.count = count;
    }

    public Pocket(int id, RegistryKey<World> world, int x, int z) {
        this.id = id;
        this.world = world;
        box = new BlockBox(x * 16, 0, z * 16, (x + 1) * 16, 0, (z + 1) * 16);
    }

    boolean isInBounds(BlockPos pos) {
        return box.contains(pos);
    }

    public BlockPos getOrigin() {
        return new BlockPos(box.minX, box.minY, box.minZ);
    }

    public boolean addDye(Entity entity, DyeColor color) {
        int maxDye = amountOfDyeRequiredToColor(this);

        if (dyeColor == color) {
            EntityUtils.chat(entity, new TranslatableText("dimdoors.pockets.dyeAlreadyAbsorbed"));
            return false;
        }

        if (this.nextDyeColor != null && this.nextDyeColor == color) {
            if (count + 1 > amountOfDyeRequiredToColor(this)) {
                dyeColor = color;
                this.nextDyeColor = null;
                count = 0;
                EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.pocketHasBeenDyed", dyeColor));
                return true;
            } else {
                count++;
                EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", count, maxDye, color));
                return true;
            }
        } else {
            this.nextDyeColor = color;
            count = 1;
            EntityUtils.chat(entity, new TranslatableText("dimdoors.pocket.remainingNeededDyes", count, maxDye, color));
            return true;
        }
    }

//    private void repaint(DyeColor dyeColor) {
//        BlockPos origin = getOrigin();
//        World world = WorldUtils.getWorld(dim);
//        BlockState innerWall = ModBlocks.getDefaultState()..withProperty(..., dyeColor); // <-- forgot the exact name of the color property
//        BlockState outerWall = ModBlocks.ETERNAL_FABRIC.getDefaultState().withProperty(..., dyeColor);
//
//        for (int x = origin.getX(); x < origin.getX() + size; x++) {
//            for (int y = origin.getY(); y < origin.getY() + size; y++) {
//                for (int z = origin.getZ(); z < origin.getZ() + size; z++) {
//                    int layer = Collections.min(Arrays.asList(x, y, z, size - 1 - x, size - 1 - y, size - 1 - z));
//                    if (layer == 0) {
//                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.ETERNAL_FABRIC) {
//                            world.setBlockState(x, y, z, outerWall);
//                        }
//                    } else if (layer < 5) {
//                        if (world.getBlockState(x, y, z).getBlock() == ModBlocks.FABRIC) {
//                            world.setBlockState(x, y, z, innerWall);
//                        }
//                    }
//                }
//            }
//        }
//
//        return schematic;
//    }

    private static int amountOfDyeRequiredToColor(Pocket pocket) {
        int outerVolume = pocket.box.getBlockCountX() * pocket.box.getBlockCountY() * pocket.box.getBlockCountZ();
        int innerVolume = (pocket.box.getBlockCountX() - 5) * (pocket.box.getBlockCountY() - 5) * (pocket.box.getBlockCountZ() - 5);

        return (outerVolume - innerVolume) / BLOCKS_PAINTED_PER_DYE;
    }

    public void setSize(int x, int y, int z) {
        box = new BlockBox(box.minX, box.minY, box.minZ, box.minX + x, box.minY + y, box.minZ + z);
    }

    public Vector3i getSize() {
        Vec3i dimensions = box.getDimensions();
        return new Vector3i(dimensions.getX(), dimensions.getY(), dimensions.getZ());
    }
}

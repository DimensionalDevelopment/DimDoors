package org.dimdev.dimdoors.world.pocket.type.addon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.EntityUtils;
import org.dimdev.dimdoors.block.AncientFabricBlock;
import org.dimdev.dimdoors.block.FabricBlock;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PocketColor;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.HashMap;

public class DyeableAddon implements PocketAddon {
    public static Identifier ID = DimensionalDoors.id("dyeable");
    public static final MapCodec<DyeableAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    PocketColor.CODEC.fieldOf("dyeColor").forGetter(a -> a.dyeColor),
                    PocketColor.CODEC.fieldOf("nextDyeColor").forGetter(a -> a.nextDyeColor),
                    Codec.INT.fieldOf("count").forGetter(a -> a.count)
            ).apply(instance, DyeableAddon::new)

    );

    public DyeableAddon() {
        this(PocketColor.WHITE);
    }

    public DyeableAddon(PocketColor dyeColor) {
        this(dyeColor, PocketColor.NONE);
    }

    public DyeableAddon(PocketColor dyeColor, PocketColor nextDyeColor) {
        this(dyeColor, nextDyeColor, 0);
    }

    public DyeableAddon(PocketColor dyeColor, PocketColor nextDyeColor, int count) {
        this.dyeColor = dyeColor;
        this.nextDyeColor = nextDyeColor;
    }

    protected PocketColor dyeColor = PocketColor.WHITE;
    private PocketColor nextDyeColor = PocketColor.NONE;
    private int count = 0;

    private static int amountOfDyeRequiredToColor(Pocket<?, ?> pocket) {
        int outerVolume = pocket.getBox().getYSpan() * pocket.getBox().getZSpan() * pocket.getBox().getXSpan();

        return Math.max(outerVolume / DimensionalDoors.getConfig().getPocketsConfig().blocksColoredPerDye, 1);
    }

    private void repaint(Pocket<?, ?> pocket, DyeColor dyeColor) {
        Level serverWorld = DimensionalDoors.getWorld(pocket.getWorld());

        BlockState innerWall = ModBlocks.fabricFromDye(dyeColor).defaultBlockState();
        BlockState outerWall = ModBlocks.ancientFabricFromDye(dyeColor).defaultBlockState();

        var box = pocket.getBox();
        int minX = box.minX();
        int minChunkX = minX >> 4;
        int minZ = box.minZ();
        int minChunkZ = minZ >> 4;
        int minY = box.minY();
        int minChunkY = minY >> 4;


        int xSpan = box.getXSpan();
        int xChunkSpan = xSpan >> 4;
        int ySpan = box.getXSpan();
        int yChunkSpan = ySpan >> 4;
        int zSpan = box.getXSpan();
        int zChunkSpan = zSpan >> 4;

        for (int chunkX = 0; chunkX <= xChunkSpan; chunkX++) {

            for (int chunkZ = 0; chunkZ <= zChunkSpan; chunkZ++) {

                var chunk = serverWorld.getChunk(minChunkX + chunkX, minChunkZ + chunkZ);
                boolean changed = false;

                for (int sectionY = 0; sectionY <= yChunkSpan; sectionY++) {

                    int sectionIndex = chunk.getSectionIndexFromSectionY(minChunkY + sectionY);
                    var section = chunk.getSection(sectionIndex);

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                BlockState state = section.getBlockState(x, y, z);
                                Block block = state.getBlock();

                                BlockState replacement = switch (block) {
                                    case AncientFabricBlock ignored -> outerWall;
                                    case FabricBlock ignored -> innerWall;
                                    default -> null;
                                };

                                if (replacement != null) {
                                    section.setBlockState(x, y, z, replacement);
                                    changed = true;
                                }
                            }
                        }
                    }
                }

                if (changed) {
                    chunk.markUnsaved();
                }
            }
        }
    }

    public int addDye(Pocket<?, ?> pocket, Entity entity, DyeColor dyeColor, int count) {
        PocketColor color = PocketColor.from(dyeColor);

        int maxDye = amountOfDyeRequiredToColor(pocket);

        if (count <= 0) {
            return count;
        }

        if (this.dyeColor == color) {
            EntityUtils.chat(entity, Component.translatable("dimdoors.pocket.dyeAlreadyAbsorbed"));
            return count;
        }

        if (this.nextDyeColor != color) {
            this.nextDyeColor = color;
            this.count = 0;
        }

        int remainingNeeded = maxDye - this.count;
        int absorbed = Math.min(count, remainingNeeded);
        int remainingInStack = count - absorbed;

        this.count += absorbed;

        if (this.count >= maxDye) {
            repaint(pocket, dyeColor);

            this.dyeColor = color;
            this.nextDyeColor = PocketColor.NONE;
            this.count = 0;

            EntityUtils.chat(
                    entity,
                    Component.translatable(
                            "dimdoors.pocket.pocketHasBeenDyed",
                            dyeColor.getSerializedName()
                    )
            );
        } else {
            EntityUtils.chat(
                    entity,
                    Component.translatable(
                            "dimdoors.pocket.remainingNeededDyes",
                            this.count,
                            maxDye,
                            color.getSerializedName()
                    )
            );
        }

        return remainingInStack;
    }

    @Override
    public boolean applicable(Pocket pocket) {
        return pocket instanceof PrivatePocket;
    }

    @Override
    public PocketAddonType<?, ?> getType() {
        return PocketAddonType.DYEABLE_ADDON;
    }

    public interface DyeablePocketBuilder<T extends Pocket<T, P>, P extends Pocket.PocketBuilder<T, P>> extends PocketBuilderExtension<T, P> {
        default P dyeColor(PocketColor dyeColor) {

            this.<DyeableBuilderAddon>getAddon(PocketAddonType.DYEABLE_ADDON).dyeColor = dyeColor;

            return getSelf();
        }
    }

    public static class DyeableBuilderAddon implements PocketBuilderAddon<DyeableAddon, DyeableBuilderAddon> {

        public static MapCodec<DyeableBuilderAddon> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(PocketColor.CODEC.lenientOptionalFieldOf("dye_color", PocketColor.NONE).forGetter(a -> a.dyeColor)).apply(instance, DyeableBuilderAddon::new));

        private PocketColor dyeColor = PocketColor.NONE;

        public DyeableBuilderAddon() {
            this(PocketColor.NONE);
        }

        public DyeableBuilderAddon(PocketColor dyeColor) {
            this.dyeColor = dyeColor;
        }

        // TODO: add some Pocket#init so that we can have boolean shouldRepaintOnInit

        @Override
        public void apply(Pocket<?, ?> pocket) {
            DyeableAddon addon = new DyeableAddon(dyeColor);
            addon.dyeColor = dyeColor;
            pocket.addAddon(addon);
        }

        @Override
        public PocketAddonType<DyeableAddon, DyeableBuilderAddon> getType() {
            return PocketAddonType.DYEABLE_ADDON;
        }
    }

    public interface DyeablePocket extends AddonProvider {

    }
}

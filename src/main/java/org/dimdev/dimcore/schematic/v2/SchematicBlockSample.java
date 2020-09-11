package org.dimdev.dimcore.schematic.v2;

import java.util.Map;

import io.github.boogiemonster1o1.libcbe.api.ConditionalBlockEntityProvider;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;

public class SchematicBlockSample implements BlockView {
    public final Schematic schematic;
    private final int[][][] blockData;
    private final BiMap<BlockState, Integer> palette;
    private final Map<BlockPos, BlockState> container;
    private StructureWorldAccess world;

    public SchematicBlockSample(Schematic schematic) {
        this.schematic = schematic;
        this.blockData = SchematicPlacer.getBlockData(schematic, schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        this.palette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
        this.container = Maps.newHashMap();
        int width = schematic.getWidth();
        int height = schematic.getHeight();
        int length = schematic.getLength();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    this.container.put(new BlockPos(x, y, z), this.palette.inverse().get(this.blockData[x][y][z]));
                }
            }
        }
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        Block block = this.getBlockState(pos).getBlock();
        if (block.hasBlockEntity()) {
            if (block instanceof ConditionalBlockEntityProvider && ((ConditionalBlockEntityProvider) block).hasBlockEntity(this.getBlockState(pos)) && ((ConditionalBlockEntityProvider) block).hasBlockEntity(pos, this)) {
                return ((ConditionalBlockEntityProvider) block).createBlockEntity(this.world);
            } else {
                return ((BlockEntityProvider)block).createBlockEntity(this.world);
            }
        }
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return this.container.get(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.container.get(pos).getFluidState();
    }

    public void place(BlockPos origin) {
        if (this.world == null) {
            throw new UnsupportedOperationException("Can not place in a null world!");
        }
        for (Map.Entry<BlockPos, BlockState> entry : this.container.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            this.world.setBlockState(origin.add(pos), state, 2);
            this.world.toServerWorld().markDirty(origin.add(pos), null);
            this.world.toServerWorld().getChunkManager().markForUpdate(origin.add(pos));
            this.world.toServerWorld().getLightingProvider().checkBlock(origin.add(pos));
        }
    }

    public int[][][] getBlockData() {
        return this.blockData;
    }

    public BiMap<BlockState, Integer> getPalette() {
        return this.palette;
    }

    public Map<BlockPos, BlockState> getBlockContainer() {
        return this.container;
    }

    public StructureWorldAccess getWorld() {
        return this.world;
    }

    public SchematicBlockSample withWorld(StructureWorldAccess world) {
        this.world = world;
        return this;
    }
}

package org.dimdev.dimdoors.util.schematic;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RelativeBlockSample {
    public static boolean shouldUpdate = true;
    public final Schematic schematic;
    private final Map<BlockPos, BlockData> blockDataContainer;
    private final BiMap<CompoundTag, Vec3> entityContainer;

    private static class BlockData {
        public BlockState state;
        public CompoundTag blockEntity;
        public Biome biome;
    }

    public RelativeBlockSample(Schematic schematic) {
        this.schematic = schematic;
        int[][][] blockData = SchematicPlacer.getBlockData(schematic);
        int[][] biomeData = SchematicPlacer.getBiomeData(schematic);
        BiMap<BlockState, Integer> blockPalette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
        /*ImmutableBiMap.copyOf(schematic.getBiomePalette());*/
        BiMap<Biome, Integer> biomePalette = HashBiMap.create(0);
        blockDataContainer = Maps.newHashMap();
        int width = schematic.getWidth();
        int height = schematic.getHeight();
        int length = schematic.getLength();

        var hasBiomes = biomeData.length != 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {

                    var data = blockDataContainer.computeIfAbsent(new BlockPos(x, y, z), a -> new BlockData());
                    data.state = blockPalette.inverse().get(blockData[x][y][z]);
                    if(hasBiomes) data.biome = biomePalette.inverse().get(biomeData[x][z]);
                }
            }
        }

        for (CompoundTag blockEntityNbt : schematic.getBlockEntities()) {
            int[] arr = blockEntityNbt.getIntArray("Pos");
            BlockPos position = new BlockPos(arr[0], arr[1], arr[2]);

            blockDataContainer.computeIfAbsent(position, a -> new BlockData()).blockEntity = blockEntityNbt;
        }

        this.entityContainer = HashBiMap.create();
        for (CompoundTag entityNbt : schematic.getEntities()) {
            ListTag doubles = entityNbt.getList("Pos", Tag.TAG_DOUBLE);
            this.entityContainer.put(entityNbt, new Vec3(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)).subtract(Vec3.atLowerCornerOf(schematic.getOffset())));
        }
    }

    public void place(BlockPos origin, ServerLevel world, boolean biomes, HolderLookup.Provider provider) {
        shouldUpdate = false;

        this.blockDataContainer.forEach((pos, data) -> {
            if(data.state != null) {
                BlockPos actualPos = origin.offset(pos);

                world.setBlock(actualPos, data.state, 0);

                if(data.blockEntity != null) {
                    BlockEntity blockEntity = BlockEntity.loadStatic(actualPos, data.state, data.blockEntity, provider);
                    if (blockEntity != null) {
                        world.setBlockEntity(blockEntity);
                    }
                }
            }


        });

        shouldUpdate = true;


        for (Map.Entry<CompoundTag, Vec3> entry : this.entityContainer.entrySet()) {
            CompoundTag nbt = entry.getKey();

            ListTag doubles = nbt.getList("Pos", Tag.TAG_DOUBLE);
            Vec3 vec = entry.getValue().add(origin.getX(), origin.getY(), origin.getZ());
            doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x));
            doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y));
            doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z));
            nbt.put("Pos", doubles);
            Optional<Entity> entity = EntityType.create(nbt, world.getLevel());

            if(entity.isEmpty()) {
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            } else {
                world.addFreshEntity(entity.get());
            }

        }
    }
}
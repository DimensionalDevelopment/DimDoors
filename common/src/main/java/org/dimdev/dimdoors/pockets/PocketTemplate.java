package org.dimdev.dimdoors.pockets;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.NbtPlacer;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimcore.api.world.NbtPlacerUtil;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface PocketTemplate {
    void place(Pocket<?, ?> pocket);
    Vec3i getSize();

    record SchematicTemplate(Schematic schematic) implements PocketTemplate {
        public void place(Pocket<?, ?> pocket) {
            pocket.setSize(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
            ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
            BlockPos origin = pocket.getOrigin();
            SchematicPlacer.place(this.schematic, world, origin);
        }

        @Override
        public Vec3i getSize() {
            return new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());
        }
    }

    record NbtTemplate(NbtPlacerUtil nbt) implements PocketTemplate {

        @Override
        public void place(Pocket<?, ?> pocket) {
            pocket.setSize(nbt.sizeX, nbt.sizeY, nbt.sizeZ);
            ServerLevel world = DimensionalDoors.getWorld(pocket.getWorld());
            BlockPos origin = pocket.getOrigin();


            NbtPlacer.place(nbt, world, origin);
        }

        @Override
        public Vec3i getSize() {
            return nbt.sizeVector;
        }

        public static NbtPlacerUtil load(CompoundTag nbt) {
            ListTag paletteList = nbt.getList("palette", 10);
            HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>(paletteList.size());
            List<CompoundTag> paletteCompoundList = paletteList
                    .stream()
                    .filter(nbtElement -> nbtElement instanceof CompoundTag)
                    .map(element -> (CompoundTag) element)
                    .toList();

            for (int i = 0; i < paletteCompoundList.size(); i++) {
                palette.put(i, NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), paletteCompoundList.get(i)));
            }

            ListTag sizeList = nbt.getList("size", 3);
            BlockPos sizeVectorRotated = new BlockPos(sizeList.getInt(0), sizeList.getInt(1), sizeList.getInt(2));
            BlockPos sizeVector = new BlockPos(Math.abs(sizeVectorRotated.getX()), Math.abs(sizeVectorRotated.getY()), Math.abs(sizeVectorRotated.getZ()));
            ListTag positionsList = nbt.getList("blocks", 10);
            HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>> positions = new HashMap<BlockPos, Pair<BlockState, Optional<CompoundTag>>>(positionsList.size());
            List<Pair<BlockPos, Pair<BlockState, Optional<CompoundTag>>>> positionsPairList = positionsList
                    .stream()
                    .filter(nbtElement -> nbtElement instanceof CompoundTag)
                    .map(element -> (CompoundTag) element)
                    .map((nbtCompound) -> Pair
                            .of(new BlockPos(nbtCompound.getList("pos", 3).getInt(0), nbtCompound.getList("pos", 3).getInt(1),
                                            nbtCompound.getList("pos", 3).getInt(2)),
                                    Pair
                                            .of(palette.get(nbtCompound.getInt("state")),
                                                    nbtCompound.contains("nbt", Tag.TAG_COMPOUND)
                                                            ? Optional.of(nbtCompound.getCompound("nbt"))
                                                            : Optional.<CompoundTag>empty())))
                    .sorted(Comparator.comparing((pair) -> pair.getFirst().getX()))
                    .sorted(Comparator.comparing((pair) -> pair.getFirst().getY()))
                    .sorted(Comparator.comparing((pair) -> pair.getFirst().getZ()))
                    .toList();
            positionsPairList
                    .forEach((pair) -> positions
                            .put(pair.getFirst().subtract(positionsPairList.get(0).getFirst()), pair.getSecond()));
            return new NbtPlacerUtil(nbt, positions, nbt.getList("entities", 10), positionsPairList.get(0).getFirst(), sizeVector);
        }
    }
}
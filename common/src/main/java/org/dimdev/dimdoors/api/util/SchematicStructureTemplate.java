package org.dimdev.dimdoors.api.util;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.util.schematic.Schematic;
import org.dimdev.dimdoors.util.schematic.SchematicPlacer;

import java.util.List;
import java.util.stream.Collectors;

public class SchematicStructureTemplate extends StructureTemplate {
    public SchematicStructureTemplate(CompoundTag tag) {
        this(Schematic.fromNbt(tag));
    }

    public SchematicStructureTemplate(Schematic schematic) {
        this.size = new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());

        this.setAuthor(schematic.getMetadata().author());

        this.palettes.clear();
        this.entityInfoList.clear();

        loadPalette(schematic);

        schematic.getEntities().forEach(compoundTag -> {
            var array = compoundTag.getList("Pos", DoubleTag.TAG_DOUBLE);
                var vec3 = new Vec3(array.getDouble(0), array.getDouble(1), array.getDouble(2));
                var pos = new BlockPos((int) vec3.x(), (int) vec3.y(), (int) vec3.z());

                entityInfoList.add(new StructureEntityInfo(vec3, pos, compoundTag));
        });
    }

    private void loadPalette(Schematic schematic) {
        List<StructureBlockInfo> list = Lists.newArrayList();
        List<StructureBlockInfo> list2 = Lists.newArrayList();
        List<StructureBlockInfo> list3 = Lists.newArrayList();

        var blockEntities = schematic.getBlockEntities().stream().collect(Collectors.toMap(compoundTag -> {
            var array = compoundTag.getIntArray("Pos");
            return new BlockPos(array[0], array[1], array[2]);
        }, it -> it));

        var blockData = SchematicPlacer.getBlockData(schematic);
        var palleteList = schematic.getBlockPalette().inverse();
        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    var pos = new BlockPos(x, y, z);

                    var blockEntity = blockEntities.getOrDefault(pos, null);
                    var info = new StructureBlockInfo(pos, palleteList.get(blockData[x][y][z]), blockEntity);

                    addToLists(info, list, list2, list3);
                }
            }
        }

        List<StructureBlockInfo> list4 = buildInfoList(list, list2, list3);
        this.palettes.add(new Palette(list4));
    }

    public void loadFromSchematic(Schematic schematic) {

        this.palettes.clear();
        this.entityInfoList.clear();
        this.size = new Vec3i(schematic.getWidth(), schematic.getHeight(), schematic.getLength());

        var simplePalette = new SimplePalette();
    }

    private void loadPalette(HolderGetter<Block> blockGetter, ListTag paletteTag, ListTag blocksTag) {
        SimplePalette simplePalette = new SimplePalette();

        for(int i = 0; i < paletteTag.size(); ++i) {
            simplePalette.addMapping(NbtUtils.readBlockState(blockGetter, paletteTag.getCompound(i)), i);
        }

        List<StructureBlockInfo> list = Lists.newArrayList();
        List<StructureBlockInfo> list2 = Lists.newArrayList();
        List<StructureBlockInfo> list3 = Lists.newArrayList();

        for(int j = 0; j < blocksTag.size(); ++j) {
            CompoundTag compoundTag = blocksTag.getCompound(j);
            ListTag listTag = compoundTag.getList("pos", 3);
            BlockPos blockPos = new BlockPos(listTag.getInt(0), listTag.getInt(1), listTag.getInt(2));
            BlockState blockState = simplePalette.stateFor(compoundTag.getInt("state"));
            CompoundTag compoundTag2;
            if (compoundTag.contains("nbt")) {
                compoundTag2 = compoundTag.getCompound("nbt");
            } else {
                compoundTag2 = null;
            }

            StructureBlockInfo structureBlockInfo = new StructureBlockInfo(blockPos, blockState, compoundTag2);
            addToLists(structureBlockInfo, list, list2, list3);
        }

        List<StructureBlockInfo> list4 = buildInfoList(list, list2, list3);
        this.palettes.add(new Palette(list4));
    }
}

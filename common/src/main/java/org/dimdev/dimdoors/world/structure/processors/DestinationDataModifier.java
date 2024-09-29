package org.dimdev.dimdoors.world.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.dimdev.dimdoors.rift.targets.VirtualTarget;
import org.dimdev.dimdoors.world.ModStructureProccessors;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class DestinationDataModifier extends StructureProcessor {
    private final Map<Integer, CompoundTag> destinations;

    private DestinationDataModifier(Map<Integer, CompoundTag> destinations) {
        this.destinations = destinations;
    }

    public static DestinationDataModifier of(Map<Integer, VirtualTarget> destinations) {
        return new DestinationDataModifier(destinations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> VirtualTarget.toNbt(entry.getValue()))));
    }

    public static DestinationDataModifier of(int id, VirtualTarget data) {
        return of(Map.of(id, data));
    }

    public static DestinationDataModifier of(VirtualTarget data) {
        return of(0, data);
    }

    public static final MapCodec<DestinationDataModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING.xmap(Integer::parseInt, String::valueOf), CompoundTag.CODEC).fieldOf("destinations").forGetter(DestinationDataModifier::destinations)).apply(instance, DestinationDataModifier::new));

    public Map<Integer, CompoundTag> destinations() {
        return destinations;
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos offset, BlockPos pos, StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo, StructurePlaceSettings settings) {
        var data = apply(relativeBlockInfo.nbt());

        return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), data);
    }

    public CompoundTag apply(@Nullable CompoundTag tag) {
        if(tag != null && tag.contains("data")) {
            var data = tag.getCompound("data");

            var id = getMarkerId(data);
            if(id >= 0) {
                if(destinations.containsKey(id)) {
                    var nbt = destinations.get(id);

                    data.put("destination", nbt);
                }
            }
        }

        return tag;
    }

    @Override
    public StructureProcessorType<?> getType() {
        return ModStructureProccessors.DESTINATION_DATA.get();
    }

    private int getMarkerId(CompoundTag data) {
        var destination = data.getCompound("destination");
        if(destination.getString("type").equals("dimdoors:id_marker")) {
            return destination.getInt("id");
        }

        return -1;
    }
}

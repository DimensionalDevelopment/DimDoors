package org.dimdev.dimcore.schematic.v2;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;

@SuppressWarnings("CodeBlock2Expr")
public class Schematic {
    public static final Codec<Schematic> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("Version").forGetter(Schematic::getVersion),
                Codec.INT.fieldOf("Data Version").forGetter(Schematic::getDataVersion),
                SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::getMetadata),
                Codec.SHORT.fieldOf("Width").forGetter(Schematic::getWidth),
                Codec.SHORT.fieldOf("Height").forGetter(Schematic::getHeight),
                Codec.SHORT.fieldOf("Length").forGetter(Schematic::getLength),
                Vec3i.field_25123.fieldOf("Offset").forGetter(Schematic::getOffset),
                Codec.INT.fieldOf("PalleteMax").forGetter(Schematic::getPalleteMax),
                SchematicBlockPallete.CODEC.fieldOf("Palette").forGetter(Schematic::getBlockPallete),
                Codec.INT_STREAM.fieldOf("BlockData").forGetter(Schematic::getBlockData),
                Codec.list(CompoundTag.field_25128).fieldOf("BlockEntities").forGetter(Schematic::getBlockEntities),
                Codec.list(CompoundTag.field_25128).fieldOf("Entities").forGetter(Schematic::getEntities)
                ).apply(instance, Schematic::new);
    });

    private final int version;
    private final int dataVersion;
    private final SchematicMetadata metadata;
    private final short width;
    private final short height;
    private final short length;
    private final Vec3i offset;
    private final int palleteMax;
    private final Map<BlockState, Integer> blockPallete;
    private final IntStream blockData;
    private final List<CompoundTag> blockEntities;
    private final List<CompoundTag> entities;

    public Schematic(int version, int dataVersion, SchematicMetadata metadata, short width, short height, short length, Vec3i offset, int palleteMax, Map<BlockState, Integer> blockPallete, IntStream blockData, List<CompoundTag> blockEntities, List<CompoundTag> entities) {
        this.version = version;
        this.dataVersion = dataVersion;
        this.metadata = metadata;
        this.width = width;
        this.height = height;
        this.length = length;
        this.offset = offset;
        this.palleteMax = palleteMax;
        this.blockPallete = blockPallete;
        this.blockData = blockData;
        this.blockEntities = blockEntities;
        this.entities = entities;
    }

    public int getVersion() {
        return this.version;
    }

    public int getDataVersion() {
        return this.dataVersion;
    }

    public SchematicMetadata getMetadata() {
        return this.metadata;
    }

    public short getWidth() {
        return this.width;
    }

    public short getHeight() {
        return this.height;
    }

    public short getLength() {
        return this.length;
    }

    public Vec3i getOffset() {
        return this.offset;
    }

    public int getPalleteMax() {
        return this.palleteMax;
    }

    public Map<BlockState, Integer> getBlockPallete() {
        return this.blockPallete;
    }

    public IntStream getBlockData() {
        return this.blockData;
    }

    public List<CompoundTag> getBlockEntities() {
        return this.blockEntities;
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }
}

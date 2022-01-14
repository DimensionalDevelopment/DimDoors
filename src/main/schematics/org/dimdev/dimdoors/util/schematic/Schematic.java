package org.dimdev.dimdoors.util.schematic;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.SharedConstants;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

public class Schematic {
	private static final Consumer<String> PRINT_TO_STDERR = System.err::println;
	public static final Codec<Schematic> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
			Codec.INT.fieldOf("Version").forGetter(Schematic::getVersion),
			Codec.INT.optionalFieldOf("Data Version", SharedConstants.getGameVersion().getWorldVersion()).forGetter(Schematic::getDataVersion),
			SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::getMetadata),
			Codec.SHORT.fieldOf("Width").forGetter(Schematic::getWidth),
			Codec.SHORT.fieldOf("Height").forGetter(Schematic::getHeight),
			Codec.SHORT.fieldOf("Length").forGetter(Schematic::getLength),
			Vec3i.CODEC.fieldOf("Offset").forGetter(Schematic::getOffset),
			Codec.INT.fieldOf("PaletteMax").forGetter(Schematic::getPaletteMax),
			SchematicBlockPalette.CODEC.fieldOf("Palette").forGetter(Schematic::getBlockPalette),
			Codec.BYTE_BUFFER.fieldOf("BlockData").forGetter(Schematic::getBlockData),
			Codec.list(NbtCompound.CODEC).optionalFieldOf("BlockEntities", ImmutableList.of()).forGetter(Schematic::getBlockEntities),
			Codec.list(NbtCompound.CODEC).optionalFieldOf("Entities", ImmutableList.of()).forGetter(Schematic::getEntities),
			Codec.unboundedMap(BuiltinRegistries.BIOME.getCodec(), Codec.INT).optionalFieldOf("BiomePalette", Collections.emptyMap()).forGetter(Schematic::getBiomePalette),
			Codec.BYTE_BUFFER.optionalFieldOf("BiomeData", ByteBuffer.wrap(new byte[0])).forGetter(Schematic::getBlockData)
	).apply(instance, Schematic::new));

	private final int version;
	private final int dataVersion;
	private final SchematicMetadata metadata;
	private final short width;
	private final short height;
	private final short length;
	private final Vec3i offset;
	private final int paletteMax;
	private final BiMap<BlockState, Integer> blockPalette;
	private final ByteBuffer blockData;
	private List<NbtCompound> blockEntities;
	private List<NbtCompound> entities;
	private final BiMap<Biome, Integer> biomePalette;
	private final ByteBuffer biomeData;
	private RelativeBlockSample cachedBlockSample = null;

	public Schematic(int version, int dataVersion, SchematicMetadata metadata, short width, short height, short length, Vec3i offset, int paletteMax, Map<BlockState, Integer> blockPalette, ByteBuffer blockData, List<NbtCompound> blockEntities, List<NbtCompound> entities, Map<Biome, Integer> biomePalette, ByteBuffer biomeData) {
		this.version = version;
		this.dataVersion = dataVersion;
		this.metadata = metadata;
		this.width = width;
		this.height = height;
		this.length = length;
		this.offset = offset;
		this.paletteMax = paletteMax;
		this.blockPalette = HashBiMap.create(blockPalette);
		this.blockData = blockData;
		this.blockEntities = blockEntities;
		this.entities = entities;
		this.biomePalette = HashBiMap.create(biomePalette);
		this.biomeData = biomeData;
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

	public int getPaletteMax() {
		return this.paletteMax;
	}

	public Map<BlockState, Integer> getBlockPalette() {
		return this.blockPalette;
	}

	public ByteBuffer getBlockData() {
		return this.blockData;
	}

	public List<NbtCompound> getBlockEntities() {
		return this.blockEntities;
	}

	public BiMap<Biome, Integer> getBiomePalette() {
		return this.biomePalette;
	}

	public ByteBuffer getBiomeData() {
		return this.biomeData;
	}

	public void setBlockEntities(List<NbtCompound> blockEntities) {
		this.blockEntities = blockEntities.stream().map(SchematicPlacer::fixEntityId).collect(Collectors.toList());
	}

	public void setEntities(Collection<? extends Entity> entities) {
		this.setEntities(entities.stream().map((e) -> {
			NbtCompound nbt = new NbtCompound();
			e.saveSelfNbt(nbt);
			return nbt;
		}).collect(Collectors.toList()));
	}

	public List<NbtCompound> getEntities() {
		return this.entities;
	}

	public void setEntities(List<NbtCompound> entities) {
		this.entities = entities;
	}

	public static RelativeBlockSample getBlockSample(Schematic schem) {
		if (schem.cachedBlockSample == null) {
			return (schem.cachedBlockSample = new RelativeBlockSample(schem));
		}
		return schem.cachedBlockSample;
	}

	public static Schematic fromNbt(NbtCompound nbt) {
		return CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow(false, PRINT_TO_STDERR).getFirst();
	}

	public static NbtCompound toNbt(Schematic schem) {
		return (NbtCompound) CODEC.encodeStart(NbtOps.INSTANCE, schem).getOrThrow(false, PRINT_TO_STDERR);
	}

	public static Schematic fromJson(JsonObject json) {
		return CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, PRINT_TO_STDERR).getFirst();
	}

	public static JsonObject toJson(Schematic schem) {
		return (JsonObject) CODEC.encodeStart(JsonOps.INSTANCE, schem).getOrThrow(false, PRINT_TO_STDERR);
	}

	public static <T> Schematic fromDynamic(Dynamic<T> dynamic) {
		return CODEC.parse(dynamic).getOrThrow(false, PRINT_TO_STDERR);
	}

	public static <T> Dynamic<T> toDynamic(Schematic schem, DynamicOps<T> ops) {
		return new Dynamic<>(ops,CODEC.encodeStart(ops, schem).getOrThrow(false, PRINT_TO_STDERR));
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("version", this.version)
				.add("dataVersion", this.dataVersion)
				.add("metadata", this.metadata)
				.add("width", this.width)
				.add("height", this.height)
				.add("length", this.length)
				.add("offset", this.offset)
				.add("paletteMax", this.paletteMax)
				.add("blockPalette", this.blockPalette)
				.add("blockData", this.blockData)
				.add("blockEntities", this.blockEntities)
				.add("entities", this.entities)
				.add("biomePalette", this.biomePalette)
				.add("biomeData", this.biomeData)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Schematic schematic = (Schematic) o;
		return this.version == schematic.version &&
				this.dataVersion == schematic.dataVersion &&
				this.width == schematic.width &&
				this.height == schematic.height &&
				this.length == schematic.length &&
				this.paletteMax == schematic.paletteMax &&
				Objects.equals(this.metadata, schematic.metadata)
				&& Objects.equals(this.offset, schematic.offset)
				&& Objects.equals(this.blockPalette, schematic.blockPalette)
				&& Objects.equals(this.blockData, schematic.blockData)
				&& Objects.equals(this.blockEntities, schematic.blockEntities)
				&& Objects.equals(this.entities, schematic.entities)
				&& Objects.equals(this.biomePalette, schematic.biomePalette)
				&& Objects.equals(this.biomeData, schematic.biomeData);
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				this.version,
				this.dataVersion,
				this.metadata,
				this.width,
				this.height,
				this.length,
				this.offset,
				this.paletteMax,
				this.blockPalette,
				this.blockData,
				this.blockEntities,
				this.entities,
				this.biomePalette,
				this.biomeData
		);
	}
}

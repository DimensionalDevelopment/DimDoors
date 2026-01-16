package org.dimdev.dimdoors.util.schematic;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.BlockPlacementType;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.dimdev.dimdoors.util.CodecUtils.createTagMapCodec;

public class Schematic {
    private static final Consumer<String> PRINT_TO_STDERR = System.err::println;
    public static final Codec<Schematic> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.fieldOf("Version").forGetter(Schematic::getVersion),
            Codec.INT.optionalFieldOf("Data Version", SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA)).forGetter(Schematic::getDataVersion),
            SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::getMetadata),
            Codec.SHORT.fieldOf("Width").forGetter(Schematic::getWidth),
            Codec.SHORT.fieldOf("Height").forGetter(Schematic::getHeight),
            Codec.SHORT.fieldOf("Length").forGetter(Schematic::getLength),
            Vec3i.CODEC.fieldOf("Offset").forGetter(Schematic::getOffset),
            Codec.INT.fieldOf("PaletteMax").forGetter(Schematic::getPaletteMax),
            SchematicBlockPalette.CODEC.fieldOf("Palette").forGetter(Schematic::getBlockPalette),
            Codec.BYTE_BUFFER.fieldOf("BlockData").forGetter(Schematic::getBlockData),
            createTagMapCodec(BlockPos.CODEC).optionalFieldOf("BlockEntities", ImmutableMap.of()).forGetter(Schematic::getBlockEntities),
            createTagMapCodec(Vec3.CODEC).optionalFieldOf("Entities", ImmutableMap.of()).forGetter(Schematic::getEntities)/*,*/
//			Codec.unboundedMap(BuiltinRegistries.BIOME.getCodec(), Codec.INT).optionalFieldOf("BiomePalette", Collections.emptyMap()).forGetter(Schematic::getBiomePalette),
//			Codec.BYTE_BUFFER.optionalFieldOf("BiomeData", ByteBuffer.wrap(new byte[0])).forGetter(Schematic::getBlockData)
    ).apply(instance, Schematic::new));

    private final int version;
    private final int dataVersion;
    private final SchematicMetadata metadata;
    private final short width;
    private final short height;
    private final short length;
    private final Vec3i offset;
    private final BlockState[] blockPalette;
    private final ByteBuffer blockData;
    private final Map<BlockPos, CompoundTag> blockEntities;
    private final Map<Vec3, CompoundTag> entities;
//	private final BiMap<Biome, Integer> biomePalette;
//	private final ByteBuffer biomeData;
//	private RelativeBlockSample cachedBlockSample = null;

    public Schematic(int version, int dataVersion, SchematicMetadata metadata, short width, short height, short length, Vec3i offset, int paletteMax, Map<BlockState, Integer> blockPalette, ByteBuffer blockData, Map<BlockPos, CompoundTag> blockEntities, Map<Vec3, CompoundTag> entities /*, Map<Biome, Integer> biomePalette, ByteBuffer biomeData*/) {
        this.version = version;
        this.dataVersion = dataVersion;
        this.metadata = metadata;
        this.width = width;
        this.height = height;
        this.length = length;
        this.offset = offset;
        this.blockPalette = new BlockState[paletteMax];
        blockPalette.forEach((state, id) -> this.blockPalette[id] = state);

        this.blockData = blockData;
        this.blockEntities = blockEntities;
        this.entities = entities;
//		this.biomePalette = HashBiMap.create(biomePalette);
//		this.biomeData = biomeData;
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
        throw new RuntimeException("Schematic Deserialization not supported");
    }

    public Map<BlockState, Integer> getBlockPalette() {
        return IntStream.range(0, blockPalette.length).mapToObj(a -> {
            var state = blockPalette[a];

            if(state == null) return null;
            else return Map.entry(state, a);
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public ByteBuffer getBlockData() {
        return this.blockData;
    }

    public Map<BlockPos, CompoundTag> getBlockEntities() {
        return this.blockEntities;
    }

//	public BiMap<Biome, Integer> getBiomePalette() {
//		return this.biomePalette;
//	}

//	public ByteBuffer getBiomeData() {
//		return this.biomeData;
//	}

//	public void setBlockEntities(List<CompoundTag> blockEntities) {
//		this.blockEntities = blockEntities.stream().map(SchematicPlacer::fixEntityId).collect(Collectors.toList());
//	}
//
//	public void setEntities(Collection<? extends Entity> entities) {
//		this.setEntities(entities.stream().map((e) -> {
//			CompoundTag nbt = new CompoundTag();
//			e.saveAsPassenger(nbt);
//			return nbt;
//		}).collect(Collectors.toList()));
//	}

    public Map<Vec3, CompoundTag> getEntities() {
        return this.entities;
    }

//	public void setEntities(Map<Vec3, CompoundTag> entities) {
//		this.entities = entities;
//	}

//	public static RelativeBlockSample getBlockSample(Schematic schem) {
//		if (schem.cachedBlockSample == null) {
//			return (schem.cachedBlockSample = new RelativeBlockSample(schem));
//		}
//		return schem.cachedBlockSample;
//	}

    public static Schematic fromNbt(CompoundTag nbt) {
        return CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
    }

    public static CompoundTag toNbt(Schematic schem) {
        return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, schem).getOrThrow();
    }

    public static Schematic fromJson(JsonObject json) {
        return CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
    }

    public static JsonObject toJson(Schematic schem) {
        return (JsonObject) CODEC.encodeStart(JsonOps.INSTANCE, schem).getOrThrow();
    }

    public static <T> Schematic fromDynamic(Dynamic<T> dynamic) {
        return CODEC.parse(dynamic).getOrThrow();
    }

    public static <T> Dynamic<T> toDynamic(Schematic schem, DynamicOps<T> ops) {
        return new Dynamic<>(ops,CODEC.encodeStart(ops, schem).getOrThrow());
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
                .add("paletteMax", this.blockPalette.length)
                .add("blockPalette", this.blockPalette)
                .add("blockData", this.blockData)
                .add("blockEntities", this.blockEntities)
                .add("entities", this.entities)
//				.add("biomePalette", this.biomePalette)
//				.add("biomeData", this.biomeData)
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
                Objects.equals(this.metadata, schematic.metadata)
                && Objects.equals(this.offset, schematic.offset)
                && Arrays.equals(this.blockPalette, schematic.blockPalette)
                && Objects.equals(this.blockData, schematic.blockData)
                && Objects.equals(this.blockEntities, schematic.blockEntities)
                && Objects.equals(this.entities, schematic.entities)
//				&& Objects.equals(this.biomePalette, schematic.biomePalette)
//				&& Objects.equals(this.biomeData, schematic.biomeData);
                ;
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
                Arrays.hashCode(this.blockPalette),
                this.blockData,
                this.blockEntities,
                this.entities/*,*/
//				this.biomePalette,
//				this.biomeData
        );
    }

    public List<BlockEntity> place(BlockPos origin, ServerLevelAccessor world, BlockPlacementType placementType) {
        var pair = ChunkWorker.getWorkers(world, origin, width, height, length, blockData, blockPalette);

        if (pair.isEmpty()) {
            return List.of();
        }

        pair.forEach(ChunkWorker::execute);

        placeEntities(world, origin);
        return placeBlockEntities(world, origin);
    }

    private List<BlockEntity> placeBlockEntities(ServerLevelAccessor world, BlockPos origin) {
        var provider = world.registryAccess();
        var serverLevel = world.getLevel();

        List<BlockEntity> placed = new ArrayList<>();

        for (Map.Entry<BlockPos, CompoundTag> entry : blockEntities.entrySet()) {
            BlockPos pos = entry.getKey();
            CompoundTag tag = entry.getValue();

            var type = by(tag);

            if(type.isEmpty()) continue;

            var blockPos = new BlockPos(
                    pos.getX() + origin.getX(),
                    pos.getY() + origin.getY(),
                    pos.getZ() + origin.getZ()
            );

            var blockState = world.getBlockState(blockPos);
//            if(!type.get().isValid(blockState)) continue;

            var entity = type.get().create(blockPos, blockState);

            if(entity == null) continue;

            entity.loadWithComponents(tag, provider);
            entity.setLevel(serverLevel);
            serverLevel.setBlockEntity(entity);
            placed.add(entity);
        }

        return placed;
    }

    public static Optional<BlockEntityType<?>> by(CompoundTag compoundTag) {
        return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(ResourceLocation.parse(compoundTag.getString("id")));
    }

    private void placeEntities(ServerLevelAccessor world, BlockPos origin) {
        for (Map.Entry<Vec3, CompoundTag> entry : entities.entrySet()) {
            Vec3 pos = entry.getKey();
            CompoundTag tag = entry.getValue();

            var type = EntityType.by(tag);

            if(type.isEmpty()) continue;

            var entity = type.get().create(world.getLevel());

            if(entity == null) continue;

            entity.load(tag);

            entity.setPos(
                    pos.x + origin.getX(),
                    pos.y + origin.getY(),
                    pos.z + origin.getZ()
            );

            world.addFreshEntityWithPassengers(entity);
        }
    }

    public static CompoundTag fixEntityId(CompoundTag nbt) {
        return nbt;
    }

    private record ChunkWorker(ChunkAccess chunk, List<SectionWorker> workers) {

        public void execute() {
            workers.forEach(SectionWorker::execute);
        }

        private record SectionWorker(LevelChunkSection section, int indexX,
                                     int indexY, int indexZ,
                                     int width, int height, int length, int schematicWidth, int schematicHeight,
                                     int schematicLength,
                                     ByteBuffer blockData, BlockState[] pallette) {

            public void execute() {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < length; z++) {
                        for (int x = 0; x < width; x++) {
                            int idx = (x + indexX) + (z * indexZ) * schematicWidth + (y + indexY) * schematicWidth * schematicLength;
                            BlockState blockstate = pallette[blockData.get(idx)];
                            if (blockstate == null) continue;

                            section.setBlockState(x, y, z, blockstate);
                        }
                    }
                }

                section.recalcBlockCounts();
            }
        }

        private static List<ChunkWorker> getWorkers(
                LevelAccessor world,
                BlockPos origin,
                int width,
                int height,
                int length,
                ByteBuffer blockData,
                BlockState[] palette) {

            List<ChunkWorker> workers = new ArrayList<>();

            var numXSections = width / 16;
            var indexOffsetX = width % 16;
            if(indexOffsetX != 0) numXSections += 1;

            var numYSections = height / 16;
            var indexOffsetY = height % 16;
            if(indexOffsetY != 0) numYSections += 1;

            var numZSections = length / 16;
            var indexOffsetZ = length % 16;
            if(indexOffsetZ != 0) numZSections += 1;

            int minChunPosY = origin.getY() / 16;
            int minChunPosX = origin.getX() / 16;
            int minChunPosZ = origin.getZ() / 16;

            for (int locallX = 0; locallX < numXSections; locallX++) {
                var chunkPosX = minChunPosX + locallX;
                var jobWidth = locallX == numXSections - 1 ? indexOffsetX : 16;


                for (int localZ = 0; localZ < numZSections; localZ++) {
                    var chunkPosZ = minChunPosZ + localZ;
                    var jobLength = localZ == numZSections - 1 ? indexOffsetZ : 16;

                    ChunkAccess chunk = world.getChunk(chunkPosX, chunkPosZ);

                    var worker = new ChunkWorker(chunk, new ArrayList<>());
                    workers.add(worker);

                    LevelChunkSection[] sections = chunk.getSections();

                    for (int localY = 0; localY < numYSections; localY++) {
                        var chunkPosY = minChunPosY + localY;
                        var jobHeight = localY == numYSections - 1 ? indexOffsetY : 16;

                        var sectionIndex = world.getSectionIndex(chunkPosY);

                        LevelChunkSection section = sections[sectionIndex];
                        if (section == null) {
                            section = new LevelChunkSection(world.registryAccess().registryOrThrow(Registries.BIOME));
                            sections[sectionIndex] = section;
                        }

                        worker.workers().add(new SectionWorker(
                                section,
                                locallX,
                                localY,
                                localZ,
                                jobWidth,
                                jobHeight,
                                jobLength,
                                width,
                                height,
                                length,
                                blockData,
                                palette
                        ));

                    }
                }
            }

            return workers;
        }
    }
}

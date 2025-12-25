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

        if (pair.getFirst().isEmpty()) {
            return List.of();
        }

        pair.getFirst().forEach(ChunkWorker::execute);
        pair.getSecond().forEach(chunk -> chunk.setUnsaved(true));

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
                    pos.getX() + origin.getX() - offset.getX(),
                    pos.getY() + origin.getY() - offset.getY(),
                    pos.getZ() + origin.getZ() - offset.getZ()
            );

            var blockState = world.getBlockState(blockPos);
            if(!type.get().isValid(blockState)) continue;

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
                    pos.x + origin.getX() - offset.getX(),
                    pos.y + origin.getY() - offset.getY(),
                    pos.z + origin.getZ() - offset.getZ()
            );

            world.addFreshEntityWithPassengers(entity);
        }
    }

    public static CompoundTag fixEntityId(CompoundTag nbt) {





        return nbt;
    }

    private record ChunkWorker(LevelChunkSection section, int offsetX, int offsetY, int offsetZ, int indexX, int indexY, int indexZ,
                       int width, int height, int length, ByteBuffer blockData, BlockState[] pallette) {
        private static Pair<List<ChunkWorker>, List<ChunkAccess>> getWorkers(
                LevelAccessor world,
                BlockPos origin,
                int width,
                int height,
                int length,
                ByteBuffer blockData,
                BlockState[] palette) {

            List<ChunkWorker> workers = new ArrayList<>();
            List<ChunkAccess> chunks = new ArrayList<>();

            int minSectionY = origin.getY() >> 4;
            int maxSectionY = (origin.getY() + height - 1) >> 4;
            int minSectionX = (origin.getX()) >> 4;
            int maxSectionX = (origin.getX() + width - 1) >> 4;
            int minSectionZ = (origin.getZ()) >> 4;
            int maxSectionZ = (origin.getZ() + length - 1) >> 4;


            for (int sectionX = minSectionX; sectionX <= maxSectionX; sectionX++) {
                for (int sectionZ = minSectionZ; sectionZ <= maxSectionZ; sectionZ++) {
                    ChunkAccess chunk = world.getChunk(sectionX, sectionZ);
                    chunks.add(chunk);
                    LevelChunkSection[] sections = chunk.getSections();

                    for (int sectionY = minSectionY; sectionY <= maxSectionY; sectionY++) {
                        LevelChunkSection section = sections[world.getSectionIndex(sectionY)];
                        if (section == null) {
                            section = new LevelChunkSection(world.registryAccess().registryOrThrow(Registries.BIOME));
                            sections[chunk.getSectionIndex(sectionY << 4)] = section;
                        }

                        int localX = ((sectionX << 4) - origin.getX());
                        int localY = ((sectionY << 4) - origin.getY());
                        int localZ = ((sectionZ << 4) - origin.getZ());

                        int localStartX = Math.max(0, localX);
                        int localStartY = Math.max(0, localY);
                        int localStartZ = Math.max(0, localZ);

                        int localEndX = Math.min(width, localX + 16);
                        int localEndY = Math.min(height, localY + 16);
                        int localEndZ = Math.min(length, localZ + 16);

                        int jobWidth = localEndX - localStartX;
                        int jobHeight = localEndY - localStartY;
                        int jobLength = localEndZ - localStartZ;

                        int secOffsetX = (localStartX + origin.getX()) & 15;
                        int secOffsetY = (localStartY + origin.getY()) & 15;
                        int secOffsetZ = (localStartZ + origin.getZ()) & 15;

                        workers.add(new ChunkWorker(
                                section,
                                secOffsetX,
                                secOffsetY,
                                secOffsetZ,
                                localStartX,
                                localStartY,
                                localStartZ,
                                jobWidth,
                                jobHeight,
                                jobLength,
                                blockData,
                                palette
                        ));
                    }
                }
            }

            return new Pair<>(workers, chunks);
        }

        public void execute() {
            int idx = (indexY * length + indexZ) * width + indexX;

            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    for (int x = 0; x < width; x++, idx++) {
                        BlockState blockstate = pallette[blockData.get(idx)];
                        if (blockstate == null) continue;

                        section.setBlockState(offsetX + x, offsetY + y, offsetZ + z, blockstate);
                    }
                }
            }

            section.recalcBlockCounts();
        }
    }
}

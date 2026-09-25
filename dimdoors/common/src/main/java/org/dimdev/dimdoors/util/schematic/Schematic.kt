package org.dimdev.dimdoors.util.schematic

import com.google.common.base.MoreObjects
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.gson.JsonObject
import com.mojang.serialization.Codec
import com.mojang.serialization.Dynamic
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.block.state.BlockState
import java.nio.ByteBuffer
import java.util.Objects
import java.util.function.Consumer

class Schematic(
    val version: Int,
    val dataVersion: Int,
    val metadata: SchematicMetadata,
    val width: Short,
    val height: Short,
    val length: Short,
    val offset: Vec3i,
    blockPalette: Map<BlockState, Int>,
    val blockData: ByteBuffer,
    blockEntities: List<CompoundTag>,
    var entities: List<CompoundTag> /*, Map<Biome, Integer> biomePalette, ByteBuffer biomeData*/
) {
    val blockPalette: BiMap<BlockState, Int> = HashBiMap.create(blockPalette)
    var blockEntities: List<CompoundTag> = blockEntities
        set(value) {
            field = value.map(SchematicPlacer::fixEntityId)
        }
    //    private final BiMap<Biome, Integer> biomePalette;
//    private final ByteBuffer biomeData;
    private var cachedBlockSample: RelativeBlockSample? = null

//    public BiMap<Biome, Integer> getBiomePalette() {
//        return this.biomePalette;
//    }

//    public ByteBuffer getBiomeData() {
//        return this.biomeData;
//    }

    fun setEntities(entities: Collection<Entity>) {
        this.entities = entities.map { e ->
            val nbt = CompoundTag()
            e.saveAsPassenger(nbt)
            nbt
        }
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(this)
            .add("version", this.version)
            .add("dataVersion", this.dataVersion)
            .add("metadata", this.metadata)
            .add("width", this.width)
            .add("height", this.height)
            .add("length", this.length)
            .add("offset", this.offset)
            .add("blockPalette", this.blockPalette)
            .add("blockData", this.blockData)
            .add("blockEntities", this.blockEntities)
            .add("entities", this.entities)
//                .add("biomePalette", this.biomePalette)
//                .add("biomeData", this.biomeData)
            .toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        val schematic = other as Schematic
        return this.version == schematic.version &&
                this.dataVersion == schematic.dataVersion &&
                this.width == schematic.width &&
                this.height == schematic.height &&
                this.length == schematic.length &&
                this.metadata == schematic.metadata
                && this.offset == schematic.offset
                && this.blockPalette == schematic.blockPalette
                && this.blockData == schematic.blockData
                && this.blockEntities == schematic.blockEntities
                && this.entities == schematic.entities
//                && Objects.equals(this.biomePalette, schematic.biomePalette)
//                && Objects.equals(this.biomeData, schematic.biomeData);
    }

    override fun hashCode(): Int {
        return Objects.hash(
            this.version,
            this.dataVersion,
            this.metadata,
            this.width,
            this.height,
            this.length,
            this.offset,
            this.blockPalette,
            this.blockData,
            this.blockEntities,
            this.entities/*,*/
//                this.biomePalette,
//                this.biomeData
        )
    }

    private data class Blocks(val palette: Map<BlockState, Int>, val data: ByteBuffer, val blockEntities: List<CompoundTag>) {
        companion object {
            val CODEC: Codec<Blocks> = RecordCodecBuilder.create { instance ->
                instance.group(
                    SchematicBlockPalette.CODEC.fieldOf("Palette").forGetter(Blocks::palette),
                    Codec.BYTE_BUFFER.fieldOf("Data").forGetter(Blocks::data),
                    CompoundTag.CODEC.listOf().optionalFieldOf("BlockEntities", listOf()).forGetter(Blocks::blockEntities)
                ).apply(instance, ::Blocks)
            }
        }
    }

    companion object {
        private val PRINT_TO_STDERR = Consumer<String> { System.err.println(it) }

        @JvmField
        val CODEC: Codec<Schematic> = RecordCodecBuilder.create<Schematic> { instance ->
            instance.group(
                Codec.INT.fieldOf("Version").forGetter(Schematic::version),
                Codec.INT.fieldOf("DataVersion").forGetter(Schematic::dataVersion),
                SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::metadata),
                Codec.SHORT.fieldOf("Width").forGetter(Schematic::width),
                Codec.SHORT.fieldOf("Height").forGetter(Schematic::height),
                Codec.SHORT.fieldOf("Length").forGetter(Schematic::length),
                Vec3i.CODEC.optionalFieldOf("Offset", Vec3i.ZERO).forGetter(Schematic::offset),
                Blocks.CODEC.fieldOf("Blocks").forGetter { Blocks(it.blockPalette, it.blockData, it.blockEntities) },
                CompoundTag.CODEC.listOf().optionalFieldOf("Entities", listOf()).forGetter(Schematic::entities)
//            Codec.unboundedMap(BuiltinRegistries.BIOME.getCodec(), Codec.INT).optionalFieldOf("BiomePalette", Collections.emptyMap()).forGetter(Schematic::getBiomePalette),
//            Codec.BYTE_BUFFER.optionalFieldOf("BiomeData", ByteBuffer.wrap(new byte[0])).forGetter(Schematic::getBlockData)
            ).apply(instance) { version, dataVersion, metadata, width, height, length, offset, blocks, entities ->
                Schematic(version, dataVersion, metadata, width, height, length, offset, blocks.palette, blocks.data, blocks.blockEntities, entities)
            }
        }.fieldOf("Schematic").codec()

        @JvmStatic
        fun getBlockSample(schem: Schematic): RelativeBlockSample {
            return schem.cachedBlockSample ?: RelativeBlockSample(schem).also { schem.cachedBlockSample = it }
        }

        @JvmStatic
        fun fromNbt(nbt: CompoundTag): Schematic {
            return CODEC.decode(NbtOps.INSTANCE, SchematicUpgrader.update(nbt)).getOrThrow().first
        }

        @JvmStatic
        fun toNbt(schem: Schematic): CompoundTag {
            return CODEC.encodeStart(NbtOps.INSTANCE, schem).getOrThrow() as CompoundTag
        }

        @JvmStatic
        fun fromJson(json: JsonObject): Schematic {
            return CODEC.decode(JsonOps.INSTANCE, json).getOrThrow().first
        }

        @JvmStatic
        fun toJson(schem: Schematic): JsonObject {
            return CODEC.encodeStart(JsonOps.INSTANCE, schem).getOrThrow() as JsonObject
        }

        @JvmStatic
        fun <T> fromDynamic(dynamic: Dynamic<T>): Schematic {
            return CODEC.parse(dynamic).getOrThrow()
        }

        @JvmStatic
        fun <T> toDynamic(schem: Schematic, ops: DynamicOps<T>): Dynamic<T> {
            return Dynamic(ops, CODEC.encodeStart(ops, schem).getOrThrow())
        }
    }
}

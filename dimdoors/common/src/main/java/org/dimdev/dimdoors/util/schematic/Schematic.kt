package org.dimdev.dimdoors.util.schematic

import com.google.common.base.MoreObjects
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableBiMap
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import org.dimdev.dimcore.api.ext.minusAssign
import org.dimdev.dimcore.api.ext.set
import org.dimdev.dimdoors.util.CodecUtils
import java.util.*

class Schematic(
    val version: Int,
    val dataVersion: Int,
    val metadata: SchematicMetadata,
    val width: Short,
    val height: Short,
    val length: Short,
    val offset: Vec3i,
    val blocks: Blocks,
    val biomes: Biomes,
    var entities: List<CompoundTag>,
) {
    private var cachedBlockSample: RelativeBlockSample? = null

    fun setEntities(entities: Collection<Entity>, origin: BlockPos) {
        this.entities = entities.mapNotNull { e ->
            val nbt = CompoundTag()
            if (!e.saveAsPassenger(nbt)) return@mapNotNull null
            val pos = e.position().subtract(Vec3.atLowerCornerOf(origin))
            CompoundTag().apply {
                put("Pos", ListTag().apply {
                    add(DoubleTag.valueOf(pos.x))
                    add(DoubleTag.valueOf(pos.y))
                    add(DoubleTag.valueOf(pos.z))
                })
                this["Id"] = nbt.getString("id")
                nbt -= "id"
                nbt.remove("Pos")
                put("Data", nbt)
            }
        }
    }

    abstract class DataBlock<T> {
        abstract val palette: BiMap<T, Int>
        abstract val data: IntArray
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
            .add("blocks", this.blocks)
            .add("biomes", this.biomes)
            .add("entities", this.entities)
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
                && this.blocks == schematic.blocks
                && this.biomes == schematic.biomes
                && this.entities == schematic.entities

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
            this.blocks,
            this.biomes,
            this.entities
        )
    }

    data class Biomes(override val palette: BiMap<ResourceKey<Biome>, Int>, override val data: IntArray): DataBlock<ResourceKey<Biome>>() {

        companion object {
            val EMPTY = Biomes(ImmutableBiMap.of(), IntArray(0))
            val CODEC: Codec<Biomes> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(ResourceKey.codec(Registries.BIOME), Codec.INT).biMap().fieldOf("Palette").forGetter(Biomes::palette),
                    CodecUtils.INT_ARRAY_CODEC.fieldOf("Data").forGetter(Biomes::data),
                ).apply(instance, ::Biomes)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Biomes

            if (palette != other.palette) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = palette.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }

    data class Blocks(override val palette: BiMap<BlockState, Int>, override val data: IntArray, private val blockEntitiesBacking: List<CompoundTag>) : DataBlock<BlockState>() {
        var blockEntities: List<CompoundTag> = blockEntitiesBacking
            set(value) {
                field = value.map(SchematicPlacer::fixEntityId)
            }

        companion object {
            val CODEC: Codec<Blocks> = RecordCodecBuilder.create { instance ->
                instance.group(
                    SchematicBlockPalette.CODEC.biMap().fieldOf("Palette").forGetter(Blocks::palette),
                    CodecUtils.INT_ARRAY_CODEC.fieldOf("Data").forGetter(Blocks::data),
                    CompoundTag.CODEC.listOf().optionalFieldOf("BlockEntities", listOf()).forGetter(Blocks::blockEntities)
                ).apply(instance, ::Blocks)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Blocks

            if (palette != other.palette) return false
            if (!data.contentEquals(other.data)) return false
            if (blockEntitiesBacking != other.blockEntitiesBacking) return false
            if (blockEntities != other.blockEntities) return false

            return true
        }

        override fun hashCode(): Int {
            var result = palette.hashCode()
            result = 31 * result + data.contentHashCode()
            result = 31 * result + blockEntitiesBacking.hashCode()
            result = 31 * result + blockEntities.hashCode()
            return result
        }
    }

    companion object {
        @JvmField
        val CODEC: Codec<Schematic> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("Version").forGetter(Schematic::version),
                Codec.INT.fieldOf("DataVersion").forGetter(Schematic::dataVersion),
                SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::metadata),
                Codec.SHORT.fieldOf("Width").forGetter(Schematic::width),
                Codec.SHORT.fieldOf("Height").forGetter(Schematic::height),
                Codec.SHORT.fieldOf("Length").forGetter(Schematic::length),
                Vec3i.CODEC.optionalFieldOf("Offset", Vec3i.ZERO).forGetter(Schematic::offset),
                Blocks.CODEC.fieldOf("Blocks").forGetter { it.blocks },
                CompoundTag.CODEC.listOf().optionalFieldOf("Entities", listOf()).forGetter(Schematic::entities),
                Biomes.CODEC.optionalFieldOf("Biomes", Biomes.EMPTY).forGetter { it.biomes },
            ).apply(instance) { version, dataVersion, metadata, width, height, length, offset, blocks, entities, biomes ->
                Schematic(version, dataVersion, metadata, width, height, length, offset, blocks, biomes, entities)
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
        fun toNbt(schem: Schematic, registries: HolderLookup.Provider): CompoundTag {
            return CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), schem).getOrThrow() as CompoundTag
        }
    }
}

fun <K, V> UnboundedMapCodec<K, V>.biMap(): Codec<BiMap<K, V>> = xmap< BiMap<K, V>>({ HashBiMap.create(it) }, {it })

package org.dimdev.dimdoors.util.schematic

import com.google.common.collect.HashBiMap
import net.minecraft.SharedConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.*
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.WorldGenLevel
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.DimCore
import java.util.*
import java.util.stream.Stream

private fun <T: Any> T.nameSpace(registry: Registry<T>): String =
    this.let(registry::wrapAsHolder).unwrapKey().orElseThrow().location().namespace

object SchematicPlacer {
    @JvmField
    val LOGGER: Logger = LogManager.getLogger()

    @JvmStatic
    fun place(schematic: Schematic, world: ServerLevel, origin: BlockPos) {
        LOGGER.debug("Placing schematic: {}", schematic.metadata.name)
        for (id in schematic.metadata.requiredMods) {
            if (!DimCore.platform.isModLoaded(id)) {
                LOGGER.warn("Schematic \"${schematic.metadata.name}\" depends on mod \"$id\", which is missing!")
            }
        }
        val blockSample = Schematic.getBlockSample(schematic)
        blockSample.place(origin, world, false, world.registryAccess())
    }

    @JvmStatic
    fun copy(world: ServerLevel, box: BoundingBox, metadata: SchematicMetadata): Schematic {
        val origin = BlockPos(box.minX(), box.minY(), box.minZ())
        val blockPalette = HashBiMap.create<BlockState, Int>()

        val size = box.xSpan * box.ySpan * box.zSpan

        val blockData = IntArray(size)
        val biomePalette = HashBiMap.create<ResourceKey<Biome>, Int>()
        val biomeData = IntArray(size)
        val blockEntities = mutableListOf<CompoundTag>()
        val pos = BlockPos.MutableBlockPos()

        val requiredMods = mutableSetOf<String>()

        for (y in 0 until box.ySpan) {
            for (z in 0 until box.zSpan) {
                for (x in 0 until box.xSpan) {
                    pos.setWithOffset(origin, x, y, z)
                    val state = world.getBlockState(pos).also { requiredMods.add(it.blockHolder.unwrapKey().get().location().namespace) }
                    val biome = world.getBiome(pos).also { requiredMods.add(it.unwrapKey().get().location().namespace) }
                    val index = x + z * box.xSpan + y * box.xSpan * box.zSpan
                    blockData[index] = blockPalette.computeIfAbsent(state) { blockPalette.size }
                    biomeData[index] = biomePalette.computeIfAbsent(biome.unwrapKey().get()) { biomePalette.size }

                    val blockEntity = world.getBlockEntity(pos) ?: continue
                    requiredMods.add(blockEntity.type.nameSpace(BuiltInRegistries.BLOCK_ENTITY_TYPE))

                    blockEntities.add(CompoundTag().apply {
                        putIntArray("Pos", intArrayOf(x, y, z))
                        putString("Id", BlockEntityType.getKey(blockEntity.type).toString())
                        put("Data", blockEntity.saveWithoutMetadata(world.registryAccess()))
                    })
                }
            }
        }

        val entities = world.getEntities(null as Entity?, AABB.of(box)) { it !is Player }
        entities.map { it.type }.forEach { requiredMods += it.nameSpace(BuiltInRegistries.ENTITY_TYPE) }

        val schematic = Schematic(3, SharedConstants.getCurrentVersion().dataVersion.version, metadata.copy(requiredMods = requiredMods.toList()), box.xSpan.toShort(), box.ySpan.toShort(), box.zSpan.toShort(), Vec3i.ZERO, Schematic.Blocks(blockPalette, blockData, blockEntities), Schematic.Biomes(biomePalette, biomeData), emptyList())
        schematic.setEntities(entities, origin)

        return schematic
    }

    private fun placeEntities(origin: BlockPos, schematic: Schematic, world: WorldGenLevel) {
        val entityNbts = schematic.entities
        for (nbt in entityNbts) {
            val nbtList = Objects.requireNonNull(nbt.getList("Pos", 6), "Entity in schematic  \"" + schematic.metadata.name + "\" did not have a Pos nbt list!")
            processPos(nbtList, origin, schematic.offset, nbt)

            val entityType = EntityType.by(fixEntityId(nbt)).orElseThrow { AssertionError() }
            val e = entityType.create(world.level) ?: continue

            e.load(nbt)

            world.addFreshEntityWithPassengers(e)
        }
    }

    @JvmStatic
    fun fixEntityId(nbt: CompoundTag): CompoundTag {
        if (!nbt.contains("Id") && nbt.contains("id")) {
            nbt.putString("Id", nbt.getString("id"))
        } else if (nbt.contains("Id") && !nbt.contains("id")) {
            nbt.putString("id", nbt.getString("Id"))
        }
        if (!nbt.contains("Id") || !nbt.contains("id")) {
            System.err.println("An unexpected error occurred parsing this entity")
            System.err.println(nbt.toString())
            throw IllegalStateException("Entity did not have an 'Id' nbt string, nor an 'id' nbt string!")
        }
        return nbt
    }

    private fun processPos(nbtList: ListTag, origin: BlockPos, offset: Vec3i, nbt: CompoundTag) {
        val x = nbtList.getDouble(0)
        val y = nbtList.getDouble(1)
        val z = nbtList.getDouble(2)
        nbt.remove("Pos")
        nbt.put("Pos", NbtOps.INSTANCE.createList(Stream.of<Tag>(
            DoubleTag.valueOf(x + origin.x - offset.x),
            DoubleTag.valueOf(y + origin.y - offset.y),
            DoubleTag.valueOf(z + origin.z - offset.z)
        )))
    }
}

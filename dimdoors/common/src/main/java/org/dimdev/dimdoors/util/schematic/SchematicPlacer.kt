package org.dimdev.dimdoors.util.schematic

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.nbt.*
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.WorldGenLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.dimdev.dimcore.DimCore
import java.nio.ByteBuffer
import java.util.*
import java.util.stream.Stream

object SchematicPlacer {
    @JvmField
    val LOGGER: Logger = LogManager.getLogger()

    @JvmStatic
    fun place(schematic: Schematic, world: ServerLevel, origin: BlockPos) {
        LOGGER.debug("Placing schematic: {}", schematic.metadata.name)
        for (id in schematic.metadata.requiredMods) {
            if (!DimCore.platform.isModLoaded(id)) {
                LOGGER.warn("Schematic \"" + schematic.metadata.name + "\" depends on mod \"" + id + "\", which is missing!")
            }
        }
        val blockSample = Schematic.getBlockSample(schematic)
        blockSample.place(origin, world, false, world.registryAccess())
    }

    @JvmStatic
    fun getBlockData(schematic: Schematic): Array<Array<IntArray>> {
        val width = schematic.width.toInt()
        val height = schematic.height.toInt()
        val length = schematic.length.toInt()
        val blockDataBuffer = schematic.blockData.asReadOnlyBuffer()
        blockDataBuffer.rewind()
        val blockData = Array(width) { Array(height) { IntArray(length) } }
        // Sponge schematic v2 stores palette indices as unsigned varints in x + z * width + y * width * length order.
        for (y in 0 until height) {
            for (z in 0 until length) {
                for (x in 0 until width) {
                    blockData[x][y][z] = readVarInt(blockDataBuffer, schematic)
                }
            }
        }
        if (blockDataBuffer.hasRemaining()) {
            LOGGER.warn("Schematic \"{}\" has {} unread BlockData bytes after decoding {} blocks.", schematic.metadata.name, blockDataBuffer.remaining(), width * height * length)
        }
        return blockData
    }

    private fun readVarInt(buffer: ByteBuffer, schematic: Schematic): Int {
        var value = 0
        var shift = 0

        for (byteCount in 0 until 5) {
            if (!buffer.hasRemaining()) {
                throw IllegalArgumentException("Schematic \"" + schematic.metadata.name + "\" ended before all BlockData varints could be decoded.")
            }

            val nextByte = buffer.get().toInt() and 0xFF
            value = value or ((nextByte and 0x7F) shl shift)

            if (nextByte and 0x80 == 0) {
                return value
            }

            shift += 7
        }

        throw IllegalArgumentException("Schematic \"" + schematic.metadata.name + "\" contains a BlockData varint that is too large.")
    }

    @JvmStatic
    fun getBiomeData(schematic: Schematic): Array<IntArray> {
//        int width = schematic.getWidth();
//        int length = schematic.getLength();
//        byte[] biomeDataArray = schematic.getBiomeData().array();
//        if (biomeDataArray.length == 0) return new int[0][0];
//        int[][] biomeData = new int[width][length];
//        for (int x = 0; x < width; x++) {
//            for (int z = 0; z < length; z++) {
//                biomeData[x][z] = biomeDataArray[x + z * width];
//            }
//        }
//        return biomeData;
        return arrayOf()
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

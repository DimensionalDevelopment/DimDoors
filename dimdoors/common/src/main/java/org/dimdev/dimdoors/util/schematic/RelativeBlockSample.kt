package org.dimdev.dimdoors.util.schematic

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.Maps
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.CommandBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.CommandBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

class RelativeBlockSample(@JvmField val schematic: Schematic) {
    private val blockDataContainer: MutableMap<BlockPos, BlockData> = Maps.newHashMap()
    private val entityContainer: BiMap<CompoundTag, Vec3> = HashBiMap.create()

    private class BlockData {
        var state: BlockState? = null
        var blockEntity: CompoundTag? = null
        var biome: Biome? = null
    }

    init {
        val blockData = SchematicPlacer.getBlockData(schematic)
        val biomeData = SchematicPlacer.getBiomeData(schematic)
        val blockPalette: BiMap<BlockState, Int> = ImmutableBiMap.copyOf(schematic.blockPalette)
        /*ImmutableBiMap.copyOf(schematic.getBiomePalette());*/
        val biomePalette: BiMap<Biome, Int> = HashBiMap.create(0)
        val width = schematic.width.toInt()
        val height = schematic.height.toInt()
        val length = schematic.length.toInt()

        val hasBiomes = biomeData.isNotEmpty()

        for (x in 0 until width) {
            for (y in 0 until height) {
                for (z in 0 until length) {

                    val data = blockDataContainer.computeIfAbsent(BlockPos(x, y, z)) { BlockData() }
                    data.state = blockPalette.inverse()[blockData[x][y][z]]
                    if (hasBiomes) data.biome = biomePalette.inverse()[biomeData[x][z]]
                }
            }
        }

        for (blockEntityNbt in schematic.blockEntities) {
            val arr = blockEntityNbt.getIntArray("Pos")
            if (arr.size < 3) continue
            val position = BlockPos(arr[0], arr[1], arr[2])

            blockDataContainer.computeIfAbsent(position) { BlockData() }.blockEntity = toVanilla(blockEntityNbt)
        }

        for (entityNbt in schematic.entities) {
            val doubles = entityNbt.getList("Pos", Tag.TAG_DOUBLE.toInt())
            this.entityContainer[toVanilla(entityNbt)] = Vec3(doubles.getDouble(0), doubles.getDouble(1), doubles.getDouble(2)).subtract(Vec3.atLowerCornerOf(schematic.offset))
        }
    }

    fun place(origin: BlockPos, world: ServerLevel, biomes: Boolean, provider: HolderLookup.Provider) {
        shouldUpdate = false

        try {
            this.blockDataContainer.forEach { (pos, data) ->
                val state = data.state
                if (state != null) {
                    val actualPos = origin.offset(pos)

                    world.setBlock(actualPos, state, 0)

                    val blockEntityNbt = data.blockEntity
                    if (blockEntityNbt != null) {
                        val blockEntity = BlockEntity.loadStatic(actualPos, state, blockEntityNbt, provider)
                        if (blockEntity != null) {
                            world.setBlockEntity(blockEntity)
                        }
                    }
                }


            }
        } finally {
            shouldUpdate = true
        }

        this.initializePlacedCommandBlocks(origin, world)


        for ((nbt, value) in this.entityContainer) {
            val doubles = nbt.getList("Pos", Tag.TAG_DOUBLE.toInt())
            val vec = value.add(origin.x.toDouble(), origin.y.toDouble(), origin.z.toDouble())
            doubles.set(0, NbtOps.INSTANCE.createDouble(vec.x))
            doubles.set(1, NbtOps.INSTANCE.createDouble(vec.y))
            doubles.set(2, NbtOps.INSTANCE.createDouble(vec.z))
            nbt.put("Pos", doubles)
            nbt.remove("UUID")
            EntityType.create(nbt, world.level).ifPresent { world.addFreshEntity(it) }
        }
    }

    private fun initializePlacedCommandBlocks(origin: BlockPos, world: ServerLevel) {
        this.blockDataContainer.forEach { (pos, data) ->
            if (data.state?.block !is CommandBlock) {
                return@forEach
            }

            val actualPos = origin.offset(pos)
            val commandBlock = world.getBlockEntity(actualPos) as? CommandBlockEntity ?: return@forEach

            val state = world.getBlockState(actualPos)
            val block = state.block as? CommandBlock ?: return@forEach

            val powered = world.hasNeighborSignal(actualPos)
            commandBlock.setPowered(powered)

            if (commandBlock.mode == CommandBlockEntity.Mode.SEQUENCE) {
                return@forEach
            }

            if (powered || commandBlock.isAutomatic) {
                commandBlock.markConditionMet()
                world.scheduleTick(actualPos, block, 1)
            }
        }
    }

    companion object {
        @JvmField
        var shouldUpdate = true

        private fun toVanilla(sponge: CompoundTag): CompoundTag {
            val vanilla = sponge.getCompound("Data").copy()
            vanilla.putString("id", sponge.getString("Id"))
            if (sponge.contains("Pos")) {
                vanilla.put("Pos", sponge.get("Pos")!!.copy())
            }
            return vanilla
        }
    }
}

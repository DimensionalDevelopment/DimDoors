package org.dimdev.dimdoors.api.util

import com.mojang.serialization.Codec
import net.minecraft.server.TickTask
import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.function.BiConsumer
import java.util.function.Supplier

enum class BlockPlacementType(
    val id: String,
    val useSection: Boolean,
    val markForUpdate: Boolean,
    val blockEntityPlacer: (Level, BlockEntity) -> Unit
) : StringRepresentable {
    // TODO: do we need some update fluids only option?
    SECTION_NO_UPDATE_QUEUE_BLOCK_ENTITY(
        "section_no_update_queue_block_entity",
        true,
        false,
        BlockPlacementType::queueBlockEntity
    ),
    SECTION_NO_UPDATE(
        "section_no_update",
        true,
        false,
        Level::setBlockEntity
    ),
    SECTION_UPDATE(
        "section_update",
        true,
        true,
        Level::setBlockEntity
    ),
    SET_BLOCK_STATE(
        "set_block_state",
        false,
        false,
        Level::setBlockEntity
    ),
    SET_BLOCK_STATE_QUEUE_BLOCK_ENTITY(
        "set_block_state_queue_block_entity",
        false,
        false,
        BlockPlacementType::queueBlockEntity
    );

    fun useSection(): Boolean {
        return useSection
    }

    fun shouldMarkForUpdate(): Boolean {
        return markForUpdate
    }

    override fun getSerializedName(): String {
        return id
    }

    companion object {
        private val idMap = mutableMapOf<String, BlockPlacementType>()

        @JvmField
        val CODEC = StringRepresentable.fromEnum(entries::toTypedArray)

        init {
            for (type in entries) {
                idMap[type.id] = type
            }
        }

        fun getFromId(id: String?): BlockPlacementType? = idMap[id]

        private fun queueBlockEntity(world: Level, blockEntity: BlockEntity) {
            val server = world.server
            server?.tell(TickTask(server.tickCount) { world.setBlockEntity(blockEntity) })
        }
    }
}

package org.dimdev.dimdoors.rift.item

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.Tag
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.PlayerEnderChestContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.storage.LevelResource
import org.dimdev.dimcore.api.ext.*
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.api.util.Location
import org.dimdev.dimdoors.util.CodecUtils
import java.nio.file.Files
import java.util.*

sealed interface Holder : LocationProvider {
    fun findStack(id: UUID): ItemStack?

    companion object {
        val offline = mutableMapOf<UUID, Location?>()

        val CODEC: Codec<Holder> = Codec.either(EntityHolder.CODEC, BlockHolder.CODEC).xmap(
            CodecUtils::mapEither
        ) { holder ->
            when (holder) {
                is EntityHolder -> Either.left(holder)
                is BlockHolder -> Either.right(holder)
            }
        }

        fun entity(entity: Entity) = EntityHolder(entity.uuid)
        fun location(location: Location) = BlockHolder(location)

        fun of(container: Container): Holder? = when (container) {
            is BlockEntity -> container.takeIfOnServer()?.holder()
            is Entity -> container.takeIfOnServer()?.holder()
            is Inventory -> container.player.takeIfOnServer()?.holder()
            is PlayerEnderChestContainer -> server.playerList.players.firstOrNull { it.enderChestInventory === container }?.holder()
            else -> null
        }


        fun Entity.takeIfOnServer() = this.takeUnless { it.level().isClientSide }
        fun BlockEntity.takeIfOnServer() = this.takeUnless { it.level?.isClientSide == true }
        fun Entity.holder() = entity(this)
        val BlockEntity.location: Location? get() = Location(level?.dimension() ?: return null, blockPos)
        fun BlockEntity.holder() = this.location?.let { location(it) }
        fun List<ItemStack>.findStackOrNull(id: UUID) = firstOrNull { ItemRiftRegistry.find(it, id) != null }
    }

    data class EntityHolder(val entity: UUID) : Holder {
        override val providedLocation: Location?
            get() = loaded()?.location ?: offline.computeIfAbsent(entity) { playerFile(it) }

        override fun findStack(id: UUID): ItemStack? = loaded()?.heldStacks?.findStackOrNull(id)

        private fun loaded(): Entity? = server.allLevels.firstNotNullOfOrNull { it.getEntity(entity) }

        companion object {
            val CODEC: Codec<EntityHolder> = UUIDUtil.CODEC.fieldOf("entity").xmap(::EntityHolder, EntityHolder::entity).codec()
        }
    }

    data class BlockHolder(val location: Location) : Holder {
        override val providedLocation get() = location

        override fun findStack(id: UUID): ItemStack? = location.blockEntity?.castOrNull<Container>()?.stacks?.findStackOrNull(id)

        companion object {
            val CODEC: Codec<BlockHolder> = Location.CODEC.xmap(::BlockHolder, BlockHolder::location)
        }
    }

    fun playerFile(player: UUID): Location? {
        val tag = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve("$player.dat").takeIf(Files::exists)?.readCompressedNbt() ?: return null
        val level = tag["Dimension"]?.read(Level.RESOURCE_KEY_CODEC) ?: return null
        val pos = tag.getList("Pos", Tag.TAG_DOUBLE.toInt()).let { list -> BlockPos.containing(list.getDouble(0), list.getDouble(1), list.getDouble(2)) }
        return Location(level, pos)
    }
}

val Entity.location: Location get() = Location(level().dimension(), blockPosition())
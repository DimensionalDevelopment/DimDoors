package org.dimdev.dimdoors.rift.item.holder

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.Tag
import net.minecraft.world.Container
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.horse.AbstractHorse
import net.minecraft.world.entity.decoration.ItemFrame
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.InventoryCarrier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.PlayerEnderChestContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.storage.LevelResource
import org.dimdev.dimcore.api.MapCodecHasHolder
import org.dimdev.dimcore.api.ext.castOrNull
import org.dimdev.dimcore.api.ext.children
import org.dimdev.dimcore.api.ext.read
import org.dimdev.dimcore.api.ext.readCompressedNbt
import org.dimdev.dimcore.mixin.accessor.AbstractHorseAccessor
import org.dimdev.dimdoors.DimensionalDoors.Companion.server
import org.dimdev.dimdoors.rift.targets.LocationProvider
import org.dimdev.dimdoors.util.Location
import java.nio.file.Files
import java.util.*

interface Holder : MapCodecHasHolder<Holder>,  LocationProvider {
    fun findStack(): ItemStack?
    val owner: UUID? get() = null
    val root: Holder get() = this

    data class EntityHolder(val uuid: UUID, val slot: Int) : Holder {
        override val owner get() = uuid
        override val providedLocation: Location?
            get() = loaded(uuid)?.location /*?: offline.computeIfAbsent(
                entity,
                ::playerFile
            )*/

        override fun findStack() = when(val entity = loaded(uuid)) {
            is Player -> entity.inventory.getItem(slot)
            is Container -> entity.getItem(slot)
            is LivingEntity -> EquipmentSlot.entries.getOrNull(slot)?.let(entity::getItemBySlot)
            is ItemEntity -> entity.item
            is ItemFrame -> entity.item
            else -> null
        }

        override val type: MapCodec<out EntityHolder>
            get() = ItemHolders.ENTITY

        companion object {
            fun loaded(entity: UUID): Entity? = server.allLevels.firstNotNullOfOrNull { it.getEntity(entity) }

            val CODEC: MapCodec<EntityHolder> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    UUIDUtil.CODEC.fieldOf("uuid").forGetter(EntityHolder::uuid),
                    Codec.INT.fieldOf("slot").forGetter(EntityHolder::slot)
                ).apply(instance, ::EntityHolder)
            }
        }
    }

    data class BlockHolder(val location: Location, val slot: Int) : Holder {
        override val providedLocation get() = location
        override fun findStack() = location.blockEntity?.castOrNull<Container>()?.getItem(slot)
        override val type get() = ItemHolders.BLOCK

        companion object {
            val CODEC: MapCodec<BlockHolder> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Location.CODEC.fieldOf("location").forGetter(BlockHolder::location),
                    Codec.INT.fieldOf("slot").forGetter(BlockHolder::slot)
                ).apply(instance, ::BlockHolder)

            }
        }
    }

    data class NestedHolder(val parent: Holder, val index: Int) : Holder {
        override val owner get() = parent.owner
        override val root get() = parent.root
        override val providedLocation get() = parent.providedLocation
        override fun findStack() = parent.findStack()?.children?.elementAtOrNull(index)
        override val type: MapCodec<out Holder>
            get() = ItemHolders.NESTED

        companion object {
            val CODEC: MapCodec<NestedHolder> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                    Codec.lazyInitialized { Holder.CODEC }.fieldOf("holder").forGetter(NestedHolder::parent),
                    Codec.INT.fieldOf("index").forGetter(NestedHolder::index)
                ).apply(instance, ::NestedHolder)
            }
        }
    }

    data class EnderChestHolder(val uuid: UUID, val slot: Int) : Holder {
        override val providedLocation get() = EntityHolder.loaded(uuid)?.location
        override fun findStack() = EntityHolder.loaded(uuid)?.castOrNull<Player>()?.enderChestInventory?.getItem(slot)
        override val type get() = ItemHolders.ENDER_CHEST

        companion object {
            val CODEC: MapCodec<EnderChestHolder> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(EnderChestHolder::uuid),
                Codec.INT.fieldOf("slot").forGetter(EnderChestHolder::slot)
            ).apply(instance, ::EnderChestHolder) }
        }
    }

    data class EntityInventoryHolder(val uuid: UUID, val slot: Int) : Holder {
        override val owner get() = uuid
        override val providedLocation get() = EntityHolder.loaded(uuid)?.location
        override fun findStack() = when (val entity = EntityHolder.loaded(uuid)) {
            is AbstractHorse -> (entity as AbstractHorseAccessor).horseInventory.getItem(slot)
            is InventoryCarrier -> entity.inventory.getItem(slot)
            else -> null
        }
        override val type get() = ItemHolders.ENTITY_INVENTORY

        companion object {
            val CODEC: MapCodec<EntityInventoryHolder> = RecordCodecBuilder.mapCodec { instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(EntityInventoryHolder::uuid),
                Codec.INT.fieldOf("slot").forGetter(EntityInventoryHolder::slot)
            ).apply(instance, ::EntityInventoryHolder) }
        }
    }

    companion object {
        const val RECURSION_DEPTH: Int = 4
        val offline = mutableMapOf<UUID, Location?>()

        val CODEC: Codec<Holder> = ItemHolders.codec

        fun entity(entity: Entity, index: Int) = EntityHolder(entity.uuid, index)
        fun location(location: Location, index: Int) = BlockHolder(location, index)

        fun of(container: Container, index: Int): Holder? = when (container) {
            is BlockEntity -> container.takeIfOnServer()?.holder(index)
            is Entity -> container.takeIfOnServer()?.holder(index)
            is Inventory -> container.player.takeIfOnServer()?.holder(index)
            is PlayerEnderChestContainer -> server.playerList.players.firstOrNull { it.enderChestInventory === container }?.let { EnderChestHolder(it.uuid, index) }
            else -> null
        }

        fun Entity.takeIfOnServer() = this.takeUnless { it.level().isClientSide }
        fun BlockEntity.takeIfOnServer() = this.takeUnless { it.level?.isClientSide == true }
        fun Entity.holder(index: Int) = entity(this, index)
        val BlockEntity.location: Location? get() = Location(level?.dimension() ?: return null, blockPos)
        fun BlockEntity.holder(index: Int) = this.location?.let { location(it, index) }
    }


    fun playerFile(player: UUID): Location? {
        val tag = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve("$player.dat").takeIf(Files::exists)?.readCompressedNbt() ?: return null
        val level = tag["Dimension"]?.read(Level.RESOURCE_KEY_CODEC) ?: return null
        val pos = tag.getList("Pos", Tag.TAG_DOUBLE.toInt()).let { list -> BlockPos.containing(list.getDouble(0), list.getDouble(1), list.getDouble(2)) }
        return Location(level, pos)
    }
}

val Entity.location: Location get() = Location(level().dimension(), blockPosition())
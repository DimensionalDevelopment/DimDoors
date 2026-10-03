package org.dimdev.dimcore.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.DimCore.transfer
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.util.SimpleEvent

class TransferType<U : Unit<U>> private constructor(
    private val id: ResourceLocation,
    private val construct: (Any, DataComponentPatch, Long) -> U,
    private val serialize: (U) -> Tag,
    private val deserialize: (Tag) -> U
) {
    val lookup: SimpleEvent<Lookup<U>> = SimpleEvent.of { listeners -> { level, pos, side ->
            listeners.firstNotNullOfOrNull { it.find(level, pos, side) }
        }
    }

    val expose: SimpleEvent<Expose<U>> = SimpleEvent.of { listeners -> { blockEntity, side ->
            listeners.firstNotNullOfOrNull { it.expose(blockEntity, side) }
        }
    }

    fun id(): ResourceLocation {
        return id
    }

    fun create(resource: Any, components: DataComponentPatch, amount: Long): U = construct(resource, components, amount)

    fun save(unit: U): Tag = serialize(unit)

    fun load(tag: Tag): U = deserialize(tag)

    fun find(level: Level?, pos: BlockPos?, side: Direction?): Handle<U>? = lookup.invoker().find(level, pos, side) ?: transfer.find(this, level, pos, side)

    fun expose(blockEntity: BlockEntity, side: Direction?): Handle<U>? {
        return expose.invoker().expose(blockEntity, side)
    }

    fun declare(type: BlockEntityType<*>) = transfer.declare(this, type)

    override fun toString() = "TransferType[$id]"

    fun interface Lookup<U : Unit<U>> {
        fun find(level: Level?, pos: BlockPos?, side: Direction?): Handle<U>?
    }

    fun interface Expose<U : Unit<U>> {
        fun expose(blockEntity: BlockEntity?, side: Direction?): Handle<U>?
    }

    companion object {
        private val TYPES = mutableMapOf<ResourceLocation, TransferType<*>>()

        val FLUID = create(
            ResourceLocation.fromNamespaceAndPath("dimcore", "fluid"),
            { resource, components, amount -> FluidUnit(resource as Fluid, components, amount) },
            { unit -> tag(BuiltInRegistries.FLUID, unit.fluid, unit) },
            { tag ->
                val compound = tag.cast<CompoundTag>()
                FluidUnit(BuiltInRegistries.FLUID.resource(compound), compound.components(), compound.getLong("amount")) }
        )

        val ITEM = create(
            ResourceLocation.fromNamespaceAndPath("dimcore", "item"),
            { resource, components, amount -> ItemUnit(resource as Item, components, amount) },
            { unit -> tag(BuiltInRegistries.ITEM, unit.item, unit) },
            { tag ->
                val compound = tag.cast<CompoundTag>()
                ItemUnit(BuiltInRegistries.ITEM.resource(compound), compound.components(), compound.getLong("amount"))
            }
        )

        val ENERGY = create(
            ResourceLocation.fromNamespaceAndPath("dimcore", "energy"),
            { _, _, amount -> EnergyUnit(amount) },
            { unit -> LongTag.valueOf(unit.amount) },
            { tag -> EnergyUnit(tag.cast<LongTag>().asLong) }
        )

        @Synchronized
        fun <U : Unit<U>> create(
            id: ResourceLocation,
            construct: (Any, DataComponentPatch, Long) -> U,
            serialize: (U) -> Tag,
            deserialize: (Tag) -> U
        ): TransferType<U> {
            check(!TYPES.containsKey(id)) { "Transfer type already registered: $id" }
            return TransferType(id, construct, serialize, deserialize).also { TYPES[id] = it }
        }

        operator fun get(id: ResourceLocation?): TransferType<*>? = TYPES[id]

        fun all() = TYPES.values.toSet()

        private fun <T> Registry<T>.resource(tag: CompoundTag): T = get(ResourceLocation.parse(tag.getString("id")))!!

        private fun CompoundTag.components(): DataComponentPatch =
            if (contains("components")) DataComponentPatch.CODEC.parse(NbtOps.INSTANCE, get("components")).result().orElse(DataComponentPatch.EMPTY) else DataComponentPatch.EMPTY

        private fun <T> tag(registry: Registry<T>, resource: T, unit: Unit<*>): CompoundTag = CompoundTag().apply {
            putString("id", registry.getKey(resource).toString())
            if (!unit.components.isEmpty) {
                DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, unit.components).result().ifPresent { put("components", it) }
            }
            putLong("amount", unit.amount)
        }
    }
}

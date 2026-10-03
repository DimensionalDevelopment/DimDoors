package org.dimdev.dimcore.transfer

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.Storage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import org.dimdev.dimcore.api.ext.cast
import org.dimdev.dimcore.api.transfer.*
import org.dimdev.dimcore.api.transfer.Unit
import team.reborn.energy.api.EnergyStorage

class FabricTransfer : TransferBridgeImpl() {
    override fun interactWithFluid(
        player: Player?,
        hand: InteractionHand?,
        level: Level?,
        pos: BlockPos?,
        side: Direction?
    ): Boolean {
        val storage = FluidStorage.SIDED.find(level, pos, side)
        return storage != null && FluidStorageUtil.interactWithFluidStorage(storage, player, hand)
    }

    init {
        bind(TransferType.FLUID, FluidStorage.SIDED.sided(), ::wrap) { type, handle -> unwrap(type, handle) }
        bind(TransferType.ITEM, ItemStorage.SIDED.sided(), ::wrap, ::unwrap)
        bind(TransferType.ENERGY, EnergyStorage.SIDED.sided(), ::wrapEnergy, ::unwrapEnergy)
    }

    fun <U : Unit<U>, V> wrap(type: TransferType<U>, storage: Storage<V>): Handle<U> =
        if (storage is HandleStorage<*>) storage.handle.cast() else StorageHandle(type, storage.cast())

    fun <U : Unit<U>, V> unwrap(type: TransferType<U>, handle: Handle<out U>): Storage<V> = if (handle is StorageHandle<*>) handle.storage.cast() else HandleStorage(type, handle.cast()).cast()

    fun wrapEnergy(type: TransferType<EnergyUnit>, storage: EnergyStorage): Handle<EnergyUnit> =
        if (storage is HandleEnergyStorage) storage.handle else EnergyStorageHandle(storage)

    fun unwrapEnergy(type: TransferType<EnergyUnit>, handle: Handle<EnergyUnit>): EnergyStorage =
        if (handle is EnergyStorageHandle) handle.storage else HandleEnergyStorage(handle)

    @Suppress("UNCHECKED_CAST")
    fun <H : Any> BlockApiLookup<H, Direction?>.sided(): Lookup<H, BlockApiLookup<H, Direction?>> = Lookup(this,
        { lookup, level, pos, side -> lookup.find(level, pos, side) },
        { lookup, type, provider -> lookup.registerForBlockEntity({ blockEntity, side -> provider(blockEntity, side) }, type) })

    companion object {
        private val Unit<*>.variant: TransferVariant<*>
            get() = when (val resource = resource) {
                is Item -> ItemVariant.of(resource, components)
                is Fluid -> FluidVariant.of(resource, components)
                else -> error("No Fabric variant for $resource")
            }

        private fun <U : Unit<U>> TransferType<U>.unit(variant: Any, amount: Long): U = (variant as TransferVariant<*>).let { create(it.`object`, it.components, amount) }

        private fun Transaction.finish(simulate: Boolean) = if (simulate) abort() else commit()
    }

    private class StorageHandle<U : Unit<U>>(val type: TransferType<U>, val storage: Storage<Any>) : Handle<U> {
        override fun insert(unit: U, simulate: Boolean): Long = Transaction.openOuter().use { transaction ->
            storage.insert(unit.variant, unit.amount, transaction).also { transaction.finish(simulate) }
        }
        override fun extract(unit: U, simulate: Boolean): Long = Transaction.openOuter().use { transaction -> storage.extract(unit.variant, unit.amount, transaction).also { transaction.finish(simulate) } }
        override fun contents(): List<U> = storage.filter { !it.isResourceBlank && it.amount > 0 }.map { type.unit(it.resource, it.amount) }
    }

    private class HandleStorage<U : Unit<U>>(val type: TransferType<U>, val handle: Handle<U>) : Storage<Any> {
        override fun insert(resource: Any, maxAmount: Long, transaction: TransactionContext): Long {
            if ((resource as TransferVariant<*>).isBlank || maxAmount <= 0) return 0
            val unit = type.unit(resource, maxAmount)
            val accepted = handle.insert(unit, true)
            if (accepted > 0) {
                val committed = unit.withAmount(accepted)
                transaction.addCloseCallback { _, result -> if (result.wasCommitted()) handle.insert(committed, false) }
            }
            return accepted
        }

        override fun extract(resource: Any, maxAmount: Long, transaction: TransactionContext): Long {
            if ((resource as TransferVariant<*>).isBlank || maxAmount <= 0) return 0
            val unit = type.unit(resource, maxAmount)
            val removed = handle.extract(unit, true)
            if (removed > 0) {
                val committed = unit.withAmount(removed)
                transaction.addCloseCallback { _, result -> if (result.wasCommitted()) handle.extract(committed, false) }
            }
            return removed
        }

        override fun iterator(): MutableIterator<StorageView<Any>> = handle.contents().map<U, StorageView<Any>> { HandleView(this, it) }.toMutableList().iterator()
    }

    private class EnergyStorageHandle(val storage: EnergyStorage) : Handle<EnergyUnit> {
        override fun insert(unit: EnergyUnit, simulate: Boolean): Long = Transaction.openOuter().use { transaction ->
            storage.insert(unit.amount, transaction).also { transaction.finish(simulate) }
        }
        override fun extract(unit: EnergyUnit, simulate: Boolean): Long = Transaction.openOuter().use { transaction ->
            storage.extract(unit.amount, transaction).also { transaction.finish(simulate) }
        }
        override fun contents(): List<EnergyUnit> = if (storage.amount > 0) listOf(EnergyUnit(storage.amount)) else emptyList()
    }

    private class HandleEnergyStorage(val handle: Handle<EnergyUnit>) : EnergyStorage {
        override fun insert(maxAmount: Long, transaction: TransactionContext): Long {
            if (maxAmount <= 0) return 0
            val accepted = handle.insert(EnergyUnit(maxAmount), true)
            if (accepted > 0) transaction.addCloseCallback { _, result -> if (result.wasCommitted()) handle.insert(EnergyUnit(accepted), false) }
            return accepted
        }

        override fun extract(maxAmount: Long, transaction: TransactionContext): Long {
            if (maxAmount <= 0) return 0
            val removed = handle.extract(EnergyUnit(maxAmount), true)
            if (removed > 0) transaction.addCloseCallback { _, result -> if (result.wasCommitted()) handle.extract(EnergyUnit(removed), false) }
            return removed
        }

        override fun getAmount(): Long = handle.contents().sumOf { it.amount }
        override fun getCapacity(): Long = Long.MAX_VALUE
    }

    private class HandleView<U : Unit<U>>(val owner: HandleStorage<U>, val held: U) : StorageView<Any> {
        override fun extract(resource: Any, maxAmount: Long, transaction: TransactionContext): Long = owner.extract(resource, maxAmount, transaction)
        override fun isResourceBlank(): Boolean = held.isEmpty
        override fun getResource(): Any = held.variant
        override fun getAmount(): Long = held.amount
        override fun getCapacity(): Long = Long.MAX_VALUE
    }
}

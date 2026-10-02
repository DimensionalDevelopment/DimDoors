package org.dimdev.dimcore.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.ItemHandlerHelper
import org.dimdev.dimcore.api.transfer.*
import org.dimdev.dimcore.api.transfer.ItemUnit.Companion.unit
import kotlin.math.max
import kotlin.math.min

class NeoForgeTransfer : TransferBridgeImpl() {
    override fun interactWithFluid(
        player: Player?,
        hand: InteractionHand?,
        level: Level?,
        pos: BlockPos?,
        side: Direction?
    ): Boolean {
        return FluidUtil.interactWithFluidHandler(player, hand, level, pos, side)
    }

    private val pending = mutableListOf<(RegisterCapabilitiesEvent) -> Unit>()

    init {
        bind(TransferType.FLUID, Capabilities.FluidHandler.BLOCK.sided(), { _, handler -> handler.handle }, { _, handle -> handle.handler })
        bind(TransferType.ITEM, Capabilities.ItemHandler.BLOCK.sided(), { _, handler -> handler.handle }, { _, handle -> handle.handler })
        bind(TransferType.ENERGY, Capabilities.EnergyStorage.BLOCK.sided(), { _, handler -> handler.handle }, { _, handle -> handle.handler })
    }

    fun init() {}

    fun registerExposed(event: RegisterCapabilitiesEvent) {
        pending.forEach { it(event) }
        pending.clear()
    }

    fun <H : Any> BlockCapability<H, Direction?>.sided(): Lookup<H, BlockCapability<H, Direction?>> = Lookup(this,
        { capability, level, pos, side -> level.getCapability(capability, pos, side) },
        { capability, type, provider -> pending += { event ->
            event.registerBlockEntity(capability, type) { blockEntity, side -> provider(blockEntity, side) }
        } })

    companion object {
        val IFluidHandler.handle: Handle<FluidUnit> get() = if (this is HandleFluidHandler) handle else FluidHandlerHandle(this)
        @get:JvmName("fluidHandler")
        val Handle<FluidUnit>.handler: IFluidHandler get() = if (this is FluidHandlerHandle) handler else HandleFluidHandler(this)

        val IItemHandler.handle: Handle<ItemUnit> get() = if (this is HandleItemHandler) handle else ItemHandlerHandle(this)
        @get:JvmName("itemHandler")
        val Handle<ItemUnit>.handler: IItemHandler get() = if (this is ItemHandlerHandle) handler else HandleItemHandler(this)

        val IEnergyStorage.handle: Handle<EnergyUnit> get() = if (this is HandleEnergyStorage) handle else EnergyStorageHandle(this)
        @get:JvmName("energyStorage")
        val Handle<EnergyUnit>.handler: IEnergyStorage get() = if (this is EnergyStorageHandle) handler else HandleEnergyStorage(this)

        fun FluidUnit.toStack(): FluidStack = FluidStack(fluid.builtInRegistryHolder(), clamp(amount), components)

        fun FluidStack.toUnit(): FluidUnit? = if (isEmpty) null else TransferType.FLUID.create(fluid, componentsPatch, amount.toLong())

        private fun clamp(amount: Long): Int = amount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

        private val Boolean.action: IFluidHandler.FluidAction get() = if (this) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
    }

    private class FluidHandlerHandle(val handler: IFluidHandler) : Handle<FluidUnit> {
        override fun insert(unit: FluidUnit, simulate: Boolean): Long = handler.fill(unit.toStack(), simulate.action).toLong()

        override fun extract(unit: FluidUnit, simulate: Boolean): Long = handler.drain(unit.toStack(), simulate.action).amount.toLong()

        override fun contents(): List<FluidUnit> = (0..<handler.tanks).mapNotNull { handler.getFluidInTank(it).toUnit() }

        override fun extractAny(amount: Long, simulate: Boolean): FluidUnit? = handler.drain(clamp(amount), simulate.action).toUnit()
    }

    private class HandleFluidHandler(val handle: Handle<FluidUnit>) : IFluidHandler {
        override fun getTanks(): Int = max(1, handle.contents().size)

        override fun getFluidInTank(tank: Int): FluidStack {
            val contents = handle.contents()
            return if (tank < contents.size) contents[tank].toStack() else FluidStack.EMPTY
        }

        override fun getTankCapacity(tank: Int): Int = Int.MAX_VALUE

        override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = true

        override fun fill(resource: FluidStack, action: IFluidHandler.FluidAction): Int {
            val unit = resource.toUnit() ?: return 0
            return clamp(handle.insert(unit, action.simulate()))
        }

        override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack {
            val unit = resource.toUnit() ?: return FluidStack.EMPTY
            val drained = handle.extract(unit, action.simulate())
            return if (drained <= 0) FluidStack.EMPTY else resource.copyWithAmount(clamp(drained))
        }

        override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack =
            handle.extractAny(maxDrain.toLong(), action.simulate())?.toStack() ?: FluidStack.EMPTY
    }

    private class ItemHandlerHandle(val handler: IItemHandler) : Handle<ItemUnit> {
        override fun insert(unit: ItemUnit, simulate: Boolean): Long {
            val stack = unit.toStack()
            val remainder = ItemHandlerHelper.insertItem(handler, stack, simulate)
            return (stack.count - remainder.count).toLong()
        }

        override fun extract(unit: ItemUnit, simulate: Boolean): Long {
            var remaining = unit.amount
            for (slot in 0..<handler.slots) {
                if (remaining <= 0) break
                val inSlot = handler.getStackInSlot(slot)
                if (inSlot.isEmpty || !unit.sameResource(inSlot.unit)) continue
                remaining -= handler.extractItem(slot, clamp(remaining), simulate).count.toLong()
            }
            return unit.amount - remaining
        }

        override fun contents(): List<ItemUnit> = (0..<handler.slots)
            .map { handler.getStackInSlot(it) }
            .filter { !it.isEmpty }
            .map { it.unit }
    }

    private class HandleItemHandler(val handle: Handle<ItemUnit>) : IItemHandler {
        override fun getSlots(): Int = max(1, handle.contents().size)

        override fun getStackInSlot(slot: Int): ItemStack = handle.contents().takeIf { slot < it.size }?.get(slot)?.toStack() ?: ItemStack.EMPTY

        override fun getSlotLimit(slot: Int): Int = 64

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (stack.isEmpty) return ItemStack.EMPTY
            val accepted = handle.insert(stack.unit, simulate)
            return if (accepted >= stack.count) ItemStack.EMPTY else stack.copyWithCount(stack.count - clamp(accepted))
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            val contents = handle.contents()
            if (slot >= contents.size) return ItemStack.EMPTY
            val held = contents[slot]
            val taken = handle.extract(held.withAmount(min(amount.toLong(), held.amount)), simulate)
            return if (taken <= 0) ItemStack.EMPTY else held.withAmount(taken).toStack()
        }
    }

    private class EnergyStorageHandle(val handler: IEnergyStorage) : Handle<EnergyUnit> {
        override fun insert(unit: EnergyUnit, simulate: Boolean): Long = handler.receiveEnergy(clamp(unit.amount), simulate).toLong()

        override fun extract(unit: EnergyUnit, simulate: Boolean): Long = handler.extractEnergy(clamp(unit.amount), simulate).toLong()

        override fun contents(): List<EnergyUnit> = handler.energyStored.takeIf { it > 0 }?.let { listOf(EnergyUnit(it.toLong())) } ?: emptyList()
    }

    private class HandleEnergyStorage(val handle: Handle<EnergyUnit>) : IEnergyStorage {
        override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int = clamp(handle.insert(EnergyUnit(toReceive.toLong()), simulate))

        override fun extractEnergy(toExtract: Int, simulate: Boolean): Int = clamp(handle.extract(EnergyUnit(toExtract.toLong()), simulate))

        override fun getEnergyStored(): Int = clamp(handle.contents().sumOf { it.amount })

        override fun getMaxEnergyStored(): Int = Int.MAX_VALUE

        override fun canExtract(): Boolean = true

        override fun canReceive(): Boolean = true
    }
}

package org.dimdev.dimcore.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.dimdev.dimcore.api.transfer.FluidUnit;
import org.dimdev.dimcore.api.transfer.Handle;
import org.dimdev.dimcore.api.transfer.ItemUnit;
import org.dimdev.dimcore.api.transfer.TransferBridge;
import org.dimdev.dimcore.api.transfer.TransferType;
import org.dimdev.dimcore.api.transfer.Unit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Bridges {@link Handle} to and from NeoForge's {@link IFluidHandler} and {@link IItemHandler}. */
public final class NeoForgeTransfer implements TransferBridge {
    private static final Map<TransferType<?>, Binding<?, ?>> BINDINGS = new HashMap<>();

    static {
        bind(TransferType.FLUID, Capabilities.FluidHandler.BLOCK, NeoForgeTransfer::of, NeoForgeTransfer::fluidHandler);
        bind(TransferType.ITEM, Capabilities.ItemHandler.BLOCK, NeoForgeTransfer::of, NeoForgeTransfer::itemHandler);
    }

    /**
     * Binds a type to a sided {@link BlockCapability}: {@link TransferType#find} answers through it,
     * and every {@link TransferType#declare declared} block entity type is exposed on it.
     */
    public static <U extends Unit<U>, H> void bind(
            TransferType<U> type, BlockCapability<H, @Nullable Direction> capability, Function<H, Handle<U>> toHandle, Function<Handle<U>, H> toHandler) {
        BINDINGS.put(type, new Binding<>(type, capability, toHandle, toHandler));
    }

    @Override
    public <U extends Unit<U>> @Nullable Handle<U> find(TransferType<U> type, Level level, BlockPos pos, @Nullable Direction side) {
        Binding<U, ?> binding = binding(type);
        return binding == null ? null : binding.find(level, pos, side);
    }

    @Override
    public boolean interactWithFluid(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side) {
        return FluidUtil.interactWithFluidHandler(player, hand, level, pos, side);
    }

    /** Exposes every {@link TransferType#declare declared} block entity type of every bound type as its capability. */
    public static void registerExposed(RegisterCapabilitiesEvent event) {
        BINDINGS.values().forEach(binding -> binding.registerExposed(event));
    }

    @SuppressWarnings("unchecked")
    private static <U extends Unit<U>> @Nullable Binding<U, ?> binding(TransferType<U> type) {
        return (Binding<U, ?>) BINDINGS.get(type);
    }

    private record Binding<U extends Unit<U>, H>(
            TransferType<U> type, BlockCapability<H, @Nullable Direction> capability, Function<H, Handle<U>> toHandle, Function<Handle<U>, H> toHandler) {
        private @Nullable Handle<U> find(Level level, BlockPos pos, @Nullable Direction side) {
            H handler = level.getCapability(capability, pos, side);
            return handler == null ? null : toHandle.apply(handler);
        }

        private void registerExposed(RegisterCapabilitiesEvent event) {
            for (BlockEntityType<?> blockEntityType : type.declared()) {
                event.registerBlockEntity(capability, blockEntityType, (blockEntity, side) -> {
                    Handle<U> handle = type.expose(blockEntity, side);
                    return handle == null ? null : toHandler.apply(handle);
                });
            }
        }
    }

    // ---- fluid ----

    public static Handle<FluidUnit> of(IFluidHandler handler) {
        return handler instanceof HandleFluidHandler(Handle<FluidUnit> handle) ? handle : new FluidHandlerHandle(handler);
    }

    public static IFluidHandler fluidHandler(Handle<FluidUnit> handle) {
        return new HandleFluidHandler(handle);
    }

    public static FluidStack toStack(FluidUnit unit) {
        return new FluidStack(unit.fluid().builtInRegistryHolder(), clamp(unit.amount()), unit.components());
    }

    public static @Nullable FluidUnit toUnit(FluidStack stack) {
        return stack.isEmpty() ? null : new FluidUnit(stack.getFluid(), stack.getComponentsPatch(), stack.getAmount());
    }

    private record FluidHandlerHandle(IFluidHandler handler) implements Handle<FluidUnit> {
        @Override
        public long insert(FluidUnit unit, boolean simulate) {
            return handler.fill(toStack(unit), action(simulate));
        }

        @Override
        public long extract(FluidUnit unit, boolean simulate) {
            return handler.drain(toStack(unit), action(simulate)).getAmount();
        }

        @Override
        public List<FluidUnit> contents() {
            List<FluidUnit> units = new ArrayList<>(handler.getTanks());
            for (int tank = 0; tank < handler.getTanks(); tank++) {
                FluidUnit unit = toUnit(handler.getFluidInTank(tank));
                if (unit != null) {
                    units.add(unit);
                }
            }
            return units;
        }

        @Override
        public @Nullable FluidUnit extractAny(long amount, boolean simulate) {
            return toUnit(handler.drain(clamp(amount), action(simulate)));
        }
    }

    private record HandleFluidHandler(Handle<FluidUnit> handle) implements IFluidHandler {
        @Override
        public int getTanks() {
            return Math.max(1, handle.contents().size());
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            List<FluidUnit> contents = handle.contents();
            return tank < contents.size() ? toStack(contents.get(tank)) : FluidStack.EMPTY;
        }

        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return true;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            FluidUnit unit = toUnit(resource);
            return unit == null ? 0 : clamp(handle.insert(unit, action.simulate()));
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) {
            FluidUnit unit = toUnit(resource);
            if (unit == null) {
                return FluidStack.EMPTY;
            }
            long drained = handle.extract(unit, action.simulate());
            return drained <= 0 ? FluidStack.EMPTY : resource.copyWithAmount(clamp(drained));
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action) {
            FluidUnit unit = handle.extractAny(maxDrain, action.simulate());
            return unit == null ? FluidStack.EMPTY : toStack(unit);
        }
    }

    // ---- item ----

    public static Handle<ItemUnit> of(IItemHandler handler) {
        return handler instanceof HandleItemHandler(Handle<ItemUnit> handle) ? handle : new ItemHandlerHandle(handler);
    }

    public static IItemHandler itemHandler(Handle<ItemUnit> handle) {
        return new HandleItemHandler(handle);
    }

    private record ItemHandlerHandle(IItemHandler handler) implements Handle<ItemUnit> {
        @Override
        public long insert(ItemUnit unit, boolean simulate) {
            ItemStack stack = unit.toStack();
            ItemStack remainder = ItemHandlerHelper.insertItem(handler, stack, simulate);
            return stack.getCount() - remainder.getCount();
        }

        @Override
        public long extract(ItemUnit unit, boolean simulate) {
            long remaining = unit.amount();
            for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
                ItemStack inSlot = handler.getStackInSlot(slot);
                if (inSlot.isEmpty() || !unit.sameResource(ItemUnit.of(inSlot))) {
                    continue;
                }
                remaining -= handler.extractItem(slot, clamp(remaining), simulate).getCount();
            }
            return unit.amount() - remaining;
        }

        @Override
        public List<ItemUnit> contents() {
            List<ItemUnit> units = new ArrayList<>(handler.getSlots());
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    units.add(ItemUnit.of(stack));
                }
            }
            return units;
        }
    }

    /** Slots mirror {@link Handle#contents()}; an empty handle still shows one slot so inserters have somewhere to aim. */
    private record HandleItemHandler(Handle<ItemUnit> handle) implements IItemHandler {
        @Override
        public int getSlots() {
            return Math.max(1, handle.contents().size());
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            List<ItemUnit> contents = handle.contents();
            return slot < contents.size() ? contents.get(slot).toStack() : ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
            long accepted = handle.insert(ItemUnit.of(stack), simulate);
            return accepted >= stack.getCount() ? ItemStack.EMPTY : stack.copyWithCount(stack.getCount() - clamp(accepted));
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            List<ItemUnit> contents = handle.contents();
            if (slot >= contents.size()) {
                return ItemStack.EMPTY;
            }
            ItemUnit held = contents.get(slot);
            long taken = handle.extract(held.withAmount(Math.min(amount, held.amount())), simulate);
            return taken <= 0 ? ItemStack.EMPTY : held.withAmount(taken).toStack();
        }
    }

    // ---- shared ----

    private static int clamp(long amount) {
        return (int) Math.min(amount, Integer.MAX_VALUE);
    }

    private static IFluidHandler.FluidAction action(boolean simulate) {
        return simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
    }
}

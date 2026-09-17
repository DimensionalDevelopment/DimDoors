package org.dimdev.dimcore.transfer;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.dimdev.dimcore.api.transfer.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Bridges {@link Handle} to and from Fabric's {@link Storage}{@code <FluidVariant>} / {@code <ItemVariant>}. */
public final class FabricTransfer implements TransferBridge {
    private static final Codec<FluidUnit, FluidVariant> FLUID = new Codec<>(
            unit -> FluidVariant.of(unit.fluid(), unit.components()),
            (variant, amount) -> new FluidUnit(variant.getFluid(), variant.getComponents(), amount));

    private static final Codec<ItemUnit, ItemVariant> ITEM = new Codec<>(
            unit -> ItemVariant.of(unit.item(), unit.components()),
            (variant, amount) -> new ItemUnit(variant.getItem(), variant.getComponents(), amount));

    @Override
    @SuppressWarnings("unchecked")
    public <U extends Unit<U>> @Nullable Handle<U> find(TransferType<U> type, Level level, BlockPos pos, @Nullable Direction side) {
        if (type == TransferType.FLUID) {
            return (Handle<U>) find(FluidStorage.SIDED, FLUID, level, pos, side);
        }
        if (type == TransferType.ITEM) {
            return (Handle<U>) find(ItemStorage.SIDED, ITEM, level, pos, side);
        }
        return null;
    }

    /** Exposes any block entity whose {@link TransferType#expose} answers as the matching sided storage. */
    public static void registerExposed() {
        registerExposed(TransferType.FLUID, FluidStorage.SIDED, FLUID);
        registerExposed(TransferType.ITEM, ItemStorage.SIDED, ITEM);
    }

    public static Handle<FluidUnit> of(Storage<FluidVariant> storage) {
        return wrap(storage, FLUID);
    }

    public static Storage<FluidVariant> fluidStorage(Handle<FluidUnit> handle) {
        return new HandleStorage<>(handle, FLUID);
    }

    public static Handle<ItemUnit> ofItems(Storage<ItemVariant> storage) {
        return wrap(storage, ITEM);
    }

    public static Storage<ItemVariant> itemStorage(Handle<ItemUnit> handle) {
        return new HandleStorage<>(handle, ITEM);
    }

    @SuppressWarnings("unchecked")
    private static <U extends Unit<U>, V extends TransferVariant<?>> Handle<U> wrap(Storage<V> storage, Codec<U, V> codec) {
        // Our own storage is a view over a handle already; go straight back to it.
        return storage instanceof HandleStorage<?, ?> ours ? (Handle<U>) ours.handle() : new StorageHandle<>(storage, codec);
    }

    private static <U extends Unit<U>, V extends TransferVariant<?>> @Nullable Handle<U> find(
            BlockApiLookup<Storage<V>, Direction> lookup, Codec<U, V> codec, Level level, BlockPos pos, @Nullable Direction side) {
        Storage<V> storage = lookup.find(level, pos, side);
        return storage == null ? null : wrap(storage, codec);
    }

    private static <U extends Unit<U>, V extends TransferVariant<?>> void registerExposed(
            TransferType<U> type, BlockApiLookup<Storage<V>, Direction> lookup, Codec<U, V> codec) {
        lookup.registerFallback((level, pos, state, blockEntity, side) -> {
            if (blockEntity == null) {
                return null;
            }
            Handle<U> handle = type.expose(blockEntity, side);
            return handle == null ? null : new HandleStorage<>(handle, codec);
        });
    }

    private static void finish(Transaction transaction, boolean simulate) {
        if (simulate) {
            transaction.abort();
        } else {
            transaction.commit();
        }
    }

    /** Unit <-> variant conversion for one resource kind. */
    private record Codec<U extends Unit<U>, V extends TransferVariant<?>>(
            Function<U, V> toVariant, BiFunction<V, Long, U> toUnit) {
    }

    private record StorageHandle<U extends Unit<U>, V extends TransferVariant<?>>(
            Storage<V> storage, Codec<U, V> codec) implements Handle<U> {
        @Override
        public long insert(U unit, boolean simulate) {
            try (Transaction transaction = Transaction.openOuter()) {
                long inserted = storage.insert(codec.toVariant().apply(unit), unit.amount(), transaction);
                finish(transaction, simulate);
                return inserted;
            }
        }

        @Override
        public long extract(U unit, boolean simulate) {
            try (Transaction transaction = Transaction.openOuter()) {
                long extracted = storage.extract(codec.toVariant().apply(unit), unit.amount(), transaction);
                finish(transaction, simulate);
                return extracted;
            }
        }

        @Override
        public List<U> contents() {
            List<U> units = new ArrayList<>();
            for (StorageView<V> view : storage) {
                if (!view.isResourceBlank() && view.getAmount() > 0) {
                    units.add(codec.toUnit().apply(view.getResource(), view.getAmount()));
                }
            }
            return units;
        }
    }

    /**
     * Views mirror {@link Handle#contents()}. Fabric transactions can't cross a handle, so each
     * operation is simulated on the handle immediately and executed when the outer transaction commits.
     */
    private record HandleStorage<U extends Unit<U>, V extends TransferVariant<?>>(
            Handle<U> handle, Codec<U, V> codec) implements Storage<V> {
        @Override
        public long insert(V resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            U unit = codec.toUnit().apply(resource, maxAmount);
            long accepted = handle.insert(unit, true);
            if (accepted > 0) {
                U committed = unit.withAmount(accepted);
                transaction.addCloseCallback((context, result) -> {
                    if (result.wasCommitted()) {
                        handle.insert(committed, false);
                    }
                });
            }
            return accepted;
        }

        @Override
        public long extract(V resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            U unit = codec.toUnit().apply(resource, maxAmount);
            long removed = handle.extract(unit, true);
            if (removed > 0) {
                U committed = unit.withAmount(removed);
                transaction.addCloseCallback((context, result) -> {
                    if (result.wasCommitted()) {
                        handle.extract(committed, false);
                    }
                });
            }
            return removed;
        }

        @Override
        public Iterator<StorageView<V>> iterator() {
            List<StorageView<V>> views = new ArrayList<>();
            for (U held : handle.contents()) {
                views.add(new HandleView<>(this, held));
            }
            return views.iterator();
        }
    }

    /** Snapshot of one {@link Handle#contents()} entry; extraction goes back through the owning storage. */
    private record HandleView<U extends Unit<U>, V extends TransferVariant<?>>(
            HandleStorage<U, V> owner, U held) implements StorageView<V> {
        @Override
        public long extract(V resource, long maxAmount, TransactionContext transaction) {
            return owner.extract(resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return held.isEmpty();
        }

        @Override
        public V getResource() {
            return owner.codec().toVariant().apply(held);
        }

        @Override
        public long getAmount() {
            return held.amount();
        }

        @Override
        public long getCapacity() {
            return Long.MAX_VALUE;
        }
    }
}

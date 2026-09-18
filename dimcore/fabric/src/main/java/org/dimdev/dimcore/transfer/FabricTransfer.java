package org.dimdev.dimcore.transfer;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.dimdev.dimcore.api.transfer.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/** Bridges {@link Handle} to and from Fabric's {@link Storage}{@code <FluidVariant>} / {@code <ItemVariant>}. */
public final class FabricTransfer implements TransferBridge {
    private static final Map<TransferType<?>, Binding<?, ?>> BINDINGS = new HashMap<>();

    private static final Codec<FluidUnit, FluidVariant> FLUID = new Codec<>(
            unit -> FluidVariant.of(unit.fluid(), unit.components()),
            (variant, amount) -> new FluidUnit(variant.getFluid(), variant.getComponents(), amount));

    private static final Codec<ItemUnit, ItemVariant> ITEM = new Codec<>(
            unit -> ItemVariant.of(unit.item(), unit.components()),
            (variant, amount) -> new ItemUnit(variant.getItem(), variant.getComponents(), amount));

    static {
        bind(TransferType.FLUID, FluidStorage.SIDED, FLUID.toVariant(), FLUID.toUnit());
        bind(TransferType.ITEM, ItemStorage.SIDED, ITEM.toVariant(), ITEM.toUnit());
    }

    /** Forces the built-in bindings to register; called from DimCore's entrypoint. */
    public static void init() {
    }

    /**
     * Binds a type to a sided {@link BlockApiLookup}: {@link TransferType#find} answers through it,
     * and any block entity whose {@link TransferType#expose} answers is exposed on it as a storage.
     */
    public static <U extends Unit<U>, V extends TransferVariant<?>> void bind(
            TransferType<U> type, BlockApiLookup<Storage<V>, Direction> lookup, Function<U, V> toVariant, BiFunction<V, Long, U> toUnit) {
        Codec<U, V> codec = new Codec<>(toVariant, toUnit);
        BINDINGS.put(type, new Binding<>(lookup, codec));
        lookup.registerFallback((level, pos, state, blockEntity, side) -> {
            if (blockEntity == null) {
                return null;
            }
            Handle<U> handle = type.expose(blockEntity, side);
            return handle == null ? null : new HandleStorage<>(handle, codec);
        });
    }

    @Override
    public <U extends Unit<U>> @Nullable Handle<U> find(TransferType<U> type, Level level, BlockPos pos, @Nullable Direction side) {
        Binding<U, ?> binding = binding(type);
        return binding == null ? null : binding.find(level, pos, side);
    }

    @Override
    public boolean interactWithFluid(Player player, InteractionHand hand, Level level, BlockPos pos, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
        return storage != null && FluidStorageUtil.interactWithFluidStorage(storage, player, hand);
    }

    public static Handle<FluidUnit> of(Storage<FluidVariant> storage) {
        return wrap(storage, FLUID);
    }

    public static Storage<FluidVariant> fluidStorage(Handle<FluidUnit> handle) {
        return storage(TransferType.FLUID, handle);
    }

    public static Handle<ItemUnit> ofItems(Storage<ItemVariant> storage) {
        return wrap(storage, ITEM);
    }

    public static Storage<ItemVariant> itemStorage(Handle<ItemUnit> handle) {
        return storage(TransferType.ITEM, handle);
    }

    /** The handle as a storage for whatever {@code type} is bound to; throws if it isn't bound. */
    @SuppressWarnings("unchecked")
    public static <U extends Unit<U>, V extends TransferVariant<?>> Storage<V> storage(TransferType<U> type, Handle<U> handle) {
        Binding<U, V> binding = (Binding<U, V>) binding(type);
        if (binding == null) {
            throw new IllegalArgumentException(type + " is not bound to a Fabric storage");
        }
        return new HandleStorage<>(handle, binding.codec());
    }

    @SuppressWarnings("unchecked")
    private static <U extends Unit<U>> @Nullable Binding<U, ?> binding(TransferType<U> type) {
        return (Binding<U, ?>) BINDINGS.get(type);
    }

    @SuppressWarnings("unchecked")
    private static <U extends Unit<U>, V extends TransferVariant<?>> Handle<U> wrap(Storage<V> storage, Codec<U, V> codec) {
        // Our own storage is a view over a handle already; go straight back to it.
        return storage instanceof HandleStorage<?, ?> ours ? (Handle<U>) ours.handle() : new StorageHandle<>(storage, codec);
    }

    private record Binding<U extends Unit<U>, V extends TransferVariant<?>>(
            BlockApiLookup<Storage<V>, Direction> lookup, Codec<U, V> codec) {
        private @Nullable Handle<U> find(Level level, BlockPos pos, @Nullable Direction side) {
            Storage<V> storage = lookup.find(level, pos, side);
            return storage == null ? null : wrap(storage, codec);
        }
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

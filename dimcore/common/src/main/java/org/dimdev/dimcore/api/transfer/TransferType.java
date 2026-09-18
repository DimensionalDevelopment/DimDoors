package org.dimdev.dimcore.api.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.dimdev.dimcore.DimCore;
import org.dimdev.dimcore.api.util.SimpleEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * One transferable resource kind and its two seams with the loader:
 * <ul>
 *   <li>{@link #lookup} — "what holds this resource at a block side?"</li>
 *   <li>{@link #expose} — "pipes touching this block entity should see a handler"</li>
 * </ul>
 * Listeners on both answer in registration order; the first non-null wins. {@link #find} falls
 * through to the loader's {@link TransferBridge} once every listener has passed, so mod listeners
 * always shadow the loader's capability / storage lookup regardless of registration order.
 * <p>
 * Mods add their own kind with {@link #create}. Fluid and item are bound to the loader's native
 * transfer API by DimCore; a mod type is bound by calling the loader bridge's {@code bind} from
 * that mod's loader entry, and is DimCore-internal (events only) without one. Either way, the
 * type's block entities are {@link #declare declared} during registration — NeoForge needs the
 * set up front to hang capabilities, Fabric answers through a fallback provider and ignores it.
 */
public final class TransferType<U extends Unit<U>> {
    private static final Map<ResourceLocation, TransferType<?>> TYPES = new LinkedHashMap<>();

    public static final TransferType<FluidUnit> FLUID = create(ResourceLocation.fromNamespaceAndPath("dimcore", "fluid"));
    public static final TransferType<ItemUnit> ITEM = create(ResourceLocation.fromNamespaceAndPath("dimcore", "item"));

    private final ResourceLocation id;
    private final Set<BlockEntityType<?>> declared = new LinkedHashSet<>();

    public final SimpleEvent<Lookup<U>> lookup = SimpleEvent.of(listeners -> (level, pos, side) -> {
        for (Lookup<U> listener : listeners) {
            Handle<U> handle = listener.find(level, pos, side);
            if (handle != null) {
                return handle;
            }
        }
        return null;
    });

    public final SimpleEvent<Expose<U>> expose = SimpleEvent.of(listeners -> (blockEntity, side) -> {
        for (Expose<U> listener : listeners) {
            Handle<U> handle = listener.expose(blockEntity, side);
            if (handle != null) {
                return handle;
            }
        }
        return null;
    });

    private TransferType(ResourceLocation id) {
        this.id = id;
    }

    public static synchronized <U extends Unit<U>> TransferType<U> create(ResourceLocation id) {
        if (TYPES.containsKey(id)) {
            throw new IllegalStateException("Transfer type already registered: " + id);
        }
        TransferType<U> type = new TransferType<>(id);
        TYPES.put(id, type);
        return type;
    }

    public static @Nullable TransferType<?> get(ResourceLocation id) {
        return TYPES.get(id);
    }

    public static Collection<TransferType<?>> all() {
        return Collections.unmodifiableCollection(TYPES.values());
    }

    public ResourceLocation id() {
        return id;
    }

    public @Nullable Handle<U> find(Level level, BlockPos pos, @Nullable Direction side) {
        Handle<U> handle = lookup.invoker().find(level, pos, side);
        return handle != null ? handle : DimCore.transfer().find(this, level, pos, side);
    }

    public @Nullable Handle<U> expose(BlockEntity blockEntity, @Nullable Direction side) {
        return expose.invoker().expose(blockEntity, side);
    }

    /** Opt a block entity type in to loader exposure; call during block entity registration. */
    public void declare(BlockEntityType<?> type) {
        declared.add(type);
    }

    public Set<BlockEntityType<?>> declared() {
        return Collections.unmodifiableSet(declared);
    }

    @Override
    public String toString() {
        return "TransferType[" + id + "]";
    }

    @FunctionalInterface
    public interface Lookup<U extends Unit<U>> {
        /** @param side the side of the block at {@code pos} being accessed, or null for any */
        @Nullable Handle<U> find(Level level, BlockPos pos, @Nullable Direction side);
    }

    @FunctionalInterface
    public interface Expose<U extends Unit<U>> {
        /** @param side the side of the block entity being accessed, or null for any */
        @Nullable Handle<U> expose(BlockEntity blockEntity, @Nullable Direction side);
    }
}

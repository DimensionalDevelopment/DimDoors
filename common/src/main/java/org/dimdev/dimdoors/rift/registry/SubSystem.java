package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.api.util.NbtUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class SubSystem<T extends SubSystem<T>> extends SavedData {
    public abstract Type<T> type();

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
        return NbtUtil.serialize(compoundTag, (T) this, type().codec());
    }

    public record Type<T extends SubSystem<T>>(String name, Supplier<T> constructor, MapCodec<T> codec) {}

    public static <T extends SubSystem<T>> T getInstance(Type<T> type) {
        return getInstance(DimensionalDoors.getServer(), type);
    }

    public static <T extends SubSystem<T>> T getInstance(MinecraftServer server, Type<T> type) {
        return server.overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                        type.constructor(),
                        (tag, provider) -> NbtUtil.deserialize(tag, type.codec()),
                        DataFixTypes.LEVEL
                ),
                type.name()
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List<SubSystem<?>> initialize(MinecraftServer server) {
        List<SubSystem<?>> subSystems = new ArrayList<>();
        for (Type<?> type : ModRegistries.SUBSYTEM_TYPE) {
            subSystems.add((SubSystem<?>) getInstance(server, (Type) type));
        }

        for (SubSystem<?> subSystem : subSystems) {
            if (subSystem instanceof RiftGraph graph) {
                graph.refreshVertices(subSystems);
            }
        }

        return subSystems;
    }
}

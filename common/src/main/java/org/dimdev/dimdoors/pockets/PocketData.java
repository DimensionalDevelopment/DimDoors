package org.dimdev.dimdoors.pockets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class PocketData extends SavedData {
    private static Factory<PocketData> FACTORY = new Factory<>(PocketData::new, (compoundTag, provider) -> {
        var ops = RegistryOps.create(NbtOps.INSTANCE, provider);

        return PocketAddon.LIST_CODEC.parse(ops, compoundTag.get("addons")).result().map(a -> new PocketData()).orElseGet(PocketData::new);
    },  DataFixTypes.LEVEL);

    private Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon> addons = new HashMap<>();

    private PocketData() {

    }

    private PocketData(List<PocketAddon> addons) {
        for(var addon : addons) {
            if(!this.addons.containsKey(addon.getType())) {
                this.addons.put(addon.getType(), addon);
            }
        }
    }

    public static PocketData get(ServerLevel world) {
        return world.getDataStorage().get(FACTORY, "pocket_data");
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        var ops = RegistryOps.create(NbtOps.INSTANCE, provider);

        PocketAddon.LIST_CODEC.encodeStart(ops, addons.values().stream().toList()).result().ifPresent(tag -> compoundTag.put("addons", tag));

        return compoundTag;
    }

    public static PocketData getData(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, "pocket_data");
    }

    public <T extends PocketAddon> Optional<T> getAddon(PocketAddon.PocketAddonType<T, ?> type) {
        return Optional.ofNullable(addons.get(type)).filter(a -> a.getType().equals(type)).map(a -> (T) a);
    }
}

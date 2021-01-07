package org.dimdev.dimdoors.world.pocket;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.util.NbtUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import static net.minecraft.world.World.OVERWORLD;

public class PrivatePocketData extends PersistentState {
    protected static class PocketInfo {
        public final RegistryKey<World> world;
        public final int id;

        public PocketInfo(RegistryKey<World> world, int id) {
            this.world = world;
            this.id = id;
        }

        public static CompoundTag toTag(PocketInfo info) {
            CompoundTag tag = new CompoundTag();
            tag.putString("world", info.world.getValue().toString());
            tag.putInt("id", info.id);
            return tag;
        }

        public static PocketInfo fromTag(CompoundTag tag) {
            return new PocketInfo(
                    RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world"))),
                    tag.getInt("id")
            );
        }
    }

    private static final String DATA_NAME = "dimdoors_private_pockets";

    protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

    public PrivatePocketData(String name) {
        super(name);
    }

    public PrivatePocketData() {
        super(DATA_NAME);
    }

    public static PrivatePocketData instance() {
        return DimensionalDoorsInitializer.getWorld(OVERWORLD).getPersistentStateManager().getOrCreate(PrivatePocketData::new, DATA_NAME);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        CompoundTag tag = nbt.getCompound("privatePocketMap");

        HashBiMap<UUID, PocketInfo> bm = HashBiMap.create();
        for (String t : tag.getKeys()) {
            bm.put(UUID.fromString(t), PocketInfo.fromTag(tag.getCompound(t)));
        }
        this.privatePocketMap = bm;
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<UUID, PocketInfo> entry : privatePocketMap.entrySet()) {
            tag.put(entry.getKey().toString(), PocketInfo.toTag(entry.getValue()));
        }
        nbt.put("privatePocketMap", tag);

        return nbt;
    }

    public Pocket getPrivatePocket(UUID playerUUID) {
        PocketInfo pocket = this.privatePocketMap.get(playerUUID);
        if (pocket == null) return null;
        return PocketRegistry.getInstance(pocket.world).getPocket(pocket.id);
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
        this.privatePocketMap.put(playerUUID, new PocketInfo(pocket.world, pocket.id));
        this.markDirty();
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        return this.privatePocketMap.inverse().get(new PocketInfo(pocket.world, pocket.id));
    }
}

package org.dimdev.dimdoors.world.pocket;

import java.util.UUID;

import org.dimdev.dimdoors.DimensionalDoorsInitializer;
import org.dimdev.dimdoors.util.NbtUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import static net.minecraft.world.World.OVERWORLD;

public class PrivatePocketData extends PersistentState {
    protected static class PocketInfo {
        public static final Codec<PocketInfo> CODEC = RecordCodecBuilder.create(instance -> {
            return instance.group(
                    World.CODEC.fieldOf("world").forGetter(a -> a.world),
                    Codec.INT.fieldOf("id").forGetter(a -> a.id)
            ).apply(instance, PocketInfo::new);
        });

        public final RegistryKey<World> world;
        public final int id;

        public PocketInfo(RegistryKey<World> world, int id) {
            this.world = world;
            this.id = id;
        }
    }

    public static final Codec<BiMap<UUID, PocketInfo>> CODEC = Codec.unboundedMap(DynamicSerializableUuid.field_25122, PocketInfo.CODEC).xmap(HashBiMap::create, a -> a);

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
        privatePocketMap = NbtUtil.deserialize(nbt.get("privatePocketMap"), CODEC);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        nbt.put("privatePocketMap", NbtUtil.serialize(privatePocketMap, CODEC));
        return nbt;
    }

    public Pocket getPrivatePocket(UUID playerUUID) {
        PocketInfo pocket = privatePocketMap.get(playerUUID);
        if (pocket == null) return null;
        return PocketRegistry.instance(pocket.world).getPocket(pocket.id);
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
        privatePocketMap.put(playerUUID, new PocketInfo(pocket.world, pocket.id));
        markDirty();
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        return privatePocketMap.inverse().get(new PocketInfo(pocket.world, pocket.id));
    }
}

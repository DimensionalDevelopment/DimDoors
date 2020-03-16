package org.dimdev.pocketlib;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.dimdev.annotatednbt.AnnotatedNbt;
import org.dimdev.annotatednbt.Saved;
import org.dimdev.util.WorldUtils;

import java.util.UUID;

public class PrivatePocketData extends PersistentState {
    protected static class PocketInfo {
        @Saved public final ServerWorld world;
        @Saved public final int id;

        public PocketInfo(ServerWorld world, int id) {
            this.world = world;
            this.id = id;
        }
    }

    private static final String DATA_NAME = "dimdoors_private_pockets";
    @Saved protected BiMap<String, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

    public PrivatePocketData(String name) {
        super(name);
    }

    public PrivatePocketData() {
        super(DATA_NAME);
    }

    public static PrivatePocketData instance() {
        return WorldUtils.getWorld(DimensionType.OVERWORLD).getPersistentStateManager().get(PrivatePocketData::new, DATA_NAME);
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        AnnotatedNbt.save(this, nbt);
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {
        AnnotatedNbt.load(this, nbt);
        return nbt;
    }

    public Pocket getPrivatePocket(UUID playerUUID) {
        PocketInfo pocket = privatePocketMap.get(playerUUID.toString());
        if (pocket == null) return null;
        return PocketRegistry.instance(pocket.world).getPocket(pocket.id);
    }

    public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
        privatePocketMap.put(playerUUID.toString(), new PocketInfo(pocket.world, pocket.id));
        markDirty();
    }

    public UUID getPrivatePocketOwner(Pocket pocket) {
        return UUID.fromString(privatePocketMap.inverse().get(new PocketInfo(pocket.world, pocket.id)));
    }
}

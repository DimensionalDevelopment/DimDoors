package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;
import org.dimdev.dimdoors.world.pocket.type.Pocket;
import org.dimdev.dimdoors.world.pocket.type.PrivatePocket;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PrivateRegistry {
    protected record PocketInfo(ResourceKey<Level> world, int id) {

        public static CompoundTag toNbt(PocketInfo info) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("world", info.world.location().toString());
            nbt.putInt("id", info.id);
            return nbt;
        }

        public static PocketInfo fromNbt(CompoundTag nbt) {
            return new PocketInfo(
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(nbt.getString("world"))),
                    nbt.getInt("id")
            );
        }
    }

	private static final String DATA_NAME = "dimdoors_private_pockets";

	protected BiMap<UUID, PocketInfo> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket Info TODO: fix AnnotatedNBT and use UUID rather than String

	public PrivateRegistry() {
	}

	public void fromNbt(CompoundTag nbt) {
		privatePocketMap.clear();
		CompoundTag privatePocketMapNbt = nbt.getCompound("private_pocket_map");

        for (var key : privatePocketMapNbt.getAllKeys()) {
            CompoundTag pocketInfoNbt = privatePocketMapNbt.getCompound(key);
            var uuidKey = UUID.fromString(key);
            var pocketInfo = PocketInfo.fromNbt(pocketInfoNbt);

            this.privatePocketMap.put(uuidKey, pocketInfo);
        }
	}

	public CompoundTag toNbt(CompoundTag nbt) {
        var pocketMapNbt = new CompoundTag();

        for(var pair : this.privatePocketMap.entrySet()) {
            pocketMapNbt.put(pair.getKey().toString(), PocketInfo.toNbt(pair.getValue()));
        }

		nbt.put("private_pocket_map", pocketMapNbt);

		return nbt;
	}

	public PrivatePocket getPrivatePocket(UUID playerUUID) {
		PocketInfo pocket = this.privatePocketMap.get(playerUUID);
		if (pocket == null) return null;
		return DimensionalRegistry.getPocketDirectory(pocket.world).getPocket(pocket.id, PrivatePocket.class);
	}

	public void setPrivatePocketID(UUID playerUUID, Pocket pocket) {
		this.privatePocketMap.put(playerUUID, new PocketInfo(pocket.getWorld(), pocket.getId()));
        DimensionalRegistry.setDirty();
    }

	public boolean removePrivatePocket(Pocket pocket) {
		return removePrivatePocket(pocket.getWorld(), pocket.getId());
	}

	public boolean removePrivatePocket(ResourceKey<Level> world, int id) {
		UUID removedOwner = this.privatePocketMap.inverse().remove(new PocketInfo(world, id));
		if (removedOwner != null) {
			DimensionalRegistry.setDirty();
			return true;
		}
		return false;
	}

	public UUID getPrivatePocketOwner(Pocket pocket) {
		return this.privatePocketMap.inverse().get(new PocketInfo(pocket.getWorld(), pocket.getId()));
	}
}

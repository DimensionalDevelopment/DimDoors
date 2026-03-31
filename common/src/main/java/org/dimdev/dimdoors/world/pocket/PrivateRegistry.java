package org.dimdev.dimdoors.world.pocket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import org.dimdev.dimdoors.world.level.registry.DimensionalRegistry;

import java.util.UUID;
import java.util.function.Function;

public class PrivateRegistry {
    public static final Codec<PrivateRegistry> CODEC = Codec.unboundedMap(UUIDUtil.STRING_CODEC, UUIDUtil.CODEC)
            .xmap(HashBiMap::create, Function.identity())
            .xmap(PrivateRegistry::new, privateRegistry -> (HashBiMap<UUID, UUID>) privateRegistry.privatePocketMap);

	private BiMap<UUID, UUID> privatePocketMap = HashBiMap.create(); // Player UUID -> Pocket UUID

    public PrivateRegistry() {
        this(HashBiMap.create());
    }

	public PrivateRegistry(BiMap<UUID, UUID> privatePocketMap) {
        this.privatePocketMap = privatePocketMap;
	}

    public UUID
    getPrivatePocket(UUID playerUUID) {
		var pocket = this.privatePocketMap.get(playerUUID);
		if (pocket == null) return null;
		return DimensionalRegistry.getPocketDirectory().get(pocket);
	}

	public void setPrivatePocketID(UUID playerUUID, UUID pocket) {
		this.privatePocketMap.put(playerUUID, pocket);
        DimensionalRegistry.setDirty();
    }

	public UUID getPrivatePocketOwner(UUID pocket) {
		return this.privatePocketMap.inverse().get(pocket);
	}
}
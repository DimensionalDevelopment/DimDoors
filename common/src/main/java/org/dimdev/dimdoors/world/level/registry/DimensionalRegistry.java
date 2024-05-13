package org.dimdev.dimdoors.world.level.registry;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.api.util.StreamUtils;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DimensionalRegistry {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private static Map<ResourceKey<Level>, PocketDirectory> pocketRegistry = new HashMap<>();
	private static RiftRegistry riftRegistry = new RiftRegistry();
	private static PrivateRegistry privateRegistry = new PrivateRegistry();

	public static void readFromNbt(CompoundTag nbt) {
		int riftDataVersion = nbt.getInt("RiftDataVersion");
		if (riftDataVersion < RIFT_DATA_VERSION) {
			nbt = RiftSchemas.update(riftDataVersion, nbt);
		} else if (RIFT_DATA_VERSION < riftDataVersion) {
			throw new UnsupportedOperationException("Downgrading is not supported!");
		}

		CompoundTag pocketRegistryNbt = nbt.getCompound("pocket_registry");
		CompletableFuture<Map<ResourceKey<Level>, PocketDirectory>> futurePocketRegistry = CompletableFuture.supplyAsync(() -> pocketRegistryNbt.getAllKeys().stream().map(key -> {
					CompoundTag pocketDirectoryNbt = pocketRegistryNbt.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(ResourceKey.create(Registries.DIMENSION, ResourceLocation.tryParse(key)), PocketDirectory.readFromNbt(key, pocketDirectoryNbt)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)));

		CompoundTag privateRegistryNbt = nbt.getCompound("private_registry");
		CompletableFuture<PrivateRegistry> futurePrivateRegistry = CompletableFuture.supplyAsync(() -> {
			PrivateRegistry privateRegistry = new PrivateRegistry();
			privateRegistry.fromNbt(privateRegistryNbt);
			return privateRegistry;
		});

		pocketRegistry = futurePocketRegistry.join();

		CompoundTag riftRegistryNbt = nbt.getCompound("rift_registry");
		CompletableFuture<RiftRegistry> futureRiftRegistry = CompletableFuture.supplyAsync(() -> RiftRegistry.fromNbt(pocketRegistry, riftRegistryNbt));
		riftRegistry = futureRiftRegistry.join();

		privateRegistry = futurePrivateRegistry.join();
	}

	public static void writeToNbt(CompoundTag nbt) {
		CompletableFuture<Tag> futurePocketRegistryNbt = StreamUtils.supplyAsync(() -> {
			List<CompletableFuture<Pair<String, Tag>>> futurePocketRegistryNbts = new ArrayList<>();
			pocketRegistry.forEach((key, value) -> futurePocketRegistryNbts.add(CompletableFuture.supplyAsync(() -> new Pair<>(key.location().toString(), value.writeToNbt()))));
			CompoundTag pocketRegistryNbt = new CompoundTag();
			futurePocketRegistryNbts.parallelStream().unordered().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)).forEach(pocketRegistryNbt::put);
			return pocketRegistryNbt;
		});

		CompletableFuture<Tag> futureRiftRegistryNbt = StreamUtils.supplyAsync(riftRegistry::toNbt);
		CompletableFuture<Tag> futurePrivateRegistryNbt = CompletableFuture.supplyAsync(() -> privateRegistry.toNbt(new CompoundTag()));

		nbt.put("pocket_registry", futurePocketRegistryNbt.join());
		nbt.put("rift_registry", futureRiftRegistryNbt.join());
		nbt.put("private_registry", futurePrivateRegistryNbt.join());

		nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
	}

	public static RiftRegistry getRiftRegistry() {
		return riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return privateRegistry;
	}

	public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
		if (!(ModDimensions.isPocketDimension(key))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		return pocketRegistry.computeIfAbsent(key, PocketDirectory::new);
	}

	public static boolean isValidWorld(Level level) {
		 return level != null && level.dimension() != null && level.dimension().equals(Level.OVERWORLD);
	}
}

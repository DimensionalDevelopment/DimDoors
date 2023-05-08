package org.dimdev.dimdoors.world.level.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelProperties;

import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import static org.dimdev.dimdoors.DimensionalDoors.getServer;

public class DimensionalRegistry implements ComponentV3 {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private Map<ResourceKey<Level>, PocketDirectory> pocketRegistry = new HashMap<>();
	private RiftRegistry riftRegistry = new RiftRegistry();
	private PrivateRegistry privateRegistry = new PrivateRegistry();

	@Override
	public void readFromNbt(CompoundTag nbt) {
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

		this.privateRegistry = futurePrivateRegistry.join();
	}

	@Override
	public void writeToNbt(CompoundTag nbt) {
		CompletableFuture<Tag> futurePocketRegistryNbt = CompletableFuture.supplyAsync(() -> {
			List<CompletableFuture<Pair<String, Tag>>> futurePocketRegistryNbts = new ArrayList<>();
			pocketRegistry.forEach((key, value) -> futurePocketRegistryNbts.add(CompletableFuture.supplyAsync(() -> new Pair<>(key.location().toString(), value.writeToNbt()))));
			CompoundTag pocketRegistryNbt = new CompoundTag();
			futurePocketRegistryNbts.parallelStream().unordered().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getFirst, Pair::getSecond)).forEach(pocketRegistryNbt::put);
			return pocketRegistryNbt;
		});

		CompletableFuture<Tag> futureRiftRegistryNbt = CompletableFuture.supplyAsync(riftRegistry::toNbt);
		CompletableFuture<Tag> futurePrivateRegistryNbt = CompletableFuture.supplyAsync(() -> privateRegistry.toNbt(new CompoundTag()));

		nbt.put("pocket_registry", futurePocketRegistryNbt.join());
		nbt.put("rift_registry", futureRiftRegistryNbt.join());
		nbt.put("private_registry", futurePrivateRegistryNbt.join());

		nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
	}

	public static DimensionalRegistry instance() {
		return DimensionalDoorsComponents.DIMENSIONAL_REGISTRY_COMPONENT_KEY.get(((DedicatedServer) getServer()).getProperties());
	}

	public static RiftRegistry getRiftRegistry() {
		return instance().riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return instance().privateRegistry;
	}

	public static PocketDirectory getPocketDirectory(ResourceKey<Level> key) {
		if (!(ModDimensions.isPocketDimension(key))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		return instance().pocketRegistry.computeIfAbsent(key, PocketDirectory::new);
	}
}

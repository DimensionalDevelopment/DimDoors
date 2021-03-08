package org.dimdev.dimdoors.world.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import static org.dimdev.dimdoors.DimensionalDoorsInitializer.getServer;

public class DimensionalRegistry implements ComponentV3 {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time
	private Map<RegistryKey<World>, PocketDirectory> pocketRegistry = new HashMap<>();
	private RiftRegistry riftRegistry = new RiftRegistry();
	private PrivateRegistry privateRegistry = new PrivateRegistry();

	@Override
	public void readFromNbt(CompoundTag tag) {
		int riftDataVersion = tag.getInt("RiftDataVersion");
		if (riftDataVersion < RIFT_DATA_VERSION) {
			tag = RiftSchemas.update(riftDataVersion, tag);
		} else if (RIFT_DATA_VERSION < riftDataVersion) {
			throw new UnsupportedOperationException("Downgrading is not supported!");
		}

		CompoundTag pocketRegistryTag = tag.getCompound("pocket_registry");
		CompletableFuture<Map<RegistryKey<World>, PocketDirectory>> futurePocketRegistry = CompletableFuture.supplyAsync(() -> pocketRegistryTag.getKeys().stream().map(key -> {
					CompoundTag pocketDirectoryTag = pocketRegistryTag.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(RegistryKey.of(Registry.DIMENSION, Identifier.tryParse(key)), PocketDirectory.readFromNbt(key, pocketDirectoryTag)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)));

		CompoundTag privateRegistryTag = tag.getCompound("private_registry");
		CompletableFuture<PrivateRegistry> futurePrivateRegistry = CompletableFuture.supplyAsync(() -> {
			PrivateRegistry privateRegistry = new PrivateRegistry();
			privateRegistry.fromTag(privateRegistryTag);
			return privateRegistry;
		});

		pocketRegistry = futurePocketRegistry.join();

		CompoundTag riftRegistryTag = tag.getCompound("rift_registry");
		CompletableFuture<RiftRegistry> futureRiftRegistry = CompletableFuture.supplyAsync(() -> RiftRegistry.fromTag(pocketRegistry, riftRegistryTag));
		riftRegistry = futureRiftRegistry.join();

		this.privateRegistry = futurePrivateRegistry.join();
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		CompletableFuture<Tag> futurePocketRegistryTag = CompletableFuture.supplyAsync(() -> {
			List<CompletableFuture<Pair<String, Tag>>> futurePocketRegistryTags = new ArrayList<>();
			pocketRegistry.forEach((key, value) -> futurePocketRegistryTags.add(CompletableFuture.supplyAsync(() -> new Pair<>(key.getValue().toString(), value.writeToNbt()))));
			CompoundTag pocketRegistryTag = new CompoundTag();
			futurePocketRegistryTags.parallelStream().unordered().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)).forEach(pocketRegistryTag::put);
			return pocketRegistryTag;
		});

		CompletableFuture<Tag> futureRiftRegistryTag = CompletableFuture.supplyAsync(riftRegistry::toTag);
		CompletableFuture<Tag> futurePrivateRegistryTag = CompletableFuture.supplyAsync(() -> privateRegistry.toTag(new CompoundTag()));

		tag.put("pocket_registry", futurePocketRegistryTag.join());
		tag.put("rift_registry", futureRiftRegistryTag.join());
		tag.put("private_registry", futurePrivateRegistryTag.join());

		tag.putInt("RiftDataVersion", RIFT_DATA_VERSION);
	}

	public static DimensionalRegistry instance() {
		return DimensionalDoorsComponents.DIMENSIONAL_REGISTRY_COMPONENT_KEY.get((LevelProperties) getServer().getSaveProperties());
	}

	public static RiftRegistry getRiftRegistry() {
		return instance().riftRegistry;
	}

	public static PrivateRegistry getPrivateRegistry() {
		return instance().privateRegistry;
	}

	public static PocketDirectory getPocketDirectory(RegistryKey<World> key) {
		if (!(ModDimensions.isPocketDimension(key))) {
			throw new UnsupportedOperationException("PocketRegistry is only available for pocket dimensions!");
		}

		return instance().pocketRegistry.computeIfAbsent(key, PocketDirectory::new);
	}
}

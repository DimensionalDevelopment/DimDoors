package org.dimdev.dimdoors.world.level.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.DimensionalDoorsComponents;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;
import org.dimdev.dimdoors.world.pocket.PrivateRegistry;

import static org.dimdev.dimdoors.DimensionalDoorsInitializer.getServer;

public class DimensionalRegistry implements ComponentV3 {
	public static final int RIFT_DATA_VERSION = 1; // Increment this number every time a new schema is added
	private Map<RegistryKey<World>, PocketDirectory> pocketRegistry = new HashMap<>();
	private RiftRegistry riftRegistry = new RiftRegistry();
	private PrivateRegistry privateRegistry = new PrivateRegistry();

	@Override
	public void readFromNbt(NbtCompound nbt) {
		int riftDataVersion = nbt.getInt("RiftDataVersion");
		if (riftDataVersion < RIFT_DATA_VERSION) {
			nbt = RiftSchemas.update(riftDataVersion, nbt);
		} else if (RIFT_DATA_VERSION < riftDataVersion) {
			throw new UnsupportedOperationException("Downgrading is not supported!");
		}

		NbtCompound pocketRegistryNbt = nbt.getCompound("pocket_registry");
		CompletableFuture<Map<RegistryKey<World>, PocketDirectory>> futurePocketRegistry = CompletableFuture.supplyAsync(() -> pocketRegistryNbt.getKeys().stream().map(key -> {
					NbtCompound pocketDirectoryNbt = pocketRegistryNbt.getCompound(key);
					return CompletableFuture.supplyAsync(() -> new Pair<>(RegistryKey.of(Registry.WORLD_KEY, Identifier.tryParse(key)), PocketDirectory.readFromNbt(key, pocketDirectoryNbt)));
				}).parallel().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)));

		NbtCompound privateRegistryNbt = nbt.getCompound("private_registry");
		CompletableFuture<PrivateRegistry> futurePrivateRegistry = CompletableFuture.supplyAsync(() -> {
			PrivateRegistry privateRegistry = new PrivateRegistry();
			privateRegistry.fromNbt(privateRegistryNbt);
			return privateRegistry;
		});

		pocketRegistry = futurePocketRegistry.join();

		NbtCompound riftRegistryNbt = nbt.getCompound("rift_registry");
		CompletableFuture<RiftRegistry> futureRiftRegistry = CompletableFuture.supplyAsync(() -> RiftRegistry.fromNbt(pocketRegistry, riftRegistryNbt));
		riftRegistry = futureRiftRegistry.join();

		this.privateRegistry = futurePrivateRegistry.join();
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		CompletableFuture<NbtElement> futurePocketRegistryNbt = CompletableFuture.supplyAsync(() -> {
			List<CompletableFuture<Pair<String, NbtElement>>> futurePocketRegistryNbts = new ArrayList<>();
			pocketRegistry.forEach((key, value) -> futurePocketRegistryNbts.add(CompletableFuture.supplyAsync(() -> new Pair<>(key.getValue().toString(), value.writeToNbt()))));
			NbtCompound pocketRegistryNbt = new NbtCompound();
			futurePocketRegistryNbts.parallelStream().unordered().map(CompletableFuture::join).collect(Collectors.toConcurrentMap(Pair::getLeft, Pair::getRight)).forEach(pocketRegistryNbt::put);
			return pocketRegistryNbt;
		});

		CompletableFuture<NbtElement> futureRiftRegistryNbt = CompletableFuture.supplyAsync(riftRegistry::toNbt);
		CompletableFuture<NbtElement> futurePrivateRegistryNbt = CompletableFuture.supplyAsync(() -> privateRegistry.toNbt(new NbtCompound()));

		nbt.put("pocket_registry", futurePocketRegistryNbt.join());
		nbt.put("rift_registry", futureRiftRegistryNbt.join());
		nbt.put("private_registry", futurePrivateRegistryNbt.join());

		nbt.putInt("RiftDataVersion", RIFT_DATA_VERSION);
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

package org.dimdev.dimdoors.world.level;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
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
	public Map<RegistryKey<World>, PocketDirectory> pocketRegistry = new HashMap<>();
	RiftRegistry riftRegistry = new RiftRegistry();
	PrivateRegistry privateRegistry = new PrivateRegistry();

	@Override
	public void readFromNbt(CompoundTag tag) {
		CompoundTag pocketRegistryTag = tag.getCompound("pocketRegistry");
		pocketRegistry = pocketRegistryTag.getKeys().stream().collect(Collectors.toMap(a -> RegistryKey.of(Registry.DIMENSION, new Identifier(a)), a -> PocketDirectory.readFromNbt(a, pocketRegistryTag.getCompound(a))));
		riftRegistry = RiftRegistry.fromTag(pocketRegistry, tag.getCompound("riftRegistry"));
		privateRegistry.fromTag(tag);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		CompoundTag pocketRegistryTag = new CompoundTag();
		pocketRegistry.forEach((key, value) -> pocketRegistryTag.put(key.getValue().toString(), value.writeToNbt()));

		tag.put("pocketRegistry", pocketRegistryTag);
		tag.put("riftRegistry", riftRegistry.toTag());
		privateRegistry.toTag(tag);
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

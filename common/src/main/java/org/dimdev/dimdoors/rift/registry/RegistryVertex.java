package org.dimdev.dimdoors.rift.registry;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.serialization.Lifecycle;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

import org.dimdev.dimdoors.DimensionalDoors;

public abstract class RegistryVertex {
	public static final Registry<RegistryVertexType<?>> registry = FabricRegistryBuilder.from(new SimpleRegistry<RegistryVertexType<? extends RegistryVertex>>(RegistryKey.ofRegistry(DimensionalDoors.id("registry_vertex")), Lifecycle.stable())).buildAndRegister();

	private RegistryKey<World> world; // The dimension to store this object in. Links are stored in both registries.

	protected UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

	public void sourceGone(RegistryVertex source) {
	}

	public void targetGone(RegistryVertex target) {
	}

	public void sourceAdded(RegistryVertex source) {
	}

	public void targetAdded(RegistryVertex target) {
	}

	public abstract RegistryVertexType<? extends RegistryVertex> getType();

	public String toString() {
		return "RegistryVertex(dim=" + this.world + ", id=" + this.id + ")";
	}

	public static RegistryVertex fromNbt(NbtCompound nbt) {
		return Objects.requireNonNull(registry.get(new Identifier(nbt.getString("type")))).fromNbt(nbt);
	}

	public static NbtCompound toNbt(RegistryVertex registryVertex) {
		String type = registry.getId(registryVertex.getType()).toString();

		NbtCompound nbt = registryVertex.getType().toNbt(registryVertex);
		nbt.putString("type", type);

		return nbt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	protected RegistryKey<World> getWorld() {
		return world;
	}

	protected void setWorld(RegistryKey<World> world) {
		this.world = world;
	}

	public interface RegistryVertexType<T extends RegistryVertex> {
		RegistryVertexType<PlayerRiftPointer> PLAYER = register("player", PlayerRiftPointer::fromNbt, PlayerRiftPointer::toNbt);
		RegistryVertexType<Rift> RIFT = register("rift", Rift::fromNbt, Rift::toNbt);
		RegistryVertexType<PocketEntrancePointer> ENTRANCE = register("entrance", PocketEntrancePointer::fromNbt, PocketEntrancePointer::toNbt);
		RegistryVertexType<RiftPlaceholder> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder::fromNbt, RiftPlaceholder::toNbt);

		static void register() {
		}

		T fromNbt(NbtCompound nbt);

		NbtCompound toNbt(RegistryVertex virtualType);

		static <T extends RegistryVertex> RegistryVertexType<T> register(String id, Function<NbtCompound, T> fromNbt, Function<T, NbtCompound> toNbt) {
			return Registry.register(registry, id, new RegistryVertexType<T>() {
				@Override
				public T fromNbt(NbtCompound nbt) {
					return fromNbt.apply(nbt);
				}

				@Override
				public NbtCompound toNbt(RegistryVertex registryVertex) {
					return toNbt.apply((T) registryVertex);
				}
			});
		}
	}
}

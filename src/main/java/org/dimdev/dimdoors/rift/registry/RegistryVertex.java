package org.dimdev.dimdoors.rift.registry;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;

public abstract class RegistryVertex {
	public static final Registry<RegistryVertexType<?>> registry = FabricRegistryBuilder.from(new MappedRegistry<RegistryVertexType<? extends RegistryVertex>>(ResourceKey.createRegistryKey(DimensionalDoors.resource("registry_vertex")), Lifecycle.stable())).buildAndRegister();

	private ResourceKey<Level> world; // The dimension to store this object in. Links are stored in both registries.

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

	public static RegistryVertex fromNbt(CompoundTag nbt) {
		return Objects.requireNonNull(registry.get(new ResourceLocation(nbt.getString("type")))).fromNbt(nbt);
	}

	public static CompoundTag toNbt(RegistryVertex registryVertex) {
		String type = registry.getKey(registryVertex.getType()).toString();

		CompoundTag nbt = registryVertex.getType().toNbt(registryVertex);
		nbt.putString("type", type);

		return nbt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	protected ResourceKey<Level> getWorld() {
		return world;
	}

	protected void setWorld(ResourceKey<Level> world) {
		this.world = world;
	}

	public interface RegistryVertexType<T extends RegistryVertex> {
		RegistryVertexType<PlayerRiftPointer> PLAYER = register("player", PlayerRiftPointer::fromNbt, PlayerRiftPointer::toNbt);
		RegistryVertexType<Rift> RIFT = register("rift", Rift::fromNbt, Rift::toNbt);
		RegistryVertexType<PocketEntrancePointer> ENTRANCE = register("entrance", PocketEntrancePointer::fromNbt, PocketEntrancePointer::toNbt);
		RegistryVertexType<RiftPlaceholder> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder::fromNbt, RiftPlaceholder::toNbt);

		static void register() {
		}

		T fromNbt(CompoundTag nbt);

		CompoundTag toNbt(RegistryVertex virtualType);

		static <T extends RegistryVertex> RegistryVertex.RegistryVertexType<T> register(String id, Function<CompoundTag, T> fromNbt, Function<T, CompoundTag> toNbt) {
			return Registry.register(registry, id, new RegistryVertexType<T>() {
				@Override
				public T fromNbt(CompoundTag nbt) {
					return fromNbt.apply(nbt);
				}

				@Override
				public CompoundTag toNbt(RegistryVertex registryVertex) {
					return toNbt.apply((T) registryVertex);
				}
			});
		}
	}
}

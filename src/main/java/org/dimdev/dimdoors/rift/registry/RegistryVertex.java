package org.dimdev.dimdoors.rift.registry;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;

public abstract class RegistryVertex {
	public static final Registry<RegistryVertexType> registry = FabricRegistryBuilder.createSimple(RegistryVertex.RegistryVertexType.class, new Identifier("dimdoors", "registry_vertex")).attribute(RegistryAttribute.MODDED).buildAndRegister();

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

	public static RegistryVertex fromTag(NbtCompound nbt) {
		return Objects.requireNonNull(registry.get(new Identifier(nbt.getString("type")))).fromTag(nbt);
	}

	public static NbtCompound toTag(RegistryVertex registryVertex) {
		String type = registry.getId(registryVertex.getType()).toString();

		NbtCompound tag = registryVertex.getType().toTag(registryVertex);
		tag.putString("type", type);

		return tag;
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
		RegistryVertexType<PlayerRiftPointer> PLAYER = register("player", PlayerRiftPointer::fromTag, PlayerRiftPointer::toTag);
		RegistryVertexType<Rift> RIFT = register("rift", Rift::fromTag, Rift::toTag);
		RegistryVertexType<PocketEntrancePointer> ENTRANCE = register("entrance", PocketEntrancePointer::fromTag, PocketEntrancePointer::toTag);
		RegistryVertexType<RiftPlaceholder> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder::fromTag, RiftPlaceholder::toTag);

		T fromTag(NbtCompound tag);

		NbtCompound toTag(RegistryVertex virtualType);

		static <T extends RegistryVertex> RegistryVertex.RegistryVertexType<T> register(String id, Function<NbtCompound, T> fromTag, Function<T, NbtCompound> toTag) {
			return Registry.register(registry, id, new RegistryVertexType<T>() {
				@Override
				public T fromTag(NbtCompound tag) {
					return fromTag.apply(tag);
				}

				@Override
				public NbtCompound toTag(RegistryVertex registryVertex) {
					return toTag.apply((T) registryVertex);
				}
			});
		}
	}
}

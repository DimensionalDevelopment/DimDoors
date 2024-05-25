package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.CodecUtil;
import org.dimdev.dimdoors.api.util.NbtUtil;

import java.util.UUID;
import java.util.function.Function;

public abstract class RegistryVertex {
	public static final Registrar<RegistryVertexType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<RegistryVertexType<? extends RegistryVertex>>builder(DimensionalDoors.id("registry_vertex")).build();
	public static final Codec<RegistryVertex> CODEC = CodecUtil.registrarCodec(REGISTRY, RegistryVertex::getType, RegistryVertexType::mapCodec);

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
		return NbtUtil.deserialize(nbt, CODEC);
	}

	public static CompoundTag toNbt(RegistryVertex registryVertex) {
		String type = REGISTRY.getId(registryVertex.getType()).toString();

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
		RegistrySupplier<RegistryVertexType<PlayerRiftPointer>> PLAYER = register("player", PlayerRiftPointer::fromNbt, PlayerRiftPointer::toNbt, PlayerRiftPointer.CODEC);
		RegistrySupplier<RegistryVertexType<Rift>> RIFT = register("rift", Rift::fromNbt, Rift::toNbt, Rift.CODEC);
		RegistrySupplier<RegistryVertexType<PocketEntrancePointer>> ENTRANCE = register("entrance", PocketEntrancePointer::fromNbt, PocketEntrancePointer::toNbt, PocketEntrancePointer.CODEC);
		RegistrySupplier<RegistryVertexType<RiftPlaceholder>> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder::fromNbt, RiftPlaceholder::toNbt, RiftPlaceholder.CODEC);

		static void register() {
		}

		public MapCodec<T> mapCodec();

		T fromNbt(CompoundTag nbt);

		CompoundTag toNbt(RegistryVertex virtualType);

		static <T extends RegistryVertex> RegistrySupplier<RegistryVertexType<T>> register(String id, Function<CompoundTag, T> fromNbt, Function<T, CompoundTag> toNbt, MapCodec<T> mapCodec) {
			return REGISTRY.register(DimensionalDoors.id(id), () -> new RegistryVertexType<T>() {
				@Override
				public MapCodec<T> mapCodec() {
					return mapCodec;
				}

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

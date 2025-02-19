package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.UUID;

public abstract class RegistryVertex {
	public static final Registrar<RegistryVertexType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<RegistryVertexType<? extends RegistryVertex>>builder(DimensionalDoors.id("registry_vertex")).build();
	public static final Codec<RegistryVertex> CODEC = ResourceLocation.CODEC.<RegistryVertexType<?>>xmap(REGISTRY::get, REGISTRY::getId).dispatch(RegistryVertex::getType, RegistryVertexType::mapCodec);


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

	public record RegistryVertexType<T extends RegistryVertex>(MapCodec<T> mapCodec) {
		public static final RegistrySupplier<RegistryVertexType<PlayerRiftPointer>> PLAYER = register("player", PlayerRiftPointer.CODEC);
		public static final RegistrySupplier<RegistryVertexType<Rift>> RIFT = register("rift", Rift.CODEC);
		public static final RegistrySupplier<RegistryVertexType<PocketEntrancePointer>> ENTRANCE = register("entrance", PocketEntrancePointer.CODEC);
		public static final RegistrySupplier<RegistryVertexType<RiftPlaceholder>> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder.CODEC);

		public static void register() {
		}

		static <T extends RegistryVertex> RegistrySupplier<RegistryVertexType<T>> register(String id, MapCodec<T> mapCodec) {
			return REGISTRY.register(DimensionalDoors.id(id), () -> new RegistryVertexType<T>(mapCodec));
		}
	}
}

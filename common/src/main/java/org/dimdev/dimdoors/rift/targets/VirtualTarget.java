package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {
	public static final Codec<VirtualTarget> CODEC = VirtualTargetType.CODEC.dispatch("type", VirtualTarget::getType, VirtualTargetType::codec);
	public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

	protected Location location;

	public static VirtualTarget fromNbt(CompoundTag nbt) {
		return CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();

//		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
//		return Objects.requireNonNull(REGISTRY.get(id), "Unknown virtual target type " + id).fromNbt(nbt);
	}

	public static <T extends VirtualTarget> CompoundTag toNbt(T virtualTarget) {
		var data = (CompoundTag) virtualTarget.getType().codec().codec().encode(virtualTarget, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
		data.putString("type", virtualTarget.getType().getId().toString());

		return data;

//		ResourceLocation id = REGISTRY.getId(virtualTarget.getType());
//		String type = id.toString();
//
//		CompoundTag nbt = virtualTarget.getType().toNbt(virtualTarget);
//		nbt.putString("type", type);
//
//		return nbt;
	}

	public void register() {
	}

	public void unregister() {
	}

	public abstract <T extends VirtualTarget> VirtualTargetType<T> getType();

	public boolean shouldInvalidate(Location riftDeleted) {
		return false;
	}

	public RGBA getColor() {
		return this.getType().getColor();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		VirtualTarget that = (VirtualTarget) o;
		return Objects.equals(this.location, that.location);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.location);
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Location getLocation() {
		return this.location;
	}

	public boolean isDummy() {
		return false;
	}

    public abstract VirtualTarget copy();

    public interface VirtualTargetType<T extends VirtualTarget> {
		Registrar<VirtualTargetType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<VirtualTargetType<?>>builder(DimensionalDoors.id("virtual_type")).build();
		Codec<VirtualTargetType<?>> CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);

		RegistrySupplier<VirtualTargetType<RandomTarget>> AVAILABLE_LINK = register("dimdoors:available_link", RandomTarget.CODEC);
		RegistrySupplier<VirtualTargetType<DungeonTarget>> DUNGEON = register("dimdoors:dungeon", DungeonTarget.CODEC);
		RegistrySupplier<VirtualTargetType<TemplateTarget>> TEMPLATE = register("dimdoors:template", TemplateTarget.CODEC);
		RegistrySupplier<VirtualTargetType<EscapeTarget>> ESCAPE = register("dimdoors:escape", EscapeTarget.CODEC);
		RegistrySupplier<VirtualTargetType<GlobalReference>> GLOBAL = register("dimdoors:global", GlobalReference.CODEC);
		RegistrySupplier<VirtualTargetType<LimboTarget>> LIMBO = register("dimdoors:limbo", LimboTarget.INSTANCE);
		RegistrySupplier<VirtualTargetType<LocalReference>> LOCAL = register("dimdoors:local", LocalReference.CODEC);
		RegistrySupplier<VirtualTargetType<PublicPocketTarget>> PUBLIC_POCKET = register("dimdoors:public_pocket", PublicPocketTarget.CODEC);
		RegistrySupplier<VirtualTargetType<PocketEntranceMarker>> POCKET_ENTRANCE = register("dimdoors:pocket_entrance", PocketEntranceMarker.CODEC);
		RegistrySupplier<VirtualTargetType<PocketExitMarker>> POCKET_EXIT = register("dimdoors:pocket_exit", VirtualTarget.COLOR, PocketExitMarker.INSTANCE);
		RegistrySupplier<VirtualTargetType<PrivatePocketTarget>> PRIVATE = register("dimdoors:private", PrivatePocketExitTarget.COLOR, PrivatePocketTarget.INSTANCE);
		RegistrySupplier<VirtualTargetType<PrivatePocketExitTarget>> PRIVATE_POCKET_EXIT = register("dimdoors:private_pocket_exit", PrivatePocketExitTarget.COLOR, PrivatePocketExitTarget.INSTANCE);
		RegistrySupplier<VirtualTargetType<RelativeReference>> RELATIVE = register("dimdoors:relative", RelativeReference.CODEC);
		RegistrySupplier<VirtualTargetType<IdMarker>> ID_MARKER = register("dimdoors:id_marker", IdMarker.CODEC);
		RegistrySupplier<VirtualTargetType<UnstableTarget>> UNSTABLE = register("dimdoors:unstable", UnstableTarget.INSTANCE);
		RegistrySupplier<VirtualTargetType<NoneTarget>> NONE = register("dimdoors:none", NoneTarget.INSTANCE);

		Map<VirtualTargetType<?>, String> TRANSLATION_KEYS = new Object2ObjectArrayMap<>();

		MapCodec<T> codec();

		RGBA getColor();

		default ResourceLocation getId() {
			return REGISTRY.getId(this);
		}

		default String getTranslationKey() {
			return TRANSLATION_KEYS.computeIfAbsent(this, t -> {
				ResourceLocation id = t.getId();
				return "dimdoors.virtualTarget." + id.getNamespace() + "." + id.getPath();
			});
		}

		static void register() {}

		static <T extends VirtualTarget> RegistrySupplier<VirtualTargetType<T>> register(String id, T instance) {
			return register(id, COLOR, instance);
		}

		static <T extends VirtualTarget> RegistrySupplier<VirtualTargetType<T>> register(String id, RGBA color, T instance) {
			return register(id, MapCodec.unit(instance), color);
		}

		static <T extends VirtualTarget> RegistrySupplier<VirtualTargetType<T>> register(String id, MapCodec<T> codec) {
			return register(id, codec, COLOR);
		}

		static <T extends VirtualTarget> RegistrySupplier<VirtualTargetType<T>> register(String id, MapCodec<T> codec, RGBA color) {
			return REGISTRY.register(ResourceLocation.tryParse(id), () -> new VirtualTargetType<T>() {
				@Override
				public MapCodec<T> codec() {
					return codec;
				}

				@Override
				public RGBA getColor() {
					return color;
				}
			});
		}
	}

	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public static class NoneTarget extends VirtualTarget {
		public static final NoneTarget INSTANCE = new NoneTarget();

		private NoneTarget() {
		}

		@Override
		public VirtualTargetType<? extends VirtualTarget> getType() {
			return VirtualTargetType.NONE.get();
		}

		@Override
		public boolean equals(Object o) {
			return o == INSTANCE;
		}

		@Override
		public int hashCode() {
			return System.identityHashCode(INSTANCE);
		}

		@Override
		public VirtualTarget copy() {
			return INSTANCE;
		}

		@Override
		public String toString() {
			return "[none]";
		}
	}
}

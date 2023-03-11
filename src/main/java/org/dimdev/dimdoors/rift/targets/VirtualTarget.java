package org.dimdev.dimdoors.rift.targets;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {
	public static final Registry<VirtualTargetType<?>> REGISTRY = FabricRegistryBuilder.<VirtualTargetType<?>, MappedRegistry<VirtualTargetType<?>>>from(new MappedRegistry<>(ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_type")), Lifecycle.stable(), false)).buildAndRegister();
	public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

	protected Location location;

	public static VirtualTarget fromNbt(CompoundTag nbt) {
		ResourceLocation id = new ResourceLocation(nbt.getString("type"));
		return Objects.requireNonNull(REGISTRY.get(id), "Unknown virtual target type " + id).fromNbt(nbt);
	}

	public static CompoundTag toNbt(VirtualTarget virtualTarget) {
		ResourceLocation id = REGISTRY.getKey(virtualTarget.getType());
		String type = id.toString();

		CompoundTag nbt = virtualTarget.getType().toNbt(virtualTarget);
		nbt.putString("type", type);

		return nbt;
	}

	public void register() {
	}

	public void unregister() {
	}

	public abstract VirtualTargetType<? extends VirtualTarget> getType();

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

	public interface VirtualTargetType<T extends VirtualTarget> {
		VirtualTargetType<RandomTarget> AVAILABLE_LINK = register("dimdoors:available_link", RandomTarget::fromNbt, RandomTarget::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<RandomTarget> DUNGEON = register("dimdoors:dungeon", DungeonTarget::fromNbt, DungeonTarget::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<EscapeTarget> ESCAPE = register("dimdoors:escape", EscapeTarget::fromNbt, EscapeTarget::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<GlobalReference> GLOBAL = register("dimdoors:global", GlobalReference::fromNbt, GlobalReference::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<LimboTarget> LIMBO = register("dimdoors:limbo", a -> LimboTarget.INSTANCE, a -> new CompoundTag(), VirtualTarget.COLOR);
		VirtualTargetType<LocalReference> LOCAL = register("dimdoors:local", LocalReference::fromNbt, LocalReference::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<PublicPocketTarget> PUBLIC_POCKET = register("dimdoors:public_pocket", PublicPocketTarget::fromNbt, PublicPocketTarget::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<PocketEntranceMarker> POCKET_ENTRANCE = register("dimdoors:pocket_entrance", PocketEntranceMarker::fromNbt, PocketEntranceMarker::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<PocketExitMarker> POCKET_EXIT = register("dimdoors:pocket_exit", a -> new PocketExitMarker(), a -> new CompoundTag(), VirtualTarget.COLOR);
		VirtualTargetType<PrivatePocketTarget> PRIVATE = register("dimdoors:private", a -> new PrivatePocketTarget(), a -> new CompoundTag(), PrivatePocketExitTarget.COLOR);
		VirtualTargetType<PrivatePocketExitTarget> PRIVATE_POCKET_EXIT = register("dimdoors:private_pocket_exit", a -> new PrivatePocketExitTarget(), a -> new CompoundTag(), PrivatePocketExitTarget.COLOR);
		VirtualTargetType<RelativeReference> RELATIVE = register("dimdoors:relative", RelativeReference::fromNbt, RelativeReference::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<IdMarker> ID_MARKER = register("dimdoors:id_marker", IdMarker::fromNbt, IdMarker::toNbt, VirtualTarget.COLOR);
		VirtualTargetType<UnstableTarget> UNSTABLE = register("dimdoors:unstable", nbt -> new UnstableTarget(), t -> new CompoundTag(), VirtualTarget.COLOR);
		VirtualTargetType<NoneTarget> NONE = register("dimdoors:none", nbt -> NoneTarget.INSTANCE, i -> new CompoundTag(), COLOR);
		Map<VirtualTargetType<?>, String> TRANSLATION_KEYS = new Object2ObjectArrayMap<>();

		T fromNbt(CompoundTag nbt);

		CompoundTag toNbt(VirtualTarget virtualType);

		RGBA getColor();

		default ResourceLocation getId() {
			return REGISTRY.getKey(this);
		}

		default String getTranslationKey() {
			return TRANSLATION_KEYS.computeIfAbsent(this, t -> {
				ResourceLocation id = t.getId();
				return "dimdoors.virtualTarget." + id.getNamespace() + "." + id.getPath();
			});
		}

		static void register() {
			DimensionalDoors.apiSubscribers.forEach(d -> d.registerVirtualTargetTypes(REGISTRY));
		}

		@SuppressWarnings("unchecked")
		static <T extends VirtualTarget> VirtualTargetType<T> register(String id, Function<CompoundTag, T> fromNbt, Function<T, CompoundTag> toNbt, RGBA color) {
			return Registry.register(REGISTRY, (String) id, new VirtualTargetType<T>() {
				@Override
				public T fromNbt(CompoundTag nbt) {
					return fromNbt.apply(nbt);
				}

				@Override
				public CompoundTag toNbt(VirtualTarget virtualType) {
					return toNbt.apply((T) virtualType);
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
			return VirtualTargetType.NONE;
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
		public String toString() {
			return "[none]";
		}
	}
}

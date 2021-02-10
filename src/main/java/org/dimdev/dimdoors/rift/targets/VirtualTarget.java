package org.dimdev.dimdoors.rift.targets;

import java.util.Objects;
import java.util.function.Function;

import com.mojang.serialization.Lifecycle;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.RGBA;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {
	public static final Registry<VirtualTargetType<?>> REGISTRY = FabricRegistryBuilder.<VirtualTargetType<?>, SimpleRegistry<VirtualTargetType<?>>>from(new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier("dimdoors", "virtual_type")), Lifecycle.stable())).buildAndRegister();
	public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

	protected Location location;

	public static VirtualTarget fromTag(CompoundTag nbt) {
		return Objects.requireNonNull(REGISTRY.get(new Identifier(nbt.getString("type")))).fromTag(nbt);
	}

	public static CompoundTag toTag(VirtualTarget virtualTarget) {
		Identifier id = REGISTRY.getId(virtualTarget.getType());
		String type = id.toString();

		CompoundTag tag = virtualTarget.getType().toTag(virtualTarget);
		tag.putString("type", type);

		return tag;
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

	public boolean isDummy() {
		return false;
	}

	public interface VirtualTargetType<T extends VirtualTarget> {
		VirtualTargetType<RandomTarget> AVAILABLE_LINK = register("dimdoors:available_link", RandomTarget::fromTag, RandomTarget::toTag, VirtualTarget.COLOR);
		VirtualTargetType<EscapeTarget> ESCAPE = register("dimdoors:escape", EscapeTarget::fromTag, EscapeTarget::toTag, VirtualTarget.COLOR);
		VirtualTargetType<GlobalReference> GLOBAL = register("dimdoors:global", GlobalReference::fromTag, GlobalReference::toTag, VirtualTarget.COLOR);
		VirtualTargetType<LimboTarget> LIMBO = register("dimdoors:limbo", a -> LimboTarget.INSTANCE, a -> new CompoundTag(), VirtualTarget.COLOR);
		VirtualTargetType<LocalReference> LOCAL = register("dimdoors:local", LocalReference::fromTag, LocalReference::toTag, VirtualTarget.COLOR);
		VirtualTargetType<PublicPocketTarget> PUBLIC_POCKET = register("dimdoors:public_pocket", PublicPocketTarget::fromTag, PublicPocketTarget::toTag, VirtualTarget.COLOR);
		VirtualTargetType<PocketEntranceMarker> POCKET_ENTRANCE = register("dimdoors:pocket_entrance", PocketEntranceMarker::fromTag, PocketEntranceMarker::toTag, VirtualTarget.COLOR);
		VirtualTargetType<PocketExitMarker> POCKET_EXIT = register("dimdoors:pocket_exit", a -> new PocketExitMarker(), a -> new CompoundTag(), VirtualTarget.COLOR);
		VirtualTargetType<PrivatePocketTarget> PRIVATE = register("dimdoors:private", a -> new PrivatePocketTarget(), a -> new CompoundTag(), PrivatePocketExitTarget.COLOR);
		VirtualTargetType<PrivatePocketExitTarget> PRIVATE_POCKET_EXIT = register("dimdoors:private_pocket_exit", a -> new PrivatePocketExitTarget(), a -> new CompoundTag(), PrivatePocketExitTarget.COLOR);
		VirtualTargetType<RelativeReference> RELATIVE = register("dimdoors:relative", RelativeReference::fromTag, RelativeReference::toTag, VirtualTarget.COLOR);
		VirtualTargetType<IdMarker> ID_MARKER = register("dimdoors:id_marker", IdMarker::fromTag, IdMarker::toTag, VirtualTarget.COLOR);
		VirtualTargetType<NoneTarget> NONE = register("dimdoors:none", tag -> NoneTarget.INSTANCE, i -> new CompoundTag(), COLOR);

		T fromTag(CompoundTag tag);

		CompoundTag toTag(VirtualTarget virtualType);

		RGBA getColor();

		static void register() {
		}

		@SuppressWarnings("unchecked")
		static <T extends VirtualTarget> VirtualTargetType<T> register(String id, Function<CompoundTag, T> fromTag, Function<T, CompoundTag> toTag, RGBA color) {
			return Registry.register(REGISTRY, (String) id, new VirtualTargetType<T>() {
				@Override
				public T fromTag(CompoundTag tag) {
					return fromTag.apply(tag);
				}

				@Override
				public CompoundTag toTag(VirtualTarget virtualType) {
					return toTag.apply((T) virtualType);
				}

				@Override
				public RGBA getColor() {
					return color;
				}
			});
		}
	}

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

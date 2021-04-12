package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Lifecycle;
import org.dimdev.dimdoors.DimensionalDoorsInitializer;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPocket<V extends AbstractPocket<?>> {
	public static final Registry<AbstractPocketType<? extends AbstractPocket<?>>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<AbstractPocketType<? extends AbstractPocket<?>>>(RegistryKey.ofRegistry(new Identifier("dimdoors", "abstract_pocket_type")), Lifecycle.stable())).buildAndRegister();

	protected Integer id;
	protected RegistryKey<World> world;

	public AbstractPocket(int id, RegistryKey<World> world) {
		this.id = id;
		this.world = world;
	}

	protected AbstractPocket() {
	}

	public int getId() {
		return id;
	}

	public static AbstractPocket<? extends AbstractPocket<?>> deserialize(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type"));
		return REGISTRY.get(id).fromNbt(nbt);
	}

	public static AbstractPocketBuilder<?, ?> deserializeBuilder(NbtCompound nbt) {
		Identifier id = Identifier.tryParse(nbt.getString("type"));
		return REGISTRY.get(id).builder().fromNbt(nbt);
	}

	public static NbtCompound serialize(AbstractPocket<?> pocket) {
		return pocket.toNbt(new NbtCompound());
	}

	public V fromNbt(NbtCompound nbt) {
		this.id = nbt.getInt("id");
		this.world = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("world")));

		return (V) this;
	}

	public NbtCompound toNbt(NbtCompound nbt) {
		nbt.putInt("id", id);
		nbt.putString("world", world.getValue().toString());

		getType().toNbt(nbt);

		return nbt;
	}

	public abstract AbstractPocketType<?> getType();

	public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
		variableMap.put("id", (double) this.id);
		return variableMap;
	}

	public abstract Pocket getReferencedPocket();

	// for bypassing the world check in some cases
	public Pocket getReferencedPocket(PocketDirectory directory) {
		return getReferencedPocket();
	}

	public RegistryKey<World> getWorld() {
		return world;
	}

	public interface AbstractPocketType<T extends AbstractPocket<?>> {
		AbstractPocketType<IdReferencePocket> ID_REFERENCE = register(new Identifier("dimdoors", IdReferencePocket.KEY), IdReferencePocket::new, IdReferencePocket::builder);

		AbstractPocketType<Pocket> POCKET = register(new Identifier("dimdoors", Pocket.KEY), Pocket::new, Pocket::builder);
		AbstractPocketType<PrivatePocket> PRIVATE_POCKET = register(new Identifier("dimdoors", PrivatePocket.KEY), PrivatePocket::new, PrivatePocket::builderPrivatePocket);
		AbstractPocketType<LazyGenerationPocket> LAZY_GENERATION_POCKET = register(new Identifier("dimdoors", LazyGenerationPocket.KEY), LazyGenerationPocket::new, LazyGenerationPocket::builderLazyGenerationPocket);


		T fromNbt(NbtCompound nbt);

		NbtCompound toNbt(NbtCompound nbt);

		T instance();

		AbstractPocketBuilder<?, T> builder();

		static void register() {
			DimensionalDoorsInitializer.apiSubscribers.forEach(d -> d.registerAbstractPocketTypes(REGISTRY));
		}

		static <U extends AbstractPocket<P>, P extends AbstractPocket<P>> AbstractPocketType<U> register(Identifier id, Supplier<U> supplier, Supplier<? extends AbstractPocketBuilder<?, U>> factorySupplier) {
			return Registry.register(REGISTRY, id, new AbstractPocketType<U>() {
				@Override
				public U fromNbt(NbtCompound nbt) {
					return (U) supplier.get().fromNbt(nbt);
				}

				@Override
				public NbtCompound toNbt(NbtCompound nbt) {
					nbt.putString("type", id.toString());
					return nbt;
				}

				@Override
				public U instance() {
					return supplier.get();
				}

				@Override
				public AbstractPocketBuilder<?, U> builder() {
					return factorySupplier.get();
				}
			});
		}
	}

	public static abstract class AbstractPocketBuilder<P extends AbstractPocketBuilder<P, T>, T extends AbstractPocket<?>> {
		protected final AbstractPocketType<T> type;

		private int id;
		private RegistryKey<World> world;

		protected AbstractPocketBuilder(AbstractPocketType<T> type) {
			this.type = type;
		}

		public Vec3i getExpectedSize() {
			return new Vec3i(1, 1, 1);
		}

		public T build() {
			T instance = type.instance();

			instance.id = id;
			instance.world = world;

			return instance;
		}

		public P id(int id) {
			this.id = id;
			return getSelf();
		}

		public P world(RegistryKey<World> world) {
			this.world = world;
			return getSelf();
		}

		public P getSelf() {
			return (P) this;
		}

		abstract public P fromNbt(NbtCompound nbt);

		abstract public NbtCompound toNbt(NbtCompound nbt);

		/*
		public P fromTag(CompoundTag tag) {
			id = tag.getInt("id");
			world = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world")));

			return getSelf();
		}

		public CompoundTag toTag(CompoundTag tag) {
			tag.putInt("id", id);
			tag.putString("world", world.getValue().toString());

			return tag;
		}
		 */
	}
}

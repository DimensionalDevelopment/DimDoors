package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPocket<V extends AbstractPocket<V>> implements IAbstractPocket<V> {
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

	public static AbstractPocket<? extends AbstractPocket<?>> deserialize(CompoundTag tag) {
		Identifier id = Identifier.tryParse(tag.getString("type"));
		return REGISTRY.get(id).fromTag(tag);
	}

	public static CompoundTag serialize(AbstractPocket<?> pocket) {
		return pocket.toTag(new CompoundTag());
	}

	public V fromTag(CompoundTag tag) {
		this.id = tag.getInt("id");
		this.world = RegistryKey.of(Registry.DIMENSION, new Identifier(tag.getString("world")));

		return (V) this;
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putInt("id", id);
		tag.putString("world", world.getValue().toString());

		getType().toTag(tag);

		return tag;
	}

	public abstract AbstractPocketType<V> getType();

	public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
		variableMap.put("id", (double) this.id);
		return variableMap;
	}

	public abstract Pocket getReferencedPocket();

	public RegistryKey<World> getWorld() {
		return world;
	}

	@Override
	public void setID(int id) { // sneakily checking for world, just always set world first when initializing and everything will be fine.
		if (this.id != null) throw new UnsupportedOperationException("Cannot change the id of a pocket that has already been initialized.");
		this.id = id;
	}

	@Override
	public void setWorld(RegistryKey<World> world) {
		if (this.world != null) throw new UnsupportedOperationException("Cannot change the world of a pocket that has already been initialized.");
		this.world = world;
	}

	public interface AbstractPocketType<T extends IAbstractPocket<?>> {
		AbstractPocketType<IdReferencePocket> ID_REFERENCE = register(new Identifier("dimdoors", IdReferencePocket.KEY), IdReferencePocket::new, IdReferencePocket::builder);

		AbstractPocketType<Pocket> POCKET = register(new Identifier("dimdoors", Pocket.KEY), Pocket::new, Pocket::builder);


		T fromTag(CompoundTag tag);

		CompoundTag toTag(CompoundTag tag);

		T instance();

		AbstractPocketBuilder<?, T> builder();

		static void register() {
		}

		static <U extends AbstractPocket<U>> AbstractPocketType<U> register(Identifier id, Supplier<U> supplier, Supplier<AbstractPocketBuilder<?, U>> factorySupplier) {
			return Registry.register(REGISTRY, id, new AbstractPocketType<U>() {
				@Override
				public U fromTag(CompoundTag tag) {
					return supplier.get().fromTag(tag);
				}

				@Override
				public CompoundTag toTag(CompoundTag tag) {
					tag.putString("type", id.toString());
					return tag;
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

	public static abstract class AbstractPocketBuilder<P extends AbstractPocketBuilder<P, T>, T extends IAbstractPocket<?>> {
		private final AbstractPocketType<T> type;

		private int id;
		private RegistryKey<World> world;

		//TODO: fromTag/ toTag for reading builders from json, in subclasses as well
		protected AbstractPocketBuilder(AbstractPocketType<T> type) {
			this.type = type;
		}

		public Vec3i getExpectedSize() {
			return new Vec3i(1, 1, 1);
		}

		public T build() {
			T instance = type.instance();

			instance.setID(id);
			instance.setWorld(world);

			return instance;
		}

		public P id(int id) {
			this.id = id;
			return (P) this;
		}

		public P world(RegistryKey<World> world) {
			this.world = world;
			return (P) this;
		}
	}
}

package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.CodecUtil;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPocket<V extends AbstractPocket<?>> {
	public static final Registrar<AbstractPocketType<? extends AbstractPocket<?>>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<AbstractPocketType<? extends AbstractPocket<?>>>builder(DimensionalDoors.id("abstract_pocket_type")).build();
	public static final Codec<AbstractPocket<?>> CODEC = CodecUtil.registrarCodec(REGISTRY, AbstractPocket::getType, AbstractPocket.AbstractPocketType::mapCodec);

	protected static <T extends AbstractPocket<?>> Products.P2<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>> commonFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(Codec.INT.fieldOf("id").forGetter(AbstractPocket::getId), ResourceKey.codec(Registries.DIMENSION).fieldOf("world").forGetter(AbstractPocket::getWorld));
	}


	protected Integer id;
	protected ResourceKey<Level> world;

	public AbstractPocket(int id, ResourceKey<Level> world) {
		this.id = id;
		this.world = world;
	}

	protected AbstractPocket() {
	}

	public int getId() {
		return id;
	}

	public static AbstractPocket<? extends AbstractPocket<?>> deserialize(CompoundTag nbt) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		return REGISTRY.get(id).fromNbt(nbt);
	}

	public static AbstractPocketBuilder<?, ?> deserializeBuilder(CompoundTag nbt) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		return REGISTRY.get(id).builder().fromNbt(nbt);
	}

	public static CompoundTag serialize(AbstractPocket<?> pocket) {
		return pocket.toNbt(new CompoundTag());
	}

	public V fromNbt(CompoundTag nbt) {
		this.id = nbt.getInt("id");
		this.world = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(nbt.getString("world")));

		return (V) this;
	}

	public CompoundTag toNbt(CompoundTag nbt) {
		nbt.putInt("id", id);
		nbt.putString("world", world.location().toString());

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

	public ResourceKey<Level> getWorld() {
		return world;
	}

	public interface AbstractPocketType<T extends AbstractPocket<?>> {
		RegistrySupplier<AbstractPocketType<IdReferencePocket>> ID_REFERENCE = register(DimensionalDoors.id(IdReferencePocket.KEY), IdReferencePocket::new, IdReferencePocket::builder, IdReferencePocket.CODEC);

		RegistrySupplier<AbstractPocketType<Pocket>> POCKET = register(DimensionalDoors.id(Pocket.KEY), Pocket::new, Pocket::builder, Pocket.CODEC);
		RegistrySupplier<AbstractPocketType<PrivatePocket>> PRIVATE_POCKET = register(DimensionalDoors.id(PrivatePocket.KEY), PrivatePocket::new, PrivatePocket::builderPrivatePocket);
		RegistrySupplier<AbstractPocketType<LazyGenerationPocket>> LAZY_GENERATION_POCKET = register(DimensionalDoors.id(LazyGenerationPocket.KEY), LazyGenerationPocket::new, LazyGenerationPocket::builderLazyGenerationPocket);


		T fromNbt(CompoundTag nbt);

		CompoundTag toNbt(CompoundTag nbt);

		T instance();

		AbstractPocketBuilder<?, T> builder();

		static void register() {
		}

		static <U extends AbstractPocket<P>, P extends AbstractPocket<P>> RegistrySupplier<AbstractPocketType<U>> register(ResourceLocation id, Supplier<U> supplier, Supplier<? extends AbstractPocketBuilder<?, U>> factorySupplier, MapCodec<U> mapCodec) {
			return REGISTRY.register(id, () -> new AbstractPocketType<U>() {
				@Override
				public U fromNbt(CompoundTag nbt) {
					return (U) supplier.get().fromNbt(nbt);
				}

				@Override
				public CompoundTag toNbt(CompoundTag nbt) {
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

				@Override
				public MapCodec<? extends AbstractPocket<?>> mapCodec() {
					return mapCodec;
				}
			});
		}

		MapCodec<? extends AbstractPocket<?>> mapCodec();
	}

	public static abstract class AbstractPocketBuilder<P extends AbstractPocketBuilder<P, T>, T extends AbstractPocket<?>> {
		protected final AbstractPocketType<T> type;

		private int id;
		private ResourceKey<Level> world;

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

		public P world(ResourceKey<Level> world) {
			this.world = world;
			return getSelf();
		}

		public P getSelf() {
			return (P) this;
		}

		abstract public P fromNbt(CompoundTag nbt);

		abstract public CompoundTag toNbt(CompoundTag nbt);

		/*
		public P fromTag(CompoundTag tag) {
			id = tag.getInt("id");
			world = ResourceKey.of(Registry.DIMENSION, new ResourceLocation(tag.getString("world")));

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

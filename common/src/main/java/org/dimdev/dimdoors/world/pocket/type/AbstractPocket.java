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
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPocket {
	public static final Registrar<AbstractPocketType<?, ?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<AbstractPocketType<?, ?>>builder(DimensionalDoors.id("abstract_pocket_type")).build();
	public static final Codec<AbstractPocketType<?, ?>> POCKET_TYPE_CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, REGISTRY::getId);
	public static final Codec<AbstractPocket> CODEC = POCKET_TYPE_CODEC.dispatch(AbstractPocket::getType, AbstractPocketType::mapCodec);
	public static final Codec<AbstractPocketBuilder<?, ?>> BUILDER_CODEC = POCKET_TYPE_CODEC.dispatch(AbstractPocketBuilder::getType, AbstractPocketType::builderMapCodec);

	protected Integer id;
	protected ResourceKey<Level> world;

	public AbstractPocket(int id, ResourceKey<Level> world) {
		this.id = id;
		this.world = world;
	}

	protected AbstractPocket() {
	}

	protected static <T extends AbstractPocket> Products.P2<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>> commonCodecFields(RecordCodecBuilder.Instance<T> instance) {
		return instance.group(
				Codec.INT.fieldOf("id").forGetter(AbstractPocket::getId),
				ResourceKey.codec(Registries.DIMENSION).fieldOf("world").forGetter(AbstractPocket::getWorld));
	}

	public int getId() {
		return id;
	}

	public static AbstractPocket deserialize(CompoundTag nbt) {
		ResourceLocation id = ResourceLocation.tryParse(nbt.getString("type"));
		return CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
	}

	public static AbstractPocketBuilder<?, ?> deserializeBuilder(CompoundTag nbt) {
		return BUILDER_CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
	}

	public static CompoundTag serialize(AbstractPocket pocket) {
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

	public abstract AbstractPocketType<?, ?> getType();

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

	public record AbstractPocketType<U extends AbstractPocket, T extends AbstractPocketBuilder<T, U>>(MapCodec<U> mapCodec, MapCodec<T> builderMapCodec, Supplier<U> builder) {
		public static final RegistrySupplier<AbstractPocketType<IdReferencePocket, IdReferencePocket.IdReferencePocketBuilder>> ID_REFERENCE = register(DimensionalDoors.id(IdReferencePocket.KEY), IdReferencePocket.CODEC, IdReferencePocket.IdReferencePocketBuilder.CODEC, IdReferencePocket::new);

		public static final RegistrySupplier<AbstractPocketType<PocketImpl, PocketImpl.PocketImplBuilder>> POCKET = register(DimensionalDoors.id(PocketImpl.KEY), PocketImpl.CODEC, PocketImpl.PocketImplBuilder.CODEC, PocketImpl::new);
		public static final RegistrySupplier<AbstractPocketType<PrivatePocket, PrivatePocket.PrivatePocketBuilder>> PRIVATE_POCKET = register(DimensionalDoors.id(PrivatePocket.KEY), PrivatePocket.CODEC, PrivatePocket.PrivatePocketBuilder.CODEC, PrivatePocket::new);
		public static final RegistrySupplier<AbstractPocketType<LazyGenerationPocket, LazyGenerationPocket.LazyGenerationPocketBuilder>> LAZY_GENERATION_POCKET = register(DimensionalDoors.id(LazyGenerationPocket.KEY), LazyGenerationPocket::new, LazyGenerationPocket::builderLazyGenerationPocket);

		public static void register() {
		}

		static <U extends AbstractPocket, T extends AbstractPocketBuilder<T,U>> RegistrySupplier<AbstractPocketType<U, T>> register(ResourceLocation id, MapCodec<U> mapCodec, MapCodec<T> builderMapCodec, Supplier<U> builder) {
			return REGISTRY.register(id, () -> new AbstractPocketType<>(mapCodec, builderMapCodec, builder));
		}
	}

	public static abstract class AbstractPocketBuilder<P extends AbstractPocketBuilder<P, T>, T extends AbstractPocket> {
		protected static <T extends AbstractPocketBuilder<?, ?>> Products.P2<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>> commonCodecFields(RecordCodecBuilder.Instance<T> instance) {
			return instance.group(Codec.INT.fieldOf("id").forGetter(t -> t.id), ResourceKey.codec(Registries.DIMENSION).fieldOf("world").forGetter(a -> a.world));
		}

		protected int id;
		protected ResourceKey<Level> world;

		protected AbstractPocketBuilder() {}

		public Vec3i getExpectedSize() {
			return new Vec3i(1, 1, 1);
		}

		public T build() {
			T instance = getType().builder.get();

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

		public abstract AbstractPocketType<T, P> getType();
	}
}

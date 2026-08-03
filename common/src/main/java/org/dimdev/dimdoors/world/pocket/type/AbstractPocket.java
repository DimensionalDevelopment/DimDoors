package org.dimdev.dimdoors.world.pocket.type;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.world.pocket.DialingPocket;
import org.dimdev.dimdoors.world.pocket.PocketDirectory;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractPocket<V extends AbstractPocket<V, T>, T extends AbstractPocket.AbstractPocketBuilder<V, T>> {
    public static <T extends AbstractPocket<?, ?>> Products.P2<RecordCodecBuilder.Mu<T>, Integer, ResourceKey<Level>> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.INT.fieldOf("id").forGetter(AbstractPocket::getId),
                Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(AbstractPocket::getWorld)
        );
    }

    public static final Codec<AbstractPocket<?, ?>> CODEC = AbstractPocketType.CODEC.dispatch(AbstractPocket::getType, AbstractPocketType::codec);

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

    public abstract AbstractPocketType<?, ?> getType();

    public Map<String, Double> toVariableMap(Map<String, Double> variableMap) {
        variableMap.put("id", (double) this.id);
        return variableMap;
    }

    public abstract Pocket<?, ?> getReferencedPocket();

    // for bypassing the world check in some cases
    public Pocket<?, ?> getReferencedPocket(PocketDirectory directory) {
        return getReferencedPocket();
    }

    public ResourceKey<Level> getWorld() {
        return world;
    }

    public record AbstractPocketType<T extends AbstractPocket<T, V>, V extends AbstractPocket.AbstractPocketBuilder<T, V>>(MapCodec<T> codec, MapCodec<V> builderCodec, Supplier<T> supplier) {
        public static final Codec<AbstractPocketType<?, ?>> CODEC = ModRegistries.POCKET_TYPE.byNameCodec();

        public static final AbstractPocketType<IdReferencePocket, IdReferencePocket.IdReferencePocketBuilder> ID_REFERENCE = register(IdReferencePocket.KEY, IdReferencePocket.CODEC, IdReferencePocket.IdReferencePocketBuilder.CODEC, IdReferencePocket::new);
        public static final AbstractPocketType<PocketImpl, PocketImpl.Builder> POCKET = register(Pocket.KEY, PocketImpl.CODEC, PocketImpl.Builder.CODEC, PocketImpl::new);
        public static final AbstractPocketType<PrivatePocket, PrivatePocket.PrivatePocketBuilder> PRIVATE_POCKET = register(PrivatePocket.KEY, PrivatePocket.CODEC, PrivatePocket.PrivatePocketBuilder.CODEC, PrivatePocket::new);
        public static final AbstractPocketType<DialingPocket, DialingPocket.Builder> DIALING = register("dialing_pocket", DialingPocket.CODEC, DialingPocket.Builder.CODEC, DialingPocket::new);

        public static void register() {
        }

        static <U extends AbstractPocket<U, P>, P extends AbstractPocket.AbstractPocketBuilder<U, P>> AbstractPocketType<U, P> register(String id, MapCodec<U> codec, MapCodec<P> builderCodec, Supplier<U> supplier) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.POCKET_TYPE, id, new AbstractPocketType<>(codec, builderCodec, supplier));
        }
    }

    public static abstract class AbstractPocketBuilder<T extends AbstractPocket<T, P>, P extends AbstractPocketBuilder<T, P>> {
        public static final Codec<AbstractPocketBuilder<?, ?>> CODEC = AbstractPocketType.CODEC.dispatch(AbstractPocketBuilder::type, AbstractPocketType::builderCodec);

        protected int id;
        protected ResourceKey<Level> world;

        protected AbstractPocketBuilder() {}

        public Vec3i getExpectedSize() {
            return new Vec3i(1, 1, 1);
        }

        public T build() {
            T instance = type().supplier().get();

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

        abstract public AbstractPocketType<T, P> type();

        public P copy() {
            P copy = instance();
            copy.id = this.id;
            copy.world = this.world;
            return copy;
        }

        public abstract P instance();
    }
}

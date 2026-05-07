package org.dimdev.dimdoors.rift.targets;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Objects;

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {
    public static final Codec<VirtualTarget> CODEC = VirtualTargetType.CODEC.dispatch("type", VirtualTarget::getType, VirtualTargetType::mapCodec);

    public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

    protected Location location;

    public static VirtualTarget fromNbt(CompoundTag nbt) {

        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(NoneTarget.INSTANCE);
    }

    public static <T extends VirtualTarget> Tag toNbt(T virtualTarget) {
        return CODEC.encodeStart(NbtOps.INSTANCE, virtualTarget).result().orElseThrow();
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
        ResourceKey<Registry<VirtualTargetType<? extends VirtualTarget>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("virtual_type"));
        Registry<VirtualTargetType<?>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);
        Codec<VirtualTargetType<?>> CODEC = REGISTRY.byNameCodec();

        VirtualTargetType<RandomTarget> AVAILABLE_LINK = register("available_link", RandomTarget.CODEC);
        VirtualTargetType<DungeonTarget> DUNGEON = register("dungeon", DungeonTarget.CODEC);
        VirtualTargetType<TemplateTarget> TEMPLATE = register("template", TemplateTarget.CODEC);
        VirtualTargetType<EscapeTarget> ESCAPE = register("escape", EscapeTarget.CODEC);
        VirtualTargetType<RiftReference> RIFT_REFENCE = register("rift_reference", RiftReference.CODEC);
        VirtualTargetType<LimboTarget> LIMBO = register("limbo", LimboTarget.INSTANCE);
        VirtualTargetType<PublicPocketTarget> PUBLIC_POCKET = register("public_pocket", PublicPocketTarget.CODEC);
        VirtualTargetType<PocketEntranceMarker> POCKET_ENTRANCE = register("pocket_entrance", PocketEntranceMarker.CODEC);
        VirtualTargetType<PocketExitMarker> POCKET_EXIT = register("pocket_exit", VirtualTarget.COLOR, PocketExitMarker.INSTANCE);
        VirtualTargetType<PrivatePocketTarget> PRIVATE = register("private", PrivatePocketExitTarget.COLOR, PrivatePocketTarget.INSTANCE);
        VirtualTargetType<PrivatePocketExitTarget> PRIVATE_POCKET_EXIT = register("private_pocket_exit", PrivatePocketExitTarget.COLOR, PrivatePocketExitTarget.INSTANCE);
        VirtualTargetType<UnstableTarget> UNSTABLE = register("unstable", UnstableTarget.INSTANCE);
        VirtualTargetType<IdMarker> ID_MARKER = register("id_marker", IdMarker.CODEC);
        VirtualTargetType<NoneTarget> NONE = register("none", NoneTarget.INSTANCE);

        //Deperecated. Kept to migrate older world. To be removed at a later date.
        VirtualTargetType<LocalReference> LOCAL = register("local", LocalReference.CODEC);
        VirtualTargetType<RelativeReference> RELATIVE = register("relative", RelativeReference.CODEC);
        VirtualTargetType<RiftReference> GLOBAL = register("global", RiftReference.CODEC);

        Map<VirtualTargetType<?>, String> TRANSLATION_KEYS = new Object2ObjectArrayMap<>();

        Codec<T> codec();

        MapCodec<T> mapCodec();

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
        }

        static <T extends VirtualTarget> VirtualTargetType<T> register(String id, T instance) {
            return register(id, COLOR, instance);
        }

        static <T extends VirtualTarget> VirtualTargetType<T> register(String id, RGBA color, T instance) {
            return register(id, MapCodec.unit(instance), color);
        }

        static <T extends VirtualTarget> VirtualTargetType<T> register(String id, MapCodec<T> codec) {
            return register(id, codec, COLOR);
        }

        static <T extends VirtualTarget> VirtualTargetType<T> register(String id, MapCodec<T> codec, RGBA color) {
            return DimensionalDoors.getSided().register(KEY, id, new VirtualTargetType<T>() {
                private Codec<T> cached = codec.codec(); //TODO: REvert codecs to mapCodec vs caching when motive is there.

                @Override
                public MapCodec<T> mapCodec() {
                    return codec;
                }

                @Override
                public Codec<T> codec() {
                    return cached;
                }

                @Override
                public RGBA getColor() {
                    return color;
                }
            });
        }
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public static final class NoneTarget extends VirtualTarget {
        private static final Logger logger = LogUtils.getLogger();

        public static final NoneTarget INSTANCE = new NoneTarget();

        private NoneTarget() {
        }

        @Override
        public VirtualTargetType<? extends VirtualTarget> getType() {
            return VirtualTargetType.NONE;
        }

        @Override
        public void setLocation(final Location location) {
            logger.warn("Attempted to set location of NoneTarget to {}", location, new Throwable());
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
package org.dimdev.dimdoors.rift.targets;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.ModRegistries;
import org.dimdev.dimdoors.ModRegistryKeys;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.api.util.RGBA;
import org.slf4j.Logger;

import java.util.Objects;

/**
 * A target that is not an actual object in the game such as a block or a block
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget<T extends VirtualTarget<?>> implements Target {
    public static final Codec<VirtualTarget<?>> CODEC = VirtualTargetType.CODEC.dispatch("type", VirtualTarget::getType, VirtualTargetType::codec);

    public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

    protected Location location;

    public static VirtualTarget<?> fromNbt(CompoundTag nbt) {

        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(NoneTarget.INSTANCE);
    }

    public static <T extends VirtualTarget<T>> Tag toNbt(T virtualTarget) {
        return CODEC.encodeStart(NbtOps.INSTANCE, virtualTarget).result().orElseThrow();
    }

    public void register() {
    }

    public void unregister() {
    }

    public abstract VirtualTargetType<T> getType();

    public boolean shouldInvalidate(Location riftDeleted) {
        return false;
    }

    public RGBA getColor() {
        return this.getType().color();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        VirtualTarget<?> that = (VirtualTarget<?>) o;
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

    public abstract T copy();

    public record VirtualTargetType<T extends VirtualTarget<?>>(MapCodec<T> codec, RGBA color) {
        public static final Codec<VirtualTargetType<?>> CODEC = ModRegistries.VIRTUAL_TYPE.byNameCodec();

        public static final VirtualTargetType<AvailableLinkTarget> AVAILABLE_LINK = register("available_link", AvailableLinkTarget.CODEC);
        public static final VirtualTargetType<DungeonTarget> DUNGEON = register("dungeon", DungeonTarget.CODEC);
        public static final VirtualTargetType<TemplateTarget> TEMPLATE = register("template", TemplateTarget.CODEC);
        public static final VirtualTargetType<EscapeTarget> ESCAPE = register("escape", EscapeTarget.CODEC);
        public static final VirtualTargetType<RiftReference> RIFT_REFERENCE = register("rift_reference", RiftReference.CODEC);
        public static final VirtualTargetType<TempTarget> TEMP = register("temp", TempTarget.CODEC);
        public static final VirtualTargetType<LimboTarget> LIMBO = register("limbo", LimboTarget.INSTANCE);
        public static final VirtualTargetType<PublicPocketTarget> PUBLIC_POCKET = register("public_pocket", PublicPocketTarget.CODEC);
        public static final VirtualTargetType<PocketEntranceMarker> POCKET_ENTRANCE = register("pocket_entrance", PocketEntranceMarker.CODEC);
        public static final VirtualTargetType<PocketExitMarker> POCKET_EXIT = register("pocket_exit", VirtualTarget.COLOR, PocketExitMarker.INSTANCE);
        public static final VirtualTargetType<PrivatePocketTarget> PRIVATE = register("private", PrivatePocketExitTarget.COLOR, PrivatePocketTarget.INSTANCE);
        public static final VirtualTargetType<PrivatePocketExitTarget> PRIVATE_POCKET_EXIT = register("private_pocket_exit", PrivatePocketExitTarget.COLOR, PrivatePocketExitTarget.INSTANCE);
        public static final VirtualTargetType<UnstableTarget> UNSTABLE = register("unstable", UnstableTarget.INSTANCE);
        public static final VirtualTargetType<IdMarker> ID_MARKER = register("id_marker", IdMarker.CODEC);
        public static final VirtualTargetType<NoneTarget> NONE = register("none", NoneTarget.INSTANCE);

        //Deperecated. Kept to migrate older world. To be removed at a later date.
        public static final VirtualTargetType<LocalReference> LOCAL = registerDeprecated("local", LocalReference.CODEC);
        public static final VirtualTargetType<RelativeReference> RELATIVE = registerDeprecated("relative", RelativeReference.CODEC);
        public static final VirtualTargetType<RiftReference> GLOBAL = register("global", RiftReference.CODEC);

        public static void register() {
        }

        static <T extends VirtualTarget<T>> VirtualTargetType<T> register(String id, T instance) {
            return register(id, COLOR, instance);
        }

        static <T extends VirtualTarget<T>> VirtualTargetType<T> register(String id, RGBA color, T instance) {
            return register(id, MapCodec.unit(instance), color);
        }

        static <T extends VirtualTarget<T>> VirtualTargetType<T> register(String id, MapCodec<T> codec) {
            return register(id, codec, COLOR);
        }

        static <T extends VirtualTarget<?>> VirtualTargetType<T> registerDeprecated(String id, MapCodec<T> codec) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.VIRTUAL_TYPE, id, new VirtualTargetType<>(codec, COLOR));
        }


        static <T extends VirtualTarget<T>> VirtualTargetType<T> register(String id, MapCodec<T> codec, RGBA color) {
            return DimensionalDoors.getSided().register(ModRegistryKeys.VIRTUAL_TYPE, id, new VirtualTargetType<>(codec, color));
        }
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public static final class NoneTarget extends VirtualTarget<NoneTarget> {
        private static final Logger logger = LogUtils.getLogger();

        public static final NoneTarget INSTANCE = new NoneTarget();

        private NoneTarget() {}

        @Override
        public VirtualTargetType<NoneTarget> getType() {
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
        public NoneTarget copy() {
            return INSTANCE;
        }

        @Override
        public String toString() {
            return "[none]";
        }
    }
}
package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import org.dimdev.dimdoors.util.Location;
import org.dimdev.dimdoors.util.NbtUtil;
import org.dimdev.dimdoors.util.RGBA;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;

/**
 * A target that is not an actual object in the game such as a block or a tile
 * entity. Only virtual targets can be saved to NBT.
 */
public abstract class VirtualTarget implements Target {
    public static final Registry<VirtualTargetType> registry = FabricRegistryBuilder.createSimple(VirtualTargetType.class, new Identifier("dimdoors", "virtual_type")).attribute(RegistryAttribute.MODDED).buildAndRegister();
    public static final RGBA COLOR = new RGBA(1, 0, 0, 1);

    public static Codec<VirtualTarget> CODEC = registry.dispatch(VirtualTarget::getType, VirtualTargetType::codec);

    protected Location location;

    public static VirtualTarget readVirtualTargetNBT(CompoundTag nbt) {
        return NbtUtil.deserialize(nbt, CODEC);
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

    public boolean equals(Object o) {
        return o instanceof VirtualTarget &&
                ((VirtualTarget) o).canEqual(this) &&
                (this.location == null ? ((VirtualTarget) o).location == null : ((Object) this.location).equals(((VirtualTarget) o).location));
    }

    protected boolean canEqual(Object other) {
        return other instanceof VirtualTarget;
    }

    public int hashCode() {
        return 59 + (this.location == null ? 43 : this.location.hashCode());
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isDummy() {
        return false;
    }

    public interface VirtualTargetType<T extends VirtualTarget> {
        VirtualTargetType<RandomTarget> AVAILABLE_LINK = register("available_link", RandomTarget.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<EscapeTarget> ESCAPE = register("escape", EscapeTarget.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<GlobalReference> GLOBAL = register("global", GlobalReference.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<LimboTarget> LIMBO = register("limbo", LimboTarget.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<LocalReference> LOCAL = register("local", LocalReference.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<PublicPocketTarget> PUBLIC_POCKET = register("public_pocket", PublicPocketTarget.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<PocketEntranceMarker> POCKET_ENTRANCE = register("pocket_entrance", PocketEntranceMarker.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<PocketExitMarker> POCKET_EXIT = register("pocket_exit", PocketExitMarker.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<PrivatePocketTarget> PRIVATE = register("private", PrivatePocketTarget.CODEC, PrivatePocketExitTarget.COLOR);
        VirtualTargetType<PrivatePocketExitTarget> PRIVATE_POCKET_EXIT = register("private_pocket_exit", PrivatePocketExitTarget.CODEC, PrivatePocketExitTarget.COLOR);
        VirtualTargetType<RelativeReference> RELATIVE = register("relative", RelativeReference.CODEC, VirtualTarget.COLOR);
        VirtualTargetType<NoneTarget> NONE = register("none", NoneTarget.CODEC, COLOR);

        Codec<T> codec();

        RGBA getColor();

        static <T extends VirtualTarget> VirtualTargetType<T> register(String id, Codec<T> codec, RGBA color) {
            return Registry.register(registry, (String) id, new VirtualTargetType<T>() {
                @Override
                public Codec<T> codec() {
                    return codec;
                }

                @Override
                public RGBA getColor() {
                    return color;
                }
            });
        }
    }

    public static class NoneTarget extends VirtualTarget {
        public static NoneTarget DUMMY = new NoneTarget();

        public static Codec<NoneTarget> CODEC = Codec.unit(DUMMY);

        @Override
        public VirtualTargetType<? extends VirtualTarget> getType() {
            return VirtualTargetType.NONE;
        }
    }
}

package org.dimdev.dimdoors.rift.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public abstract class RegistryVertex {
    public static final ResourceKey<Registry<RegistryVertexType<?>>> KEY = ResourceKey.createRegistryKey(DimensionalDoors.id("registry_vertex"));
    public static final Registry<RegistryVertexType<?>> REGISTRY = DimensionalDoors.getSided().createRegistry(KEY);
    public static final Codec<RegistryVertex> CODEC = ResourceLocation.CODEC.dispatch("type", RegistryVertex::getTypeId, RegistryVertex::getCodec);

    private ResourceKey<Level> world; // The dimension to store this object in. Links are stored in both registries.

    protected UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
    }

    public void targetGone(RegistryVertex target) {
    }

    public void sourceAdded(RegistryVertex source) {
    }

    public void targetAdded(RegistryVertex target) {
    }

    public void sourceMoved(RegistryVertex source) {
    }

    public void targetMoved(RegistryVertex target) {
    }


    public abstract RegistryVertexType<? extends RegistryVertex> getType();

    public String toString() {
        return "RegistryVertex(dim=" + this.world + ", id=" + this.id + ")";
    }

    public static RegistryVertex fromNbt(CompoundTag nbt) {
        return Objects.requireNonNull(REGISTRY.get(ResourceLocation.parse(nbt.getString("type")))).fromNbt(nbt);
    }

    public static CompoundTag toNbt(RegistryVertex registryVertex) {
        String type = REGISTRY.getKey(registryVertex.getType()).toString();

        CompoundTag nbt = registryVertex.getType().toNbt(registryVertex);
        nbt.putString("type", type);

        return nbt;
    }

    private static ResourceLocation getTypeId(RegistryVertex registryVertex) {
        return Objects.requireNonNull(REGISTRY.getKey(registryVertex.getType()), "Unregistered registry vertex type " + registryVertex.getType());
    }

    private static MapCodec<? extends RegistryVertex> getCodec(ResourceLocation type) {
        return Objects.requireNonNull(REGISTRY.get(type), "Unknown registry vertex type " + type).codec();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    protected ResourceKey<Level> getWorld() {
        return world;
    }

    protected void setWorld(ResourceKey<Level> world) {
        this.world = world;
    }

    public interface RegistryVertexType<T extends RegistryVertex> {
        RegistryVertexType<PlayerRiftPointer> PLAYER = register("player", PlayerRiftPointer.MAP_CODEC, PlayerRiftPointer::fromNbt, PlayerRiftPointer::toNbt);
        RegistryVertexType<Rift> RIFT = register("rift", Rift.MAP_CODEC, Rift::fromNbt, Rift::toNbt);
        RegistryVertexType<PocketEntrancePointer> ENTRANCE = register("entrance", PocketEntrancePointer.MAP_CODEC, PocketEntrancePointer::fromNbt, PocketEntrancePointer::toNbt);
        RegistryVertexType<RiftPlaceholder> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder.MAP_CODEC, RiftPlaceholder::fromNbt, RiftPlaceholder::toNbt);

        static void register() {
        }

        T fromNbt(CompoundTag nbt);

        CompoundTag toNbt(RegistryVertex virtualType);

        MapCodec<T> codec();

        static <T extends RegistryVertex> RegistryVertexType<T> register(String id, MapCodec<T> codec, Function<CompoundTag, T> fromNbt, Function<T, CompoundTag> toNbt) {
            return DimensionalDoors.getSided().register(KEY, id, new RegistryVertexType<T>() {
                @Override
                public T fromNbt(CompoundTag nbt) {
                    return fromNbt.apply(nbt);
                }

                @Override
                public CompoundTag toNbt(RegistryVertex registryVertex) {
                    return toNbt.apply((T) registryVertex);
                }

                @Override
                public MapCodec<T> codec() {
                    return codec;
                }
            });
        }
    }
}

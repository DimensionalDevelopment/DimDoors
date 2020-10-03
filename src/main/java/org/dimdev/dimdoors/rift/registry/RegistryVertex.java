package org.dimdev.dimdoors.rift.registry;

import java.util.UUID;

import com.mojang.serialization.Codec;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;

public abstract class RegistryVertex {
    public static final Registry<RegistryVertexType> registry = FabricRegistryBuilder.createSimple(RegistryVertex.RegistryVertexType.class, new Identifier("dimdoors", "registry_vertex")).attribute(RegistryAttribute.MODDED).buildAndRegister();

    public RegistryKey<World> world; // The dimension to store this object in. Links are stored in both registries.

    public static final Codec<RegistryVertex> CODEC = registry.dispatch(RegistryVertex::getType, RegistryVertexType::codec);

    public UUID id = UUID.randomUUID(); // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public void sourceGone(RegistryVertex source) {
    }

    public void targetGone(RegistryVertex target) {
    }

    public void sourceAdded(RegistryVertex source) {
    }

    public void targetAdded(RegistryVertex target) {
    }

    public abstract RegistryVertexType<? extends RegistryVertex> getType();

    public String toString() {
        return "RegistryVertex(dim=" + this.world + ", id=" + this.id + ")";
    }

    public interface RegistryVertexType<T extends RegistryVertex> {
        RegistryVertexType<PlayerRiftPointer> PLAYER = register("player", PlayerRiftPointer.CODEC);
        RegistryVertexType<Rift> RIFT = register("rift", Rift.CODEC);
        RegistryVertexType<PocketEntrancePointer> ENTRANCE = register("entrance", PocketEntrancePointer.CODEC);
        RegistryVertexType<RiftPlaceholder> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder.CODEC);

        Codec<T> codec();

        static <T extends RegistryVertex> RegistryVertex.RegistryVertexType<T> register(String id, Codec<T> codec) {
            return Registry.register(registry, id, () -> codec);
        }
    }
}

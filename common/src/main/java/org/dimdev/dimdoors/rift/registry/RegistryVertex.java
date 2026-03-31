package org.dimdev.dimdoors.rift.registry;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrarManager;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.util.CodecUtils;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public abstract class RegistryVertex {
    public static <T extends RegistryVertex> Products.P1<RecordCodecBuilder.Mu<T>, UUID> commonFields(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(UUIDUtil.CODEC.fieldOf("id").forGetter(RegistryVertex::getId));
    }

	public static final Codec<RegistryVertex> CODEC = RegistryVertexType.CODEC.dispatch(RegistryVertex::getType, RegistryVertexType::codec);

    protected UUID id; // Used to create pointers to registry vertices. Should not be used for anything other than saving.

    public RegistryVertex() {
        this(UUID.randomUUID());
    }

    public RegistryVertex(UUID id) {
        this.id = id;
    }

    public void sourceGone(RegistryVertex source) {}

	public void targetGone(RegistryVertex target) {}

	public void sourceAdded(RegistryVertex source) {}

	public void targetAdded(RegistryVertex target) {}

	public abstract RegistryVertexType<? extends RegistryVertex> getType();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public record RegistryVertexType<T extends RegistryVertex>(MapCodec<T> codec) {
        public static final Registrar<RegistryVertexType<?>> REGISTRY = RegistrarManager.get(DimensionalDoors.MOD_ID).<RegistryVertexType<? extends RegistryVertex>>builder(DimensionalDoors.id("registry_vertex")).build();
        public static final Codec<RegistryVertexType<?>> CODEC = CodecUtils.registarCodec(REGISTRY);

		public static final RegistrySupplier<RegistryVertexType<PlayerRiftPointer>> PLAYER = register("player", PlayerRiftPointer.CODEC);
		public static final RegistrySupplier<RegistryVertexType<Rift>> RIFT = register("rift", Rift.CODEC);
		public static final RegistrySupplier<RegistryVertexType<PocketEntrancePointer>> ENTRANCE = register("entrance", PocketEntrancePointer.CODEC);
		public static final RegistrySupplier<RegistryVertexType<RiftPlaceholder>> RIFT_PLACEHOLDER = register("rift_placeholder", RiftPlaceholder.CODEC);

		public static void register() {}

		static <T extends RegistryVertex> RegistrySupplier<RegistryVertexType<T>> register(String id, MapCodec<T> codec) {
			return REGISTRY.register(DimensionalDoors.id(id), () -> new RegistryVertexType<>(codec));
		}
	}
}
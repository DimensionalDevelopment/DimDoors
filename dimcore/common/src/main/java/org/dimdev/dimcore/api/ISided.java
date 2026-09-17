package org.dimdev.dimcore.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ISided<T extends ISided<T>> extends IRegister, ICreativeTabHandler, INetworking {
    default T self() {
        return (T) this;
    }

    String modId();

    void registerEntityAttributes(EntityType<? extends LivingEntity> type, Supplier<AttributeSupplier.Builder> attributes);

    void addPack(PackType type, String id, String name, boolean defaultedOn);

    default <C extends Config> C loadConfig(Class<C> configClass) {
        return Config.load(this, configClass);
    }

    default <C extends Config> C createConfig(Class<C> configClass) {
        return Config.createInstance(configClass);
    }

    public void registerServerLoader(String name, BiConsumer<HolderLookup.Provider, ResourceManager> consumer, boolean loadAfterTags);


    default void registerServerLoader(String pocketLoader, BiConsumer<HolderLookup.Provider, ResourceManager> consumer) {
        registerServerLoader(pocketLoader, consumer, false);
    }

	<S> void createDynamicRegistry(ResourceKey<Registry<S>> key, Codec<S> codec, Codec<S> networkCodec);

    default <S> void createDynamicRegistry(ResourceKey<Registry<S>> key, Codec<S> codec, boolean synced) {
		createDynamicRegistry(key, codec, synced ? codec : null);
	}

	default <S> void createDynamicRegistry(ResourceKey<Registry<S>> key, Codec<S> codec) {
		createDynamicRegistry(key, codec, null);
	}

	Path configPath();
}

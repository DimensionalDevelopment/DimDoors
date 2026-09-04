package org.dimdev.dimcore.api.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;


public interface IClientSided<T extends IClientSided<T>> {
    default T self() {
        return (T) this;
    }

    void register(RenderType type, Block... blocks);

    void onClientPlayerJoin(Runnable listener);

    void registerKeyBinding(KeyMapping mapping);

    void registerClientLoader(String name, Consumer<ResourceManager> consumer);
}

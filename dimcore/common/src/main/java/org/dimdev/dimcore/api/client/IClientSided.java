package org.dimdev.dimcore.api.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Consumer;


public interface IClientSided<T extends IClientSided<T>> {
    default T self() {
        return (T) this;
    }

    void registerKeyBinding(KeyMapping mapping);

    void registerClientLoader(String name, Consumer<ResourceManager> consumer);
}

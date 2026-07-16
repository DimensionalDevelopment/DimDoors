package org.dimdev.limlib.api.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;


public interface IClientSided<T extends IClientSided<T>> {
    default T self() {
        return (T) this;
    }

    void register(RenderType type, Block... blocks);

    void onClientPlayerJoin(Runnable listener);
}

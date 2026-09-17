package org.dimdev.dimcore.api.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

public interface ClientPlatform {
    void register(RenderType type, Block... blocks);

    void onClientPlayerJoin(Runnable listener);
}

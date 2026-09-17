package org.dimdev.dimcore.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.dimdev.dimcore.api.client.ClientPlatform;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeClientPlatform implements ClientPlatform {
    private final List<Runnable> loginRunnables = new ArrayList<>();

    public NeoForgeClientPlatform() {
        NeoForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggingIn>addListener(event -> loginRunnables.forEach(Runnable::run));
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        loginRunnables.add(listener);
    }
}

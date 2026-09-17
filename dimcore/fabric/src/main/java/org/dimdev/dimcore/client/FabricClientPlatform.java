package org.dimdev.dimcore.client;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import org.dimdev.dimcore.api.client.ClientPlatform;

public class FabricClientPlatform implements ClientPlatform {
	@Override
	public void register(RenderType type, Block... blocks) {
		BlockRenderLayerMap.INSTANCE.putBlocks(type, blocks);
	}

	@Override
	public void onClientPlayerJoin(Runnable listener) {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> listener.run());
	}
}

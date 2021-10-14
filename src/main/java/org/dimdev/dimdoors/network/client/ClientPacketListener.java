package org.dimdev.dimdoors.network.client;

import org.dimdev.dimdoors.network.packet.s2c.*;

public interface ClientPacketListener {
	void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet);

	void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet);

	void onMonolithAggroParticles(MonolithAggroParticlesPacket packet);

	void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet);

	void onRenderBreakBlock(RenderBreakBlockS2CPacket packet);
}

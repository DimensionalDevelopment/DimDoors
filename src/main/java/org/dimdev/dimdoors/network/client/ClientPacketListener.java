package org.dimdev.dimdoors.network.client;

import org.dimdev.dimdoors.network.packet.s2c.MonolithAggroParticlesPacket;
import org.dimdev.dimdoors.network.packet.s2c.MonolithTeleportParticlesPacket;
import org.dimdev.dimdoors.network.packet.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.network.packet.s2c.SyncPocketAddonsS2CPacket;

public interface ClientPacketListener {
	void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet);

	void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet);

	void onMonolithAggroParticles(MonolithAggroParticlesPacket packet);

	void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet);
}

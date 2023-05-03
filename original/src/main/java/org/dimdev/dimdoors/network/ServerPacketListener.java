package org.dimdev.dimdoors.network;

import org.dimdev.dimdoors.network.packet.c2s.HitBlockWithItemC2SPacket;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;

public interface ServerPacketListener {
	void onAttackBlock(HitBlockWithItemC2SPacket packet);

	void onNetworkHandlerInitialized(NetworkHandlerInitializedC2SPacket packet);
}

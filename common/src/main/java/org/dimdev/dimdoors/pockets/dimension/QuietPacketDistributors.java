package org.dimdev.dimdoors.pockets.dimension;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.dimdev.dimdoors.network.ServerPacketHandler;

import java.util.function.Consumer;

public final class QuietPacketDistributors {
	public static <PACKET extends CustomPacketPayload> void sendToAll(MinecraftServer server, PACKET packet) {
        server.getPlayerList().getPlayers().forEach(player -> ServerPacketHandler.sendPacket(player, packet));
	}
}
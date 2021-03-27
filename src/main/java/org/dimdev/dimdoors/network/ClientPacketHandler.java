package org.dimdev.dimdoors.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.network.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.network.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.network.s2c.SyncPocketAddonsS2CPacket;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientPacketHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ClientPlayNetworkHandler networkHandler;
	private boolean initialized = false;

	private final Set<Identifier> registeredChannels = new HashSet<>();

	private RegistryKey<World> pocketWorld;
	private int gridSize = 1;
	private int pocketId = Integer.MIN_VALUE;
	private int pocketRange = 1;
	private List<AutoSyncedAddon> addons = new ArrayList<>();

	public void init() {
		if (initialized) throw new RuntimeException("ClientPacketHandler has already been initialized.");
		initialized = true;
		registerReceiver(PlayerInventorySlotUpdateS2CPacket.ID, PlayerInventorySlotUpdateS2CPacket::new);
		registerReceiver(SyncPocketAddonsS2CPacket.ID, SyncPocketAddonsS2CPacket::new);

		sendPacket(new NetworkHandlerInitializedC2SPacket());
	}

	public static boolean sendPacket(SimplePacket<?> packet) {
		try {
			ClientPlayNetworking.send(packet.channelId(), packet.write(PacketByteBufs.create()));
			return true;
		} catch (IOException e) {
			LOGGER.error(e);
			return false;
		}
	}

	public ClientPacketHandler(ClientPlayNetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
	}

	private void registerReceiver(Identifier channelName, Supplier<? extends SimplePacket<ClientPacketHandler>> supplier) {
		ClientPlayNetworking.registerReceiver(channelName,
				(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) -> {
					try {
						supplier.get().read(buf).apply(this);
					} catch (IOException e) {
						LOGGER.error(e);
					}
				});
		registeredChannels.add(channelName);
	}

	private void unregisterReceiver(Identifier channelName) {
		ClientPlayNetworking.unregisterReceiver(channelName);
		registeredChannels.remove(channelName);
	}

	public void unregister() {
		new HashSet<>(registeredChannels).forEach(this::unregisterReceiver);
	}

	public MinecraftClient getClient() {
		return ((ExtendedClientPlayNetworkHandler) networkHandler).dimdoorsGetClient();
	}

	public void onSyncPocketAddons(RegistryKey<World> world, int gridSize, int pocketId, int pocketRange, List<AutoSyncedAddon> addons) {
		this.pocketWorld = world;
		this.gridSize = gridSize;
		this.pocketId = pocketId;
		this.pocketRange = pocketRange;
		this.addons = addons;
	}

	public void onPlayerInventorySlotUpdate(int slot, ItemStack stack) {
		if (getClient().player != null) {
			getClient().player.getInventory().setStack(slot, stack);
		}
	}

	public RegistryKey<World> getPocketWorld() {
		return pocketWorld;
	}

	public int getGridSize() {
		return gridSize;
	}

	public int getPocketId() {
		return pocketId;
	}

	public int getPocketRange() {
		return pocketRange;
	}

	public List<AutoSyncedAddon> getAddons() {
		return addons;
	}
}

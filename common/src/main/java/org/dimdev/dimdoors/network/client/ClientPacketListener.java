package org.dimdev.dimdoors.network.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.api.util.math.GridUtil;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
import org.dimdev.dimdoors.mixin.client.accessor.WorldRendererAccessor;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.world.pocket.type.addon.AutoSyncedAddon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

@Environment(EnvType.CLIENT)
public class ClientPacketListener {
	private static final Logger LOGGER = LogManager.getLogger();

    private static final RandomSource clientRandom = RandomSource.create();

	private static ResourceKey<Level> pocketWorld;
	private static int gridSize = 1;
	private static int pocketId = Integer.MIN_VALUE;
	private static int pocketRange = 1;
	private static List<AutoSyncedAddon> addons = new ArrayList<>();

    @ExpectPlatform
	public static <T extends CustomPacketPayload> void sendPacket(T packet) {
        throw new RuntimeException("Not implemented");
	}

    public static <T extends CustomPacketPayload> boolean tryToSendPacket(T packet) {
        try {
            sendPacket(packet);
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
            return false;
        }
    }

    public static ResourceKey<Level> getPocketWorld() {
		return pocketWorld;
	}

	public static int getGridSize() {
		return gridSize;
	}

	public static int getPocketId() {
		return pocketId;
	}

	public static int getPocketRange() {
		return pocketRange;
	}

	public static List<AutoSyncedAddon> getAddons() {
		return addons;
	}

	public static void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet) {
		Minecraft.getInstance().execute(() -> {
			if (Minecraft.getInstance().player != null) {
				Minecraft.getInstance().player.getInventory().setItem(packet.slot(), packet.stack());
			}
		});
	}

    public static void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet) {
		pocketWorld = packet.world();
		gridSize = packet.gridSize();
		pocketId = packet.pocketId();
		pocketRange = packet.pocketRange();
		addons = packet.addons();
	}

	public static void onMonolithAggroParticles(MonolithAggroParticlesPacket packet) {
		Minecraft.getInstance().execute(() -> spawnParticles(packet.aggro()));
	}

	@Environment(EnvType.CLIENT)
	public static void spawnParticles(int aggro) {
		Player player = Minecraft.getInstance().player;
		if (aggro < 120) {
			return;
		}
		int count = 10 * aggro / MAX_AGGRO;
		for (int i = 1; i < count; ++i) {
			//noinspection ConstantConditions
			player.level().addParticle(ParticleTypes.PORTAL, player.getX() + (clientRandom.nextDouble() - 0.5D) * 3.0,
					player.getY() + clientRandom.nextDouble() * player.getBbHeight() - 0.75D,
					player.getZ() + (clientRandom.nextDouble() - 0.5D) * player.getBbWidth(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D, -clientRandom.nextDouble(),
					(clientRandom.nextDouble() - 0.5D) * 2.0D);
		}
	}

	public static void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet) {
		Minecraft client = Minecraft.getInstance();
		//noinspection ConstantConditions
		client.execute(() -> client.particleEngine.add(new MonolithParticle(client.level, client.player.getX(), client.player.getY(), client.player.getZ())));
	}

	public static void onRenderBreakBlock(RenderBreakBlockS2CPacket packet) {
		CustomBreakBlockHandler.customBreakBlock(packet.pos(), packet.stage(), ((WorldRendererAccessor) Minecraft.getInstance().levelRenderer).getTicks());
	}

    @Environment(EnvType.CLIENT)
    public static <T> List<T> applicableAddonsClient(Class<T> clazz, Level world, BlockPos pos) {
        if (!world.dimension().equals(getPocketWorld())) return Collections.emptyList();

        int pocketId = GridUtil.gridPosToID(new GridUtil.GridPos(pos, getGridSize()));
        if (pocketId < getPocketId() || pocketId >= getPocketId() + getPocketRange()) {
            return Collections.emptyList();
        }
        return getAddons().stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
    }
}

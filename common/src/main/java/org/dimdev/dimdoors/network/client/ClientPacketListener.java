package org.dimdev.dimdoors.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.util.math.GridUtil;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class ClientPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final RandomSource clientRandom = RandomSource.create();

    private static ResourceKey<Level> pocketWorld;
    private static BoundingBox area;
    private static Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon> addons = new HashMap<>();

    public static <T extends CustomPacketPayload> void sendPacket(T packet) {
        DimensionalDoors.getSided().sendPacket(packet);
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

    public static Map<PocketAddon.PocketAddonType<?, ?>, PocketAddon> getAddons() {
        return addons;
    }

    public static void clearPocketAddons() {
        pocketWorld = null;
        area = null;
        addons = new HashMap<>();
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
        area = packet.box();
        addons = packet.addons().stream().collect(Collectors.toMap(PocketAddon::getType, Function.identity()));
    }

    public static void onMonolithAggroParticles(MonolithAggroParticlesPacket packet) {
        Minecraft.getInstance().execute(() -> spawnParticles(packet.aggro()));
    }

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
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            if (client.level == null) {
                return;
            }
            client.levelRenderer.destroyBlockProgress(Long.hashCode(packet.pos().asLong()), packet.pos(), packet.stage());
        });
    }

    public static <T extends PocketAddon> Optional<T> getAddonClient(PocketAddon.PocketAddonType<T, ?> type, Level world, BlockPos pos) {
        if (!world.dimension().equals(pocketWorld)) return Optional.empty();

        if(!area.isInside(pos.getX(), pos.getY(), pos.getZ())) return Optional.empty();


        return Optional.ofNullable((T) addons.get(type));
    }

    public static BoundingBox getArea() {
        return area;
    }
}

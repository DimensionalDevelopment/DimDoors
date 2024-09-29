package org.dimdev.dimdoors.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.client.CustomBreakBlockHandler;
import org.dimdev.dimdoors.mixin.client.accessor.WorldRendererAccessor;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.network.packet.s2c.*;
import org.dimdev.dimdoors.particle.client.MonolithParticle;

import static org.dimdev.dimdoors.entity.MonolithEntity.MAX_AGGRO;

public class ClientPacketLogicActive extends ClientPacketLogic {
    private static final RandomSource clientRandom = RandomSource.create();

    public void onPlayerInventorySlotUpdate(PlayerInventorySlotUpdateS2CPacket packet, NetworkManager.PacketContext context) {
        Minecraft.getInstance().execute(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getInventory().setItem(packet.slot(), packet.stack());
            }
        });
    }

    public void onSyncPocketAddons(SyncPocketAddonsS2CPacket packet, NetworkManager.PacketContext context) {
        ClientPacketHandler.update(packet);
    }

    public void onMonolithAggroParticles(MonolithAggroParticlesPacket packet, NetworkManager.PacketContext context) {
        Minecraft.getInstance().execute(() -> spawnParticles(packet.aggro()));
    }

    public void spawnParticles(int aggro) {
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

    public void onMonolithTeleportParticles(MonolithTeleportParticlesPacket packet, NetworkManager.PacketContext context) {
        Minecraft client = Minecraft.getInstance();
        //noinspection ConstantConditions
        client.execute(() -> client.particleEngine.add(new MonolithParticle(client.level, client.player.getX(), client.player.getY(), client.player.getZ())));
    }

    public void onRenderBreakBlock(RenderBreakBlockS2CPacket packet, NetworkManager.PacketContext context) {
        CustomBreakBlockHandler.customBreakBlock(packet.pos(), packet.stage(), ((WorldRendererAccessor) Minecraft.getInstance().levelRenderer).getTicks());
    }

    @Override
    public void sendPacket(CustomPacketPayload packet) {
        NetworkManager.sendToServer(packet);
    }
}

package org.dimdev.dimdoors.compat.sable.mixins;

import dev.ryanhcode.sable.network.packets.tcp.ClientboundStopMovingSubLevelPacket;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelTrackingSystem;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SubLevelTrackingSystem.class)
public class SubLevelTrackingSystemDimensionChangeMixin {
    // TODO: Make this more robust by only silencing removal and movement-change packets for sublevels DimDoors forced to load for teleportation.
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Ldev/ryanhcode/sable/sublevel/system/SubLevelTrackingSystem;sendRemoval(Lfoundry/veil/api/network/VeilPacketManager$PacketSink;Ldev/ryanhcode/sable/sublevel/ServerSubLevel;)V", ordinal = 0), remap = false)
    private void dimdoors$skipSourceLevelRemovalPacket(SubLevelTrackingSystem instance, @Coerce Object sink, ServerSubLevel subLevel) {
    }

    @Redirect(method = "sendMovementUpdates", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V"), remap = false
    )
    private void dimdoors$skipSourceLevelStopMovingPacket(ServerGamePacketListenerImpl connection, Packet<?> packet) {
        if (packet instanceof ClientboundCustomPayloadPacket(CustomPacketPayload payload) && payload instanceof ClientboundStopMovingSubLevelPacket) {
            return;
        }

        connection.send(packet);
    }
}

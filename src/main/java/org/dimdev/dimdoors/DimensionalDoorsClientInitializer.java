package org.dimdev.dimdoors;

import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.ModSkyRendering;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.particle.ModParticleTypes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityTypes.initClient();
        ModSkyRendering.initClient();
		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
        ModBlocks.initClient();
		ModParticleTypes.initClient();

		ClientPlayNetworking.registerGlobalReceiver(DimensionalDoorsInitializer.MONOLITH_PARTICLE_PACKET, (client, networkHandler, buf, sender) -> MonolithEntity.spawnParticles(buf, client));
    }
}

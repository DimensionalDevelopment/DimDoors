package org.dimdev.dimdoors;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.ModSkyRendering;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.ClientPacketHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
	private static ClientPacketHandler clientPacketHandler;

	private static final Logger LOGGER = LogManager.getLogger();
    @Override
    public void onInitializeClient() {
        ModEntityTypes.initClient();
        ModSkyRendering.initClient();
		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
        ModBlocks.initClient();
		ModParticleTypes.initClient();

		ClientPlayNetworking.registerGlobalReceiver(DimensionalDoorsInitializer.MONOLITH_PARTICLE_PACKET, (client, networkHandler, buf, sender) -> MonolithEntity.spawnParticles(buf, client));

		registerListeners();
    }

    private void registerListeners() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> clientPacketHandler = new ClientPacketHandler(handler, client));

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			clientPacketHandler.unregister();
			clientPacketHandler = null;
		}));
	}
}

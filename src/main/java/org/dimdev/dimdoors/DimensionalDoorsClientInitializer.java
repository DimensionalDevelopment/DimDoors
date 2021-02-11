package org.dimdev.dimdoors;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.ModSkyRendering;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.s2c.PlayerInventorySlotUpdateS2CPacket;
import org.dimdev.dimdoors.particle.ModParticleTypes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
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

		// TODO: change this to a normal Receiver with the inventory network handler attached. Will have to register/ unregister when a player joins/ leaves a world
		ClientPlayNetworking.registerGlobalReceiver(PlayerInventorySlotUpdateS2CPacket.ID, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
			PlayerInventorySlotUpdateS2CPacket packet = new PlayerInventorySlotUpdateS2CPacket();
			try {
				packet.read(packetByteBuf);
				minecraftClient.player.inventory.setStack(packet.getSlot(), packet.getStack());
				// TODO: remove commented out debug code
				//LOGGER.info("Synced slot " + packet.getSlot() + " with item stack " + packet.getStack().toTag(new CompoundTag()));
			} catch (IOException e) {
				LOGGER.error(e);
			}
		});
    }
}

package org.dimdev.dimdoors.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.client.ExtendedClientPlayNetworkHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider((manager) -> new DimensionalDoorModelVariantProvider());

		ScreenRegistry.register(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
        ModEntityTypes.initClient();
		ModFluids.initClient();
//        ModBlockEntityTypes.initClient();
		BlockEntityRendererRegistry.register(ModBlockEntityTypes.ENTRANCE_RIFT, ctx -> new EntranceRiftBlockEntityRenderer());
		BlockEntityRendererRegistry.register(ModBlockEntityTypes.DETACHED_RIFT, ctx -> new DetachedRiftBlockEntityRenderer());
        ModBlocks.initClient();
		ModEntityModelLayers.initClient();
		ModParticleTypes.initClient();

		DimensionRenderering.initClient();

		registerListeners();
    }

    private void registerListeners() {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ((ExtendedClientPlayNetworkHandler) handler).getDimDoorsPacketHandler().init());

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			((ExtendedClientPlayNetworkHandler) handler).getDimDoorsPacketHandler().unregister();
		}));
	}
}

package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.network.client.ExtendedClientPlayNetworkHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
//		ModelLoadingRegistry.INSTANCE.registerVariantProvider((manager) -> new DimensionalDoorModelVariantProvider());

		MenuRegistry.registerScreenFactory(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
        ModEntityTypes.initClient();
		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
        ModBlocks.initClient();
		ModEntityModelLayers.initClient();
		ModParticleTypes.initClient();

		DimensionRenderering.initClient();

		registerListeners();
    }

    private void registerListeners() {
		ClientReloadShadersEvent.EVENT.register(new ClientReloadShadersEvent() {
			@Override
			public void reload(ResourceProvider provider, ShadersSink sink) {
				try {
					sink.registerShader(new ShaderInstance(provider, "dimensional_portal", DefaultVertexFormat.POSITION), ModShaders::setDimensionalPortal);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {

			((ExtendedClientPlayNetworkHandler) player).getDimDoorsPacketHandler().init();
		});
		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> ((ExtendedClientPlayNetworkHandler) player).getDimDoorsPacketHandler().unregister());
	}
}

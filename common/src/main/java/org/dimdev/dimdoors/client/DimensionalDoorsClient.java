package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.architectury.event.events.client.ClientReloadShadersEvent;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ShaderInstance;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.network.client.ClientPacketHandler;
import org.dimdev.dimdoors.particle.ModParticleTypes;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;

import java.io.IOException;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClient {
    public static void init() {
		MenuRegistry.registerScreenFactory(ModScreenHandlerTypes.TESSELATING_LOOM.get(), TesselatingLoomScreen::new);
        ModEntityTypes.initClient();
//		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
        ModBlocks.initClient();
		ModEntityModelLayers.initClient();
		ModParticleTypes.initClient();

//		DimensionRenderering.initClient();

		registerListeners();

		ClientPacketHandler.init();
    }

    private static void registerListeners() {
		ClientReloadShadersEvent.EVENT.register((provider, sink) -> {
			try {
				sink.registerShader(new ShaderInstance(provider, "dimensional_portal", DefaultVertexFormat.POSITION), ModShaders::setDimensionalPortal);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}

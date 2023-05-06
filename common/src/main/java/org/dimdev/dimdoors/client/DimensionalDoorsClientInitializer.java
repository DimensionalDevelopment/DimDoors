package org.dimdev.dimdoors.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
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
//		ModelLoadingRegistry.INSTANCE.registerVariantProvider((manager) -> new DimensionalDoorModelVariantProvider());

		MenuRegistry.registerScreenFactory(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
        ModEntityTypes.initClient();
		ModFluids.initClient();
        ModBlockEntityTypes.initClient();
		BlockEntityRendererRegistry.register(ModBlockEntityTypes.ENTRANCE_RIFT, new BlockEntityRendererProvider<>() {
			@Override
			public BlockEntityRenderer<BlockEntity> create(Context context) {
				return new EntranceRiftBlockEntityRenderer(context);
				return null;
			}
		});
		BlockEntityRendererRegistry.register(ModBlockEntityTypes.DETACHED_RIFT, ctx -> new DetachedRiftBlockEntityRenderer());
        ModBlocks.initClient();
		ModEntityModelLayers.initClient();
		ModParticleTypes.initClient();

		DimensionRenderering.initClient();

		registerListeners();
    }

    private void registerListeners() {
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> ((ExtendedClientPlayNetworkHandler) player).getDimDoorsPacketHandler().init());

		ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> ((ExtendedClientPlayNetworkHandler) player).getDimDoorsPacketHandler().unregister());
	}
}

package org.dimdev.dimdoors.fluid;

import java.util.function.Function;
import net.fabricmc.api.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.dimdev.dimdoors.DimensionalDoors;

public class ModFluids {
	public static final FlowingFluid ETERNAL_FLUID = register("dimdoors:eternal_fluid", new EternalFluid.Still());
	public static final FlowingFluid FLOWING_ETERNAL_FLUID = register("dimdoors:flowing_eternal_fluid", new EternalFluid.Flowing());

	private static <T extends Fluid> T register(String string, T fluid) {
		return Registry.register(BuiltInRegistries.FLUID, string, fluid);
	}

	public static void init() {
	}

	public static void initClient() {
		setupFluidRendering(ModFluids.ETERNAL_FLUID, ModFluids.FLOWING_ETERNAL_FLUID, DimensionalDoors.resource("eternal_fluid"));
	}

	@OnlyIn(Dist.CLIENT)
	private static void setupFluidRendering(Fluid still, Fluid flowing, final ResourceLocation textureFluidId) {
		final ResourceLocation stillSpriteId = new ResourceLocation(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
		final ResourceLocation flowingSpriteId = new ResourceLocation(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

//		//TODO: Redo
//		// If they're not already present, add the sprites to the block atlas
//		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
//			registry.register(stillSpriteId);
//			registry.register(flowingSpriteId);
//		});

		final ResourceLocation fluidId = BuiltInRegistries.FLUID.getKey(still);
		final ResourceLocation listenerId = new ResourceLocation(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

		final TextureAtlasSprite[] fluidSprites = {null, null};

		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return listenerId;
			}

			/**
			 * Get the sprites from the block atlas when resources are reloaded
			 */
			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {
				final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
				fluidSprites[0] = atlas.apply(stillSpriteId);
				fluidSprites[1] = atlas.apply(flowingSpriteId);
			}
		});

		// The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
		final FluidRenderHandler renderHandler = new FluidRenderHandler() {
			@Override
			public TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter view, BlockPos pos, FluidState state) {
				return fluidSprites;
			}

			@Override
			public int getFluidColor(BlockAndTintGetter view, BlockPos pos, FluidState state) {
				return 16777215;
			}
		};

		FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
	}
}

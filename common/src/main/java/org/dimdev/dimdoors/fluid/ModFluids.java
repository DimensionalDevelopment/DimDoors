package org.dimdev.dimdoors.fluid;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.core.registries.Registries;
import net.minecraft.fluid.FluidState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.dimdev.dimdoors.DimensionalDoors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModFluids {
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(DimensionalDoors.MOD_ID, Registries.FLUID);

	public static final RegistrySupplier<? extends FlowingFluid> ETERNAL_FLUID = register("eternal_fluid", EternalFluid.Still::new);
	public static final RegistrySupplier<? extends  Fluid> FLOWING_ETERNAL_FLUID = register("flowing_eternal_fluid", EternalFluid.Flowing::new);

	private static <T extends Fluid> RegistrySupplier<? extends T> register(String string, Supplier<T> fluid) {
		return FLUIDS.register(new ResourceLocation(string, "dimdoors"), fluid);
	}

	public static void init() {
	}

	public static void initClient() {
		setupFluidRendering(ModFluids.ETERNAL_FLUID.get(), ModFluids.FLOWING_ETERNAL_FLUID.get(), DimensionalDoors.id("eternal_fluid"));
	}

	@Environment(EnvType.CLIENT)
	private static void setupFluidRendering(Fluid still, Fluid flowing, final ResourceLocation textureFluidId) {
		final ResourceLocation stillSpriteId = new ResourceLocation(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
		final ResourceLocation flowingSpriteId = new ResourceLocation(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

//		//TODO: Redo
//		// If they're not already present, add the sprites to the block atlas
//		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
//			registry.register(stillSpriteId);
//			registry.register(flowingSpriteId);
//		});

		final ResourceLocation fluidId = Registries.FLUID.location();
		final ResourceLocation listenerId = new ResourceLocation(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

		final TextureAtlasSprite[] fluidSprites = {null, null};

		new ReloadableResourceManager(PackType.CLIENT_RESOURCES).registerReloadListener((PreparableReloadListener) (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2) -> {
			final Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
			fluidSprites[0] = atlas.apply(stillSpriteId);
			fluidSprites[1] = atlas.apply(flowingSpriteId);
			return null; //TODO should this be null?
		});

		// The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering

		//TODO this is broken without fabric api \/
		final FluidRenderHandler renderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return fluidSprites;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				return 16777215;
			}
		};

		FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
	}
}

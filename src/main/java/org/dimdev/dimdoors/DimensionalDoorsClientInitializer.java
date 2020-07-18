package org.dimdev.dimdoors;

import io.github.waterpicker.openworlds.OpenWorlds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.client.CustomSkyProvider;
import org.dimdev.dimdoors.client.LimboSkyProvider;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.world.ModDimensions;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class DimensionalDoorsClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityTypes.initClient();

        putCutout(ModBlocks.OAK_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.GOLD_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.IRON_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.OAK_DIMENSIONAL_TRAPDOOR);
        putCutout(ModBlocks.QUARTZ_DIMENSIONAL_DOOR);
        putCutout(ModBlocks.QUARTZ_DOOR);

        setupFluidRendering(ModFluids.ETERNAL_FLUID, ModFluids.FLOWING_ETERNAL_FLUID, new Identifier("dimdoors:eternal_fluid"));

        OpenWorlds.registerSkyRenderer(ModDimensions.POCKET_TYPE, new CustomSkyProvider(null, null, new Vec3i(0, 0, 0)));
        OpenWorlds.registerSkyRenderer(ModDimensions.LIMBO_TYPE, new LimboSkyProvider());

        ClientSidePacketRegistry.INSTANCE.register(DimensionalDoorsInitializer.MONOLITH_PARTICLE_PACKET, MonolithEntity::spawnParticles);
    }

    private void putCutout(Block block) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
    }

    public static void setupFluidRendering(final Fluid still, final Fluid flowing, final Identifier textureFluidId) {
        final Identifier stillSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_still");
        final Identifier flowingSpriteId = new Identifier(textureFluidId.getNamespace(), "block/" + textureFluidId.getPath() + "_flow");

        // If they're not already present, add the sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(stillSpriteId);
            registry.register(flowingSpriteId);
        });

        final Identifier fluidId = Registry.FLUID.getId(still);
        final Identifier listenerId = new Identifier(fluidId.getNamespace(), fluidId.getPath() + "_reload_listener");

        final Sprite[] fluidSprites = { null, null };

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId()
            {
                return listenerId;
            }

            /**
             * Get the sprites from the block atlas when resources are reloaded
             */
            @Override
            public void apply(ResourceManager resourceManager)
            {
                final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                fluidSprites[0] = atlas.apply(stillSpriteId);
                fluidSprites[1] = atlas.apply(flowingSpriteId);
            }
        });

        // The FluidRenderer gets the sprites and color from a FluidRenderHandler during rendering
        final FluidRenderHandler renderHandler = new FluidRenderHandler() {
            @Override
            public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state)
            {
                return fluidSprites;
            }

            @Override
            public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state)
            {
                return 16777215;
            }
        };

        FluidRenderHandlerRegistry.INSTANCE.register(still, renderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(flowing, renderHandler);
    }

}

package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient;
import org.dimdev.dimdoors.compat.iris.IrisCompat;
import org.dimdev.dimdoors.entity.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.network.packet.c2s.NetworkHandlerInitializedC2SPacket;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.dimdev.dimdoors.particle.ModParticleTypes.*;

public class DimensionalDoorsClient {
    public static final ResourceLocation childItem = DimensionalDoors.id("item/child_item");

    public static ShaderPackDetector detector = consumer -> consumer.accept(DimensionalPortalRenderer.RENDER_LAYER);
    private static IClientSided sided;

    public static void init(IClientSided sided) {
        DimensionalDoorsClient.sided = sided;
        sided.onClientPlayerJoin(() -> ClientPacketListener.sendPacket(new NetworkHandlerInitializedC2SPacket()));
        registerCompats();

//    ModFluids.initClient();

        EnvironmentAddonClient.init();

        registerListeners();

//    ModRecipeBookGroups.init();
    }

    private static void registerCompats() {
        if (DimensionalDoors.getSided().isModLoaded("iris") || DimensionalDoors.getSided().isModLoaded("oculus")) detector = new IrisCompat();
    }

    public static void initEntitiesClient(BiConsumer<EntityType, EntityRendererProvider> consumer, BiConsumer<BlockEntityType, BlockEntityRendererProvider> blockConsumer) {
        consumer.accept(ModEntityTypes.MONOLITH, MonolithRenderer::new);
        consumer.accept(ModEntityTypes.MASK, context -> new EntityRenderer<MaskEntity>(context) {
            @Override
            public ResourceLocation getTextureLocation(MaskEntity entity) {
                return ResourceLocation.parse("blep");
            }
        });

        blockConsumer.accept(ModBlockEntityTypes.ENTRANCE_RIFT, EntranceRiftBlockEntityRenderer::new);
        blockConsumer.accept(ModBlockEntityTypes.DETACHED_RIFT, DetachedRiftBlockEntityRenderer::new);
    }

    public static void initGeneratedDoorCutouts() {
        DimensionalDoorBlockRegistrar registrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
        if (registrar == null) {
            return;
        }

        Block[] generatedBlocks = registrar.getGennedIds().stream()
                .filter(BuiltInRegistries.BLOCK::containsKey)
                .map(BuiltInRegistries.BLOCK::get)
                .toArray(Block[]::new);
        if (generatedBlocks.length > 0) {
            getClientSided().register(RenderType.cutout(), generatedBlocks);
        }
    }

    public static void initClient() {
        DimensionalDoorsClient.getClientSided().register(RenderType.cutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR, ModBlocks.DRIFTWOOD_LEAVES, ModBlocks.DRIFTWOOD_SAPLING, ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.UNRAVELED_SPIKE, ModBlocks.DRIFTWOOD_DOOR);
    }

    private static void registerListeners() {
        sided.registerCoreShader(DimensionalDoors.id("dimensional_portal"), DefaultVertexFormat.POSITION, ModShaders::setDimensionalPortal);
    }

    public static void initParticles(BiConsumer<ParticleType<? extends ParticleOptions>, ParticleProvider<?>> specialProvider, BiConsumer<ParticleType<?>, Function<SpriteSet, ? extends ParticleProvider<? extends ParticleOptions>>> spriteProivder) {
        specialProvider.accept(MONOLITH, (particleOptions, clientLevel, x, y, z, g, h, i) -> new MonolithParticle(clientLevel, x, y, z));
        spriteProivder.accept(RIFT, RiftParticle.Factory::new);
        spriteProivder.accept(LIMBO_ASH, LimboAshParticle.Factory::new);
    }


    public static IClientSided getClientSided() {
        return sided;
    }
}

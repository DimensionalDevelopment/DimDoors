package org.dimdev.dimdoors.client.neoforge;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NeoforgeMultiplatformData {
    public interface Particle {
        void register(RegisterParticleProvidersEvent event);

        public record Direct<T extends ParticleOptions>(ParticleType<T> type, ParticleProvider<T> provider) implements Particle {
            public void register(RegisterParticleProvidersEvent event) {
                event.registerSpecial(type, provider);
            }
        }

        public record InDirect<T extends ParticleOptions>(ParticleType<T> type, ParticleEngine.SpriteParticleRegistration<T> provider) implements Particle {
            public void register(RegisterParticleProvidersEvent event) {
                event.registerSpriteSet(type, provider);
            }
        }
    }

    public record ScreenInfo<T extends AbstractContainerMenu, M extends Screen & MenuAccess<T>>(MenuType<T> menuType, MenuScreens.ScreenConstructor<T, M> constructor) {
        public void register(RegisterMenuScreensEvent event) {
            event.register(menuType, constructor);
        }
    }

    public interface RendererProvider {
        void register(EntityRenderersEvent.RegisterRenderers event);

        record EntityProvider<T extends Entity>(EntityType<T> type, EntityRendererProvider<T> provider) implements RendererProvider {
            @Override
            public void register(EntityRenderersEvent.RegisterRenderers event) {
                event.registerEntityRenderer(type, provider);
            }
        }

        record BlockEntityProvider<T extends BlockEntity>(BlockEntityType<T> type, BlockEntityRendererProvider<T> provider) implements RendererProvider {
            @Override
            public void register(EntityRenderersEvent.RegisterRenderers event) {
                event.registerBlockEntityRenderer(type, provider);
            }
        }
    }

    public record FluidExtension(ResourceLocation flowing, ResourceLocation still, ResourceLocation overlay) implements IClientFluidTypeExtensions {
        public FluidExtension(ModFluids.FluidDetails attributes) {
            this(attributes.flowing(), attributes.still(), attributes.overlay());
        }

        @Override
        public ResourceLocation getFlowingTexture() {
            return flowing;
        }

        @Override
        public @Nullable ResourceLocation getOverlayTexture() {
            return overlay;
        }

        @Override
        public ResourceLocation getStillTexture() {
            return still;
        }
    }

    public record ShaderInfo(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> callback) {
        public void register(ResourceProvider provider, BiConsumer<ShaderInstance, Consumer<ShaderInstance>> consumer) throws IOException {
            consumer.accept(new ShaderInstance(provider, id, vertexFormat), callback);
        }
    }

    public record ModelLayerInfo(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
    }

    public record DimensionEffect(ResourceLocation id, DimensionSpecialEffects effects) {
    }
}

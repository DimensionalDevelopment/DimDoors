package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.api.util.function.TriFunction;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.dimdev.dimdoors.screen.TessellatingContainer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ModClient<T extends IClientSided<?>> {
    void init(T sided);

    default void initParticles(RegularParticleRegister regularParticleRegister, SpecialParticleRegister specialParticleRegister) {}

    default void initFluids(TriConsumer<FlowingFluid, Fluid, ModFluids.FluidDetails> register) {}

    default void initScreens(ScreenRegister screenRegister) {}

    default void initBlockEntityRenderers(BlockEntityRegister register) {}

    default void initEntityRenderers(EntityRegister register) {}

    default void initModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {}

    default void initModels(BiConsumer<ModelResourceLocation, Consumer<ModelLoadingRegistry>> consumer) {}

    default void initDimensionEffects(BiConsumer<ResourceLocation, DimensionSpecialEffects> effectsRegister) {}

    default void initShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> shaderRegister) {}

    default void delayedInit() {}

    interface EntityRegister {
        <T extends Entity> void register(EntityType<T> type, EntityRendererProvider<T> provider);
    }

    interface BlockEntityRegister {
        <T extends BlockEntity> void register(BlockEntityType<T> type, BlockEntityRendererProvider<T> provider);
    }

    @FunctionalInterface
    interface ScreenRegister {
        <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(MenuType<U> menuType, TriFunction<U, Inventory, Component, M> factory);
    }

    interface SpecialParticleRegister {
        <P extends ParticleOptions> void register(ParticleType<P> particleType, ParticleProvider<P> provider);
    }

    interface RegularParticleRegister {
        <P extends ParticleOptions> void register(ParticleType<P> particleType, Function<SpriteSet, ParticleProvider<P>> provider);
    }
}

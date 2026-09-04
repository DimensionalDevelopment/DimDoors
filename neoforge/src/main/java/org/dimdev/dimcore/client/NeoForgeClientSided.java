package org.dimdev.dimcore.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;
import org.dimdev.dimcore.NeoforgeResourceLoader;
import org.dimdev.dimcore.api.client.IClientSided;
import org.dimdev.dimcore.api.client.ModClient;
import org.dimdev.dimcore.api.fluid.FluidDetails;
import org.dimdev.dimcore.api.util.function.TriFunction;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class NeoForgeClientSided<V extends NeoForgeClientSided<V, T>, T extends ModClient<? super V>> implements IClientSided<V> {
	private final T client;
    private final List<Runnable> loginRunnables = new ArrayList<>();
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	private final List<Pair<ResourceLocation, Consumer<ResourceManager>>> loaders = new ArrayList<>();
    private List<KeyMapping> keyMappings = new ArrayList<>();


    public NeoForgeClientSided(IEventBus bus, ModContainer container, T client) {
		this.client = client;
		client.init(self());

        bus.<RegisterParticleProvidersEvent>addListener(event -> client.initParticles(new ModClient.RegularParticleRegister() {
            @Override
            public <P extends ParticleOptions> void register(ParticleType<P> particleType, Function<SpriteSet, ParticleProvider<P>> provider) {
                event.registerSpriteSet(particleType, provider::apply);
            }
        }, event::registerSpecial));

        bus.<RegisterMenuScreensEvent>addListener(event -> client.initScreens(new ModClient.ScreenRegister() {
            @Override
            public <U extends AbstractContainerMenu, M extends Screen & MenuAccess<U>> void register(MenuType<U> menuType, TriFunction<U, Inventory, Component, M> factory) {
                event.register(menuType, factory::apply);
            }
        }));
        bus.<RegisterClientExtensionsEvent>addListener(event -> client.initFluids((flowing, fluid, details) -> event.registerFluidType(new FluidExtension(details), fluid.getFluidType())));
        bus.<EntityRenderersEvent.RegisterRenderers>addListener(event -> {
            client.initEntityRenderers(event::registerEntityRenderer);
            client.initBlockEntityRenderers(event::registerBlockEntityRenderer);
        });
        bus.<EntityRenderersEvent.RegisterLayerDefinitions>addListener(event -> client. initModelLayers(event::registerLayerDefinition));

        if(!keyMappings.isEmpty()) bus.<RegisterKeyMappingsEvent>addListener(event -> keyMappings.forEach(event::register));

		bus.addListener(this::addReloaders);
        bus.<RegisterDimensionSpecialEffectsEvent>addListener(event -> client.initDimensionEffects(event::register));
        bus.<FMLClientSetupEvent>addListener(event -> event.enqueueWork(client::delayedInit));
        bus.<RegisterShadersEvent>addListener(event -> {
            var provider = event.getResourceProvider();

            client.initShaders((id, vertexFormat, consumer) -> {
                try {
                    event.registerShader(new ShaderInstance(provider, id, vertexFormat), consumer);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        NeoForge.EVENT_BUS.<ClientPlayerNetworkEvent.LoggingIn>addListener(event -> loginRunnables.forEach(Runnable::run));
    }

    @Override
    public void register(RenderType type, Block... blocks) {
        for (Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, type);
        }
    }

    @Override
    public void onClientPlayerJoin(Runnable listener) {
        loginRunnables.add(listener);
    }

	@Override
	public void registerClientLoader(String name, Consumer<ResourceManager> consumer) {
		loaders.add(Pair.of(ResourceLocation.fromNamespaceAndPath(client.getModId(), name), consumer));
	}

	public void addReloaders(RegisterClientReloadListenersEvent event) {
		loaders.forEach(pair -> event.registerReloadListener(new NeoforgeResourceLoader.Client(pair.getLeft(), pair.getValue())));
	}

	public record FluidExtension(ResourceLocation flowing, ResourceLocation still, ResourceLocation overlay) implements IClientFluidTypeExtensions {
        public FluidExtension(FluidDetails attributes) {
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

    @Override
    public void registerKeyBinding(KeyMapping mapping) {
        keyMappings.add(mapping);
    }
}

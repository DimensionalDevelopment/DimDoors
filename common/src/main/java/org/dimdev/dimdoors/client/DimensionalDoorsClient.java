package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.imgui.api.ImGuiMCEvents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.api.client.DimensionalPortalRenderer;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.block.door.DimensionalDoorBlockRegistrar;
import org.dimdev.dimdoors.block.entity.ModBlockEntityTypes;
import org.dimdev.dimdoors.client.effect.DungeonDimensionEffect;
import org.dimdev.dimdoors.client.effect.LimboDimensionEffect;
import org.dimdev.dimdoors.client.effect.sky.EnvironmentAddonClient;
import org.dimdev.dimdoors.client.screen.TesselatingLoomScreen;
import org.dimdev.dimdoors.compat.imgui.PortalColorGui;
import org.dimdev.dimdoors.compat.iris.IrisCompat;
import org.dimdev.dimdoors.entity.MaskEntity;
import org.dimdev.dimdoors.entity.ModEntityTypes;
import org.dimdev.dimdoors.fluid.ModFluids;
import org.dimdev.dimdoors.item.ModItems;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.dimdev.dimdoors.particle.client.LimboAshParticle;
import org.dimdev.dimdoors.particle.client.MonolithParticle;
import org.dimdev.dimdoors.particle.client.RiftParticle;
import org.dimdev.dimdoors.rift.RiftUtils;
import org.dimdev.dimdoors.screen.ModScreenHandlerTypes;
import org.dimdev.dimcore.api.client.ActionKeyMapping;
import org.dimdev.dimcore.api.client.ModClient;

import org.dimdev.dimcore.api.fluid.FluidDetails;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.dimdev.dimdoors.particle.ModParticleTypes.*;

public class DimensionalDoorsClient implements ModClient<IDimDoorsClientSided<?>> {

    public static final DimensionalDoorsClient INSTANCE = new DimensionalDoorsClient();

    public static ShaderPackDetector detector = consumer -> consumer.accept(DimensionalPortalRenderer.VANILLA_DIMENSIONAL_PORTAL_RENDER_LAYER);
    private static IDimDoorsClientSided<?> sided;
    private float renderTick;

    public void init(IDimDoorsClientSided<?> sided) {
        setClientSided(sided);
        sided.onClientPlayerJoin(ClientPacketListener::clearPocketAddons);
        registerCompats();
        EnvironmentAddonClient.init();

        sided.onPreRender(this::preRender);

        if(DimensionalDoors.getSided().isModLoaded("imguimc")) {
            sided.registerKeyBinding(new ActionKeyMapping("key.dimdoors.portal_colors_editor", GLFW.GLFW_KEY_N, "key.categories.dimdoors", PortalColorGui::toggle));

            ImGuiMCEvents.INSTANCE.preRenderImGuiEvent(PortalColorGui::render);
        }
    }

    @Override
    public String getModId() {
        return DimensionalDoors.MOD_ID;
    }

    @Override
    public void initParticles(RegularParticleRegister regularParticleRegister, SpecialParticleRegister specialParticleRegister) {
        specialParticleRegister.register(MONOLITH, new ParticleProvider<>() {
            @Override
            public @NotNull Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double x, double y, double z, double g, double h, double i) {
                return new MonolithParticle(clientLevel, x, y, z);
            }
        });
        regularParticleRegister.register(RIFT, RiftParticle.Factory::new);
        regularParticleRegister.register(LIMBO_ASH, LimboAshParticle.Factory::new);
    }

    @Override
    public void initDimensionEffects(BiConsumer<ResourceLocation, DimensionSpecialEffects> effectsRegister) {
        effectsRegister.accept(DimensionalDoors.id("limbo"), sided.createVoidEffect(LimboDimensionEffect.INSTANCE));
        effectsRegister.accept(DimensionalDoors.id("dungeon"), sided.createVoidEffect(DungeonDimensionEffect.INSTANCE));
    }

    @Override
    public void initBlockEntityRenderers(BlockEntityRegister register) {
        register.register(ModBlockEntityTypes.ENTRANCE_RIFT, EntranceRiftBlockEntityRenderer::new);
        register.register(ModBlockEntityTypes.DETACHED_RIFT, DetachedRiftBlockEntityRenderer::new);
        register.register(ModBlockEntityTypes.DIALING_DOOR, DialingDoorBlockEntityRenderer::new);
    }

    @Override
    public void initShaders(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> shaderRegister) {
        shaderRegister.accept(DimensionalDoors.id("dimensional_portal"), DefaultVertexFormat.POSITION, ModShaders::setDimensionalPortal);
    }

    @Override
    public void initItemProperties(TriConsumer<Item, ResourceLocation, ClampedItemPropertyFunction> consumer) {
        consumer.accept(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("pull"), (stack, level, entity, p_351685_) -> entity == null ? 0.0F : CrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(stack, entity));
        consumer.accept(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("pulling"), (p_174605_, p_174606_, p_174607_, p_174608_) -> p_174607_ != null && p_174607_.isUsingItem() && p_174607_.getUseItem() == p_174605_ && !CrossbowItem.isCharged(p_174605_) ? 1.0F : 0.0F);
        consumer.accept(ModItems.FARSHOT, ResourceLocation.withDefaultNamespace("charged"), (p_275891_, p_275892_, p_275893_, p_275894_) -> CrossbowItem.isCharged(p_275891_) ? 1.0F : 0.0F);
    }

    @Override
    public void initModelLayers(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        consumer.accept(ModEntityModelLayers.MONOLITH, MonolithModel::getTexturedModelData);
    }

    public void preRender(long ticks, float deltaTick) {
        renderTick = ticks + deltaTick;
        RiftUtils.updateRiftCoreRenderTime(ticks, deltaTick);
    }

    @Override
    public void initFluids(TriConsumer<FlowingFluid, Fluid, FluidDetails> register) {
        register.accept(ModFluids.LEAK, ModFluids.FLOWING_LEAK, ModFluids.LEAK_DETAILS);
        register.accept(ModFluids.ETERNAL_FLUID, ModFluids.FLOWING_ETERNAL_FLUID, ModFluids.ETERNAL_FLUID_DETAILS);
    }

    @Override
    public void initScreens(ScreenRegister screenRegister) {
        screenRegister.register(ModScreenHandlerTypes.TESSELATING_LOOM, TesselatingLoomScreen::new);
    }

    @Override
    public void initEntityRenderers(EntityRegister register) {
        register.register(ModEntityTypes.MONOLITH, MonolithRenderer::new);
        register.register(ModEntityTypes.MASK, context -> new EntityRenderer<>(context) {
            @Override
            public @NotNull ResourceLocation getTextureLocation(@NotNull MaskEntity entity) {
                return ResourceLocation.parse("blep");
            }
        });
        register.register(ModEntityTypes.FARSHOT_ENDER_PEARL, FarShotEnderPearlRenderer::new);
    }

    private static void registerCompats() {
        if (DimensionalDoors.getSided().isModLoaded("iris") || DimensionalDoors.getSided().isModLoaded("oculus")) detector = new IrisCompat();
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

    public void delayedInit() {
        initGeneratedDoorCutouts();
        sided.register(RenderType.cutout(), ModBlocks.QUARTZ_DOOR, ModBlocks.GOLD_DOOR, ModBlocks.DRIFTWOOD_LEAVES, ModBlocks.DRIFTWOOD_SAPLING, ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DRIFTWOOD_TRAPDOOR, ModBlocks.UNRAVELED_SPIKE, ModBlocks.DRIFTWOOD_DOOR, ModBlocks.DIALING_DOOR);
    }

    public static IDimDoorsClientSided<?> getClientSided() {
        return sided;
    }

    public static void setClientSided(IDimDoorsClientSided<?> sided) {
        DimensionalDoorsClient.sided = sided;
    }

    public float getRenderTick() {
        return renderTick;
    }
}

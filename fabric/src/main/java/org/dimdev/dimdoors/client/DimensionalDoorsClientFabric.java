package org.dimdev.dimdoors.client;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.item.ItemStack;
import org.dimdev.dimdoors.client.effect.DimensionEffect;
import org.dimdev.dimdoors.client.effect.VoidDimensionSpecialEffects;
import org.dimdev.dimcore.client.FabricClientSided;
import org.dimdev.dimcore.client.IDimensionSpecialEffectExtension;
import org.joml.Matrix4f;

import java.util.function.Supplier;


public class DimensionalDoorsClientFabric extends FabricClientSided<DimensionalDoorsClientFabric, DimensionalDoorsClient> implements IDimDoorsClientSided<DimensionalDoorsClientFabric> {
    public DimensionalDoorsClientFabric() {
        super(DimensionalDoorsClient.INSTANCE);
    }

    @Override
    public void onInitializeClient() {
        super.onInitializeClient();
        RecipeBookManager.init();
        checkCompat();
        ModelLoadingPlugin.register(new GeneratedDoorModelCopyPlugin());
    }

    @Override
    public Supplier<RecipeBookCategories> getRecipBookCategories(String name, Supplier<ItemStack> itemStack) {
        return () -> ClassTinkerers.getEnum(RecipeBookCategories.class, name);
    }

    @Override
    public VoidDimensionSpecialEffects createVoidEffect(DimensionEffect effect) {
        return new FabricVoidDimensionSpecialEffects(effect);
    }

    @Override
    public void onPreRender(PreRender onPrerender) {
        WorldRenderEvents.START.register(context -> {
            var level = context.world();

            if(level == null) return;

            onPrerender.preRender(level.getGameTime(), context.tickCounter().getGameTimeDeltaPartialTick(false));
        });
    }

    private static class FabricVoidDimensionSpecialEffects extends VoidDimensionSpecialEffects implements IDimensionSpecialEffectExtension {
        private final DimensionEffect effect;

        public FabricVoidDimensionSpecialEffects(DimensionEffect effect) {
            super();
            this.effect = effect;
        }

        @Override
        public boolean extRenderSky(ClientLevel level, int ticks, float partialTick, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
            return effect.renderSky(level, ticks, partialTick, modelViewMatrix, camera, projectionMatrix, isFoggy, setupFog);
        }

        @Override
        public boolean extRenderClouds(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
            return effect.renderClouds(level, ticks, partialTick, poseStack, camX, camY, camZ, modelViewMatrix, projectionMatrix);
        }

        @Override
        public boolean extRenderWeather(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
            return effect.renderWeather(level, ticks, partialTick, lightTexture, camX, camY, camZ);
        }
    }

//    public static final class GeneratedDoorModelCopyPlugin implements ModelLoadingPlugin {
//        @Override
//        public void onInitializeModelLoader(Context context) {
//            var registrar = DimensionalDoors.getDimensionalDoorBlockRegistrar();
//
//            var blocks = registrar.getGennedIds().stream()
//                    .map(BuiltInRegistries.BLOCK::get)
//                    .filter(block -> block instanceof TraversableRiftBlock<?>)
//                    .toList();
//
//            var ids = blocks.stream()
//                    .flatMap(block -> {
//                        var rift = (TraversableRiftBlock<?>) block;
//                        var original = rift.getVisualBlockState(block.defaultBlockState()).getBlock();
//
//                        return Stream.concat(
//                                block.getStateDefinition().getPossibleStates().stream()
//                                        .map(state -> Map.entry(
//                                                BlockModelShaper.stateToModelLocation(state),
//                                                BlockModelShaper.stateToModelLocation(rift.getVisualBlockState(state))
//                                        )),
//                                Stream.of(Map.entry(
//                                        ModelResourceLocation.inventory(block.asItem().builtInRegistryHolder().key().location()),
//                                        ModelResourceLocation.inventory(original.asItem().builtInRegistryHolder().key().location())
//                                ))
//                        );
//                    })
//                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//            blocks.forEach(block ->
//                    context.registerBlockStateResolver(block, ctx -> {
//                        var model = ctx.getOrLoadModel(
//                                ResourceLocation.withDefaultNamespace("block/air")
//                        );
//
//                        block.getStateDefinition().getPossibleStates()
//                                .forEach(state -> ctx.setModel(state, model));
//                    })
//            );
//
//            var itemIds = ids.entrySet().stream()
//                    .filter(e -> ModelResourceLocation.INVENTORY_VARIANT.equals(e.getKey().variant()))
//                    .collect(Collectors.toMap(
//                            e -> e.getKey().id().withPrefix("item/"),
//                            e -> e.getValue().id().withPrefix("item/")
//                    ));
//
//            context.resolveModel().register(ctx -> {
//                var original = itemIds.get(ctx.id());
//                return original == null ? null : ctx.getOrLoadModel(original);
//            });
//
//            var originals = new HashSet<>(ids.values());
//            var cache = new HashMap<ModelResourceLocation, BakedModel>();
//
//            context.modifyModelAfterBake().register((model, ctx) -> {
//                var id = ctx.topLevelId();
//                if (id == null) return model;
//
//                var originalId = ids.get(id);
//
//                if (originalId != null) {
//                    var originalModel = ctx.loader().getBakedTopLevelModels().get(originalId);
//                    return originalModel != null ? originalModel : model;
//                }
//
//                ids.forEach((generatedId, sourceId) -> {
//                    if (sourceId.equals(id)) {
//                        ctx.loader().getBakedTopLevelModels().put(generatedId, model);
//                    }
//                });
//
//                return model;
//            });
//        }
//    }
}

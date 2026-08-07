package org.dimdev.dimdoors.client;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class GeneratedDoorBakedModel implements BakedModel {
    private final ModelResourceLocation sourceId;
    private final boolean portal;

    public GeneratedDoorBakedModel(
            ModelResourceLocation sourceId,
            boolean portal
    ) {
        this.sourceId = sourceId;
        this.portal = portal;
    }

    private BakedModel source() {
        return Minecraft.getInstance()
                .getModelManager()
                .getModel(sourceId);
    }

    private BakedModel portal() {
        return Minecraft.getInstance()
                .getModelManager()
                .getModel(GeneratedDoorModelMappings.PORTAL_ITEM_MODEL);
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction direction,
            RandomSource random
    ) {
        var source = source();

        if (!portal) {
            return source.getQuads(state, direction, random);
        }

        var sourceQuads = source.getQuads(state, direction, random);
        var portalQuads = portal().getQuads(state, direction, random);

        if (portalQuads.isEmpty()) {
            return sourceQuads;
        }

        var quads = new ArrayList<BakedQuad>(
                sourceQuads.size() + portalQuads.size()
        );

        quads.addAll(portalQuads);
        quads.addAll(sourceQuads);

        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return source().useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return source().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return source().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return source().isCustomRenderer();
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return source().getParticleIcon();
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return source().getTransforms();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return source().getOverrides();
    }

    @Override
    public boolean isVanillaAdapter() {
        return !portal && source().isVanillaAdapter();
    }

    @Override
    public void emitBlockQuads(
            BlockAndTintGetter blockView,
            BlockState state,
            BlockPos pos,
            Supplier<RandomSource> randomSupplier,
            RenderContext context
    ) {
        source().emitBlockQuads(
                blockView,
                state,
                pos,
                randomSupplier,
                context
        );
    }

    @Override
    public void emitItemQuads(
            ItemStack stack,
            Supplier<RandomSource> randomSupplier,
            RenderContext context
    ) {
        source().emitItemQuads(
                stack,
                randomSupplier,
                context
        );

        if (portal) {
            portal().emitItemQuads(
                    stack,
                    randomSupplier,
                    context
            );
        }
    }
}
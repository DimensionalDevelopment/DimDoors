package org.dimdev.dimdoors.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.dimdev.dimdoors.DimensionalDoors;
import org.dimdev.dimdoors.entity.MonolithEntity;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonolithRenderer extends MobRenderer<MonolithEntity, MonolithRenderState, MonolithModel> {
    public static final List<Identifier> TRANSPARENT = Stream.of(
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_0.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_1.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_2.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_3.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_4.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_5.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_6.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_7.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_8.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_9.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_10.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_11.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_12.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_13.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_14.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_15.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_16.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_17.png"),
            DimensionalDoors.id("textures/mob/monolith/transparent/monolith_18.png")
    ).collect(Collectors.toList());

    public static final List<Identifier> SOLID = Stream.of(
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_0.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_1.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_2.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_3.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_4.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_5.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_6.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_7.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_8.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_9.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_10.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_11.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_12.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_13.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_14.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_15.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_16.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_17.png"),
            DimensionalDoors.id("textures/mob/monolith/solid/monolith_18.png")
    ).collect(Collectors.toList());

    private static MonolithModel INSTANCE;

    public MonolithRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, INSTANCE = new MonolithModel(ctx), 0);
    }

    public static MonolithModel getInstance() {
        return INSTANCE;
    }

    @Override
    protected boolean shouldShowName(@NonNull MonolithEntity entity, double distanceToCameraSq) {
        return false;
    }

    @Override
    public @NonNull Identifier getTextureLocation(MonolithRenderState state) {
        return (state.isSolid ? SOLID : TRANSPARENT).get(state.textureState);
    }

    @Override
    public @NonNull MonolithRenderState createRenderState() {
        return new MonolithRenderState();
    }

    @Override
    public void extractRenderState(@NonNull MonolithEntity entity, @NonNull MonolithRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSolid = entity.getSolid();
        state.textureState = entity.getTextureState();
        state.id = entity.getId();
        state.aggro = entity.getAggro();
    }
}

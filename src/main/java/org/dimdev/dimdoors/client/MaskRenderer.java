package org.dimdev.dimdoors.client;

import net.minecraft.client.render.entity.EntityRendererFactory;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.dimdev.dimdoors.entity.AbstractMaskEntity;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

@Environment(EnvType.CLIENT)
public class MaskRenderer extends GeoEntityRenderer<AbstractMaskEntity> {
    public MaskRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new MaskModel());
        this.shadowRadius = 0.7f;
    }
}

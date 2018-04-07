package org.dimdev.dimdoors.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.dimdoors.shared.entities.EntityMonolith;

@SideOnly(Side.CLIENT)
public class ModelMonolith extends ModelBase {

    private final ModelRenderer monolith;

    public ModelMonolith() {
        textureWidth = 256;
        textureHeight = 256;

        monolith = new ModelRenderer(this, 0, 0);
        monolith.addBox(-24F, -108F / 1.3F, -6F, 48, 108, 12);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        EntityMonolith monolith = (EntityMonolith) entity;

        setRotationAngles(0, 0, 0, 0, 0, 0, monolith);
        GlStateManager.scale(monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier());
        this.monolith.render(scale);
    }
}

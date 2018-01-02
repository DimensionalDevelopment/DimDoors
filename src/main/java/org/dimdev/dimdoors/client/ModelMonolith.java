package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.shared.entities.EntityMonolith;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelMonolith extends ModelBase {

    private final ModelRenderer wholeMonolith;

    public ModelMonolith() {
        textureWidth = 256;
        textureHeight = 256;

        wholeMonolith = new ModelRenderer(this, 0, 0);
        wholeMonolith.addBox(-24F, -108F / 1.3F, -6F, 48, 108, 12);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        EntityMonolith monolith = (EntityMonolith) entityIn;

        setRotationAngles(0, 0, 0, 0, 0, 0, monolith);
        GL11.glScalef(monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier());
        wholeMonolith.render(scale);
    }
}

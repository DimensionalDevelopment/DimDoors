package com.zixiken.dimdoors.shared.entities;


import java.util.Random;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelMobObelisk extends ModelBase {
    ModelRenderer wholemonolith;
    Random rand = new Random();

    public ModelMobObelisk() {
        textureWidth = 256;
        textureHeight = 256;

        wholemonolith = new ModelRenderer(this, 0, 0);
        wholemonolith.addBox(-24F,-108F/1.3F, -6F, 48, 108, 12);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(0,  0,  0,  0,  0,0,  entity);

        GL11.glScalef(((MobMonolith) entity).getRenderSizeModifier(), ((MobMonolith) entity).getRenderSizeModifier(), ((MobMonolith) entity).getRenderSizeModifier());
        wholemonolith.render(scale);
    }
}
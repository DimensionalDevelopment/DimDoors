//package org.dimdev.dimdoors.client;
//
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.model.Model;
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.client.model.ModelPart;
//import net.minecraft.client.model.ModelRenderer;
//import net.minecraft.client.renderer.RenderSystem;
//import net.minecraft.entity.Entity;
//import org.dimdev.dimdoors.entity.MonolithEntity;
//
//@Environment(EnvType.CLIENT)
//public class MonolithModel extends Model {
//    private final ModelPart body;
//
//    public MonolithModel() {
//        super();
//        textureWidth = 256;
//        textureHeight = 256;
//
//        body = new ModelPart(this, 0, 0);
//        body.addBox(-24F, -108F / 1.3F, -6F, 48, 108, 12);
//    }
//
//    @Override
//    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        MonolithEntity monolith = (MonolithEntity) entity;
//
//        setRotationAngles(0, 0, 0, 0, 0, 0, monolith);
//        RenderSystem.scale(monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier(), monolith.getRenderSizeModifier());
//        this.body.render(scale);
//    }
//}

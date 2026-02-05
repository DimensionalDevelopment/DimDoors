package org.dimdev.dimdoors.client.tesseract;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.renderer.LightTexture;
import org.dimdev.dimdoors.api.util.RGBA;
import org.joml.Matrix4f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class Plane {
    Vector4f[] vectors;
    private  static final Vector4f scratch = new Vector4f();

    public Plane(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        this.vectors = new Vector4f[]{vec1, vec2, vec3, vec4};
    };

    public void draw(org.joml.Matrix4f model, Matrix4f matrix4D, VertexConsumer vc, RGBA color) {
        drawVertex(model, vc, matrix4D.transform(this.vectors[0], scratch), 0, 0, color);
        drawVertex(model, vc, matrix4D.transform(this.vectors[1], scratch), 0, 1, color);
        drawVertex(model, vc, matrix4D.transform(this.vectors[2], scratch), 1, 1, color);
        drawVertex(model, vc, matrix4D.transform(this.vectors[3], scratch), 1, 0, color);
    }

    private static void drawVertex(org.joml.Matrix4f model, VertexConsumer vc, Vector4f vector, int u, int v, RGBA color) {
        float scalar = 1f / (vector.w() + 1);
        vector.mul(scalar);;
        vc.addVertex(model, vector.x(), vector.y(), vector.z())
                .setColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .setUv(u, v)
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0,0, 0);
    }


}

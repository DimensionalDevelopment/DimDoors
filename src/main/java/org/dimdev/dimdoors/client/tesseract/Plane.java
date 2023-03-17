package org.dimdev.dimdoors.client.tesseract;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.math.matrix.Matrix4f;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector4f;

import org.dimdev.dimdoors.api.util.RGBA;

import static org.spongepowered.math.TrigMath.cos;
import static org.spongepowered.math.TrigMath.sin;

@OnlyIn(Dist.CLIENT)
public class Plane {
    Vector4f[] vectors;

    public Plane(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        this.vectors = new Vector4f[]{vec1, vec2, vec3, vec4};
    }

    public void draw(org.joml.Matrix4f model, VertexConsumer vc, RGBA color, double radian) {
        drawVertex(model, vc, rotYW(this.vectors[0], radian), 0, 0, color);
        drawVertex(model, vc, rotYW(this.vectors[1], radian), 0, 1, color);
        drawVertex(model, vc, rotYW(this.vectors[2], radian), 1, 1, color);
        drawVertex(model, vc, rotYW(this.vectors[3], radian), 1, 0, color);
    }

    private static void drawVertex(org.joml.Matrix4f model, VertexConsumer vc, Vector4f vector, int u, int v, RGBA color) {
        double scalar = 1d / (vector.w() + 1);
        Vector3f scaled = vector.toVector3().mul(scalar);
        vc.vertex(model, scaled.x(), scaled.y(), scaled.z())
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .uv(u, v)
                .endVertex();
    }

    private static Vector4f rotXW(Vector4f v, double angle) {
        return Matrix4f.from(
                cos(angle), 0, 0, sin(angle),
                0, 1, 0, 0,
                0, 0, 1, 0,
                -sin(angle), 0, 0, cos(angle))
                .transform(v);
    }

    private static Vector4f rotZW(Vector4f v, double angle) {
        return Matrix4f.from(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, cos(angle), -sin(angle),
                0, 0, sin(angle), cos(angle))
                .transform(v);
    }

    private static Vector4f rotYW(Vector4f v, double angle) {
        return Matrix4f.from(
                1, 0, 0, 0,
                0, cos(angle), 0, sin(angle),
                0, 0, 1, 0,
                0, -sin(angle), 0, cos(angle))
                .transform(v);
    }

    private static Vector4f rotXY(Vector4f v, double angle) {
        return Matrix4f.from(
                cos(angle), -sin(angle), 0, 0,
                sin(angle), cos(angle), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1)
                .transform(v);
    }
}

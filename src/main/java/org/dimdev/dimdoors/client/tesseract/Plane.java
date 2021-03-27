package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import org.dimdev.dimdoors.api.util.RGBA;

import net.minecraft.client.render.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

@Environment(EnvType.CLIENT)
public class Plane {
    Vector4f[] vectors;

    public Plane(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        this.vectors = new Vector4f[]{vec1, vec2, vec3, vec4};
    }

    public void draw(net.minecraft.util.math.Matrix4f model, VertexConsumer vc, RGBA color, double radian) {
        drawVertex(model, vc, rotYW(this.vectors[0], radian), 0, 0, color);
        drawVertex(model, vc, rotYW(this.vectors[1], radian), 0, 1, color);
        drawVertex(model, vc, rotYW(this.vectors[2], radian), 1, 1, color);
        drawVertex(model, vc, rotYW(this.vectors[3], radian), 1, 0, color);
    }

    private static void drawVertex(net.minecraft.util.math.Matrix4f model, VertexConsumer vc, Vector4f vector, int u, int v, RGBA color) {
        double scalar = 1d / (vector.getW() + 1);
        Vector3f scaled = vector.toVector3().mul(scalar);
        vc.vertex(model, scaled.getX(), scaled.getY(), scaled.getZ())
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .texture(u, v)
                .next();
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

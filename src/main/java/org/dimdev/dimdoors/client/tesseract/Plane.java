package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.render.VertexConsumer;

import static com.flowpowered.math.TrigMath.*;

public class Plane {
    Vector4f[] vectors;

    public Plane(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        vectors = new Vector4f[]{vec1, vec2, vec3, vec4};
    }

    public void draw(VertexConsumer vc, float[] color, double radian) {
        drawVertex(vc, rotYW(vectors[0], radian), 0, 0, color);
        drawVertex(vc, rotYW(vectors[1], radian), 0, 1, color);
        drawVertex(vc, rotYW(vectors[2], radian), 1, 1, color);
        drawVertex(vc, rotYW(vectors[3], radian), 1, 0, color);
    }

    private static void drawVertex(VertexConsumer vc, Vector4f vector, int u, int v, float[] color) {
        double scalar = 1d / (vector.getW() + 1);
        Vector3f scaled = vector.toVector3().mul(scalar);

        vc.vertex(scaled.getX(), scaled.getY(), scaled.getZ())
          .texture(u, v)
          .color(color[0], color[1], color[2], color[3])
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

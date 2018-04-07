package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.matrix.Matrix4f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.RGBA;
import org.lwjgl.opengl.GL11;

import static com.flowpowered.math.TrigMath.cos;
import static com.flowpowered.math.TrigMath.sin;

public class Plane {
    Vector4f[] vectors;

    public Plane(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4) {
        vectors = new Vector4f[]{vec1, vec2, vec3, vec4};
    }

    @SideOnly(Side.CLIENT)
    public void draw(RGBA color, double radian) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder worldRenderer = tessellator.getBuffer();

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        project(worldRenderer, rotYW(vectors[0], radian), 0, 0, color);
        project(worldRenderer, rotYW(vectors[1], radian), 0, 1, color);
        project(worldRenderer, rotYW(vectors[2], radian), 1, 1, color);
        project(worldRenderer, rotYW(vectors[3], radian), 1, 0, color);
        tessellator.draw();
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

    @SideOnly(Side.CLIENT)
    private static void project(BufferBuilder buffer, Vector4f vector, int u, int v, RGBA color) {
        double scalar = 1d / (vector.getW() + 1);
        Vector3f vector1 = vector.toVector3().mul(scalar);

        buffer.pos(vector1.getX(), vector1.getY(), vector1.getZ()).tex(u, v).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
    }
}

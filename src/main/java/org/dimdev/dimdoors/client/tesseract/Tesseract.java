package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.vector.Vector4f;
import org.dimdev.dimdoors.api.util.RGBA;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Tesseract {
    private final Plane[] planes = new Plane[24];

    public Tesseract() {
        this.planes[0] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        this.planes[1] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        this.planes[2] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f)
        );

        this.planes[3] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        this.planes[4] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        this.planes[5] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        this.planes[6] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[7] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        this.planes[8] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        this.planes[9] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        this.planes[10] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[11] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[12] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        this.planes[13] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[14] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        this.planes[15] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[16] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        this.planes[17] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        this.planes[18] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        this.planes[19] = new Plane(
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[20] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        this.planes[21] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        this.planes[22] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        this.planes[23] = new Plane(
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f)
        );
    }

    @Environment(EnvType.CLIENT)
    public void draw(Matrix4f model, VertexConsumer vc, RGBA color, double radian) {
        for (Plane plane : this.planes) {
            plane.draw(model, vc, color, radian);
        }
    }
}

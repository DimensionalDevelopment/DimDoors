package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.vector.Vector4f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

public class Tesseract {
    private final Plane[] planes;

    public Tesseract() {
        planes = new Plane[24];

        planes[0] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[1] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        planes[2] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f)
        );

        planes[3] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f)
        );

        planes[4] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[5] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[6] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[7] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        planes[8] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        planes[9] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f)
        );

        planes[10] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[11] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[12] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[13] = new Plane(
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[14] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[15] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[16] = new Plane(
                new Vector4f(-0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[17] = new Plane(
                new Vector4f(-0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(-0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(-0.5f, 0.5f, -0.5f, 0.5f)
        );

        planes[18] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[19] = new Plane(
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[20] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f)
        );

        planes[21] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f)
        );

        planes[22] = new Plane(
                new Vector4f(0.5f, -0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, -0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, -0.5f, -0.5f, 0.5f)
        );

        planes[23] = new Plane(
                new Vector4f(0.5f, 0.5f, -0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, -0.5f),
                new Vector4f(0.5f, 0.5f, 0.5f, 0.5f),
                new Vector4f(0.5f, 0.5f, -0.5f, 0.5f)
        );
    }

    @Environment(EnvType.CLIENT)
    public void draw(VertexConsumer vc, float[] color, double radian) {
        for (Plane plane : planes) {
            plane.draw(vc, color, radian);
        }
    }
}

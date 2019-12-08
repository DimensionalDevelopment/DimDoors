package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.vector.Vector4f;
import net.minecraft.client.render.VertexConsumer;

public class Cube {
    Plane[] planes = new Plane[6];

    public Cube(Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4, Vector4f vec5, Vector4f vec6, Vector4f vec7, Vector4f vec8) {
        planes[0] = new Plane(vec1, vec2, vec3, vec4);
        planes[1] = new Plane(vec5, vec6, vec7, vec8);
        planes[2] = new Plane(vec1, vec3, vec5, vec7);
        planes[3] = new Plane(vec3, vec4, vec7, vec8);
        planes[4] = new Plane(vec2, vec4, vec6, vec8);
        planes[5] = new Plane(vec1, vec2, vec5, vec6);
    }

    public void draw(VertexConsumer vc, float[] color, double radian) {
        for (Plane plane : planes) {
            plane.draw(vc, color, radian);
        }
    }
}

package org.dimdev.dimdoors.client.tesseract;

import com.flowpowered.math.vector.Vector4f;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.dimdev.ddutils.RGBA;

public class Cube {
    Plane[] planes = new Plane[6];

    public Cube(Vector4f first, Vector4f second, Vector4f third, Vector4f fourth, Vector4f fifth, Vector4f sixth, Vector4f seventh, Vector4f eighth) {
        planes[0] = new Plane(first, second, third, fourth);
        planes[1] = new Plane(fifth, sixth, seventh, eighth);
        planes[2] = new Plane(first, third, fifth, seventh);
        planes[3] = new Plane(third, fourth, seventh, eighth);
        planes[4] = new Plane(second, fourth, sixth, eighth);
        planes[5] = new Plane(first, second, fifth, sixth);
    }

    @SideOnly(Side.CLIENT)
    public void draw(RGBA color, double radian) {
        for(Plane plane : planes) {
            plane.draw(color, radian);
        }
    }
}

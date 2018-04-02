package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.dimdev.ddutils.lsystem.LSystem;
import org.lwjgl.opengl.GL11;

import java.awt.Point; // TODO: wrong point class!

import static org.lwjgl.opengl.GL11.*;

public final class RiftCrackRenderer {

    public static void drawCrack(int riftRotation, LSystem.PolygonStorage poly, double size, double xWorld, double yWorld, double zWorld) {
        // Calculate the proper size for the rift render
        double scale = size / (poly.maxX - poly.minX);

        // Calculate the midpoint of the fractal bounding box
        double offsetX = (poly.maxX + poly.minX) / 2d;
        double offsetY = (poly.maxY + poly.minY) / 2d;
        double offsetZ = 0;

        // Changes how far the triangles move
        // TODO: Actually seems to control the glow around the rift
        float motionMagnitude = 0.2F;

        // Changes how quickly the triangles move
        float motionSpeed = 2000.0F;

        // Number of individual jitter waveforms to generate
        // changes how "together" the overall motions are
        int jCount = 5;

        // Calculate jitter like for monoliths
        // Used to be: 0xF1234568 * hashCode(), this is probably to avoid syncing all rifts
        long riftRandom = (long) (0xF1234568L * (xWorld + yWorld * (2L << 21) + zWorld * (2L << 42)));
        float time = ((Minecraft.getSystemTime() + riftRandom) % 2000000) / motionSpeed;
        double[] jitters = new double[jCount];

        // TODO: Fix jitters. This loop seems to be overwriting all but the last cos with sin
        // generate a series of waveforms
        for (int i = 0; i < jCount - 1; i += 1) {
            // TODO: Division by magnitude... Not multiplication???
            jitters[i] = Math.sin((1F + i / 10F) * time) * Math.cos(1F - i / 10F * time) / motionMagnitude;
            jitters[i + 1] = Math.cos((1F + i / 10F) * time) * Math.sin(1F - i / 10F * time) / motionMagnitude;
        }

        // determines which jitter waveform we select. Modulo so the same point
        // gets the same jitter waveform over multiple frames
        int jIndex = 0;
        // set the color for the render
        GlStateManager.color(.1F, .1F, .1F, 1F);

        // set the blending mode
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_ONE_MINUS_SRC_COLOR, GL_ONE);
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        for (Point p : poly.points) {
            jIndex = Math.abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y);
            // jIndex++;
            // Calculate the rotation for the fractal, apply offset, and apply jitter
            double x = (p.x + jitters[(jIndex + 1) % jCount] - offsetX) * Math.cos(Math.toRadians(riftRotation)) - jitters[(jIndex + 2) % jCount] * Math.sin(Math.toRadians(riftRotation));
            double y = p.y + jitters[jIndex % jCount] - offsetY;
            double z = (p.x + jitters[(jIndex + 2) % jCount] - offsetX) * Math.sin(Math.toRadians(riftRotation)) + jitters[(jIndex + 2) % jCount] * Math.cos(Math.toRadians(riftRotation));

            // Apply scaling
            x *= scale;
            y *= scale;
            z *= scale;

            // Apply transform to center the offset origin into the middle of a block
            x += .5;
            y += .5;
            z += .5;

            // Draw the vertex and apply the world (screenspace) relative coordinates
            GL11.glVertex3d(xWorld + x, yWorld + y, zWorld + z);
        }
        GlStateManager.glEnd();

        GlStateManager.color(.3F, .3F, .3F, .2F);

        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);

        // Draw the next set of triangles to form a background and change their color slightly over time
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        for (Point p : poly.points) {
            jIndex++;

            double x = (p.x - offsetX) * Math.cos(Math.toRadians(riftRotation)) - 0 * Math.sin(Math.toRadians(riftRotation));
            double y = p.y - offsetY;
            double z = (p.x - offsetX) * Math.sin(Math.toRadians(riftRotation)) + 0 * Math.cos(Math.toRadians(riftRotation));

            x *= scale;
            y *= scale;
            z *= scale;

            x += .5;
            y += .5;
            z += .5;

            // TODO: What does this do?
            //if (jIndex % 3 == 0) {
            //    GL11.glColor4d(1 - jitters[(jIndex + 5) % jCount] / 11,
            //                   1 - jitters[(jIndex + 4) % jCount] / 8,
            //                   1 - jitters[(jIndex + 3) % jCount] / 8, 1);
            //}

            GL11.glVertex3d(xWorld + x, yWorld + y, zWorld + z);
        }

        // Stop drawing triangles
        GlStateManager.glEnd();
    }
}

package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.dimdev.ddutils.lsystem.LSystem;
import org.lwjgl.opengl.GL11;

import java.awt.Point; // TODO: wrong point class!

import static org.lwjgl.opengl.GL11.*;

public final class RiftCrackRenderer {

    public static void drawCrack(float riftRotation, LSystem.PolygonInfo poly, double size, double xWorld, double yWorld, double zWorld) {
        // Calculate the proper size for the rift render
        double scale = size / (poly.maxX - poly.minX);

        // Calculate the midpoint of the fractal bounding box
        double offsetX = (poly.maxX + poly.minX) / 2d;
        double offsetY = (poly.maxY + poly.minY) / 2d;
        double offsetZ = 0;

        // Changes how far the triangles move
        // TODO: Actually seems to control the glow around the rift
        float motionMagnitude = 0.6F;

        // Changes how quickly the triangles move
        float motionSpeed = 0.014f;

        // Number of individual jitter waveforms to generate
        // changes how "together" the overall motions are
        int jCount = 10;


        // Calculate jitter like for monoliths
        // Used to be: 0xF1234568 * hashCode(), this is probably to avoid syncing all rifts
        long riftRandom = 0;//(long) (0xF1234568L * (xWorld + yWorld * (2L << 21) + zWorld * (2L << 42)));
        float time = ((Minecraft.getSystemTime() + riftRandom) % 2000000) * motionSpeed;
        double[] jitters = new double[jCount];

        double jitterScale = size * size / 1100f;
        // We use random constants here on purpose just to get different wave forms
        double xJitter = jitterScale * Math.sin(1.1f * time*size) * Math.sin(0.8f * time);
        double yJitter = jitterScale * Math.sin(1.2f * time*size) * Math.sin(0.9f * time);
        double zJitter = jitterScale * Math.sin(1.3f * time*size) * Math.sin(0.7f * time);

        // generate a series of waveforms
        for (int i = 0; i < jCount; i += 1) {
            jitters[i] = Math.sin((1F + i / 10F) * time) * Math.cos(1F - i / 10F * time) * motionMagnitude;
        }

        GlStateManager.color(0.08f, 0.08f, 0.08f, .3F);
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO); // Invert the backgrounds

        // Draw the next set of triangles to form a background and change their color slightly over time
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        for (Point p : poly.points) {
            int jIndex = Math.abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y);

            double x = (p.x + jitters[(jIndex + 1) % jCount] - offsetX) * Math.cos(Math.toRadians(riftRotation)) - jitters[(jIndex + 2) % jCount] * Math.sin(Math.toRadians(riftRotation));
            double y = p.y + jitters[jIndex % jCount] - offsetY;
            double z = (p.x + jitters[(jIndex + 2) % jCount] - offsetZ) * Math.sin(Math.toRadians(riftRotation)) + jitters[(jIndex + 2) % jCount] * Math.cos(Math.toRadians(riftRotation));
            x *= scale;
            y *= scale;
            z *= scale;

            GL11.glVertex3d(xWorld + x + xJitter, yWorld + y + yJitter, zWorld + z + zJitter);
        }

        // Stop drawing triangles
        GlStateManager.glEnd();
    }
}

package org.dimdev.dimdoors.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.dimdev.ddutils.lsystem.LSystem;
import org.dimdev.dimdoors.shared.ModConfig;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public final class RiftCrackRenderer {

    public static void drawCrack(float riftRotation, LSystem.PolygonInfo poly, double size, double xWorld, double yWorld, double zWorld) {
        // Calculate the proper size for the rift render
        double scale = size / (poly.maxX - poly.minX);
        // Calculate the midpoint of the fractal's bounding box
        double offsetX = (poly.maxX + poly.minX) / 2d;
        double offsetY = (poly.maxY + poly.minY) / 2d;
        double offsetZ = 0;
        // Jitters that make rifts shake
        float jitterSpeed = 0.014f; // Changes how quickly the rift jitters
        // Calculate jitter like for monoliths, depending on x, y and z coordinates to avoid all rifts syncing
        long riftRandom = (long) (0xF1234568L * (xWorld + yWorld * (2L << 21) + zWorld * (2L << 42)));
        float time = (Minecraft.getSystemTime() + riftRandom) % 2000000;
        double jitterScale = ModConfig.graphics.riftJitter * size * size * size / 2000f;
        // We use random constants here on purpose just to get different wave forms
        double xJitter = jitterScale * Math.sin(1.1f * time * size * jitterSpeed) * Math.sin(0.8f * time * jitterSpeed);
        double yJitter = jitterScale * Math.sin(1.2f * time * size * jitterSpeed) * Math.sin(0.9f * time * jitterSpeed);
        double zJitter = jitterScale * Math.sin(1.3f * time * size * jitterSpeed) * Math.sin(0.7f * time * jitterSpeed);
        // Flutters in the rift's triangles (most noticed in the edges)
        float flutterMagnitude = 0.6F; // Changes how far the triangles move
        int flutterModulo = 10; // Changes how "together" the overall motions are
        float flutterSpeed = 0.014f; // Changes the speed at which the rift flutters
        double[] flutters = new double[flutterModulo];
        for (int i = 0; i < flutterModulo; i += 1)
            flutters[i] = Math.sin((1F + i / 10F) * time * flutterSpeed) *
                    Math.cos(1F - i / 10F * time * flutterSpeed) * flutterMagnitude;
        // Set color (nearly black, but inverts background)
        GlStateManager.color(0.08f, 0.08f, 0.08f, .3F);
        GlStateManager.blendFunc(GL_ONE_MINUS_DST_COLOR, GL_ZERO);
        // Draw the rift
        GlStateManager.glBegin(GL11.GL_TRIANGLES);
        for (Point p : poly.points) {
            // Reduces most overlap between triangles inside the rift's center
            int flutterIndex = Math.abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y);
            // Apply flutter and jitter
            double x = (p.x + flutters[(flutterIndex + 1) % flutterModulo] - offsetX) *
                    Math.cos(Math.toRadians(riftRotation)) - flutters[(flutterIndex + 2) % flutterModulo] *
                    Math.sin(Math.toRadians(riftRotation));
            double y = p.y + flutters[flutterIndex % flutterModulo] - offsetY;
            double z = (p.x + flutters[(flutterIndex + 2) % flutterModulo] - offsetZ) *
                    Math.sin(Math.toRadians(riftRotation)) + flutters[(flutterIndex + 2) % flutterModulo] *
                    Math.cos(Math.toRadians(riftRotation));
            // Scale the rift
            x *= scale;
            y *= scale;
            z *= scale;
            // Draw the vertex
            GL11.glVertex3d(xWorld + x + xJitter, yWorld + y + yJitter, zWorld + z + zJitter);
        }
        GlStateManager.glEnd();
    }
}

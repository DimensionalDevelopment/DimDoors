package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.ModConfig;

import net.minecraft.client.render.VertexConsumer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class RiftCrackRenderer {
    public static void drawCrack(VertexConsumer vc, float riftRotation, RiftCurves.PolygonInfo poly, double size, long riftRandom) {
        // Calculate the proper size for the rift render
        double scale = size / (poly.maxX - poly.minX);

        // Calculate the midpoint of the fractal's bounding box
        double offsetX = (poly.maxX + poly.minX) / 2d;
        double offsetY = (poly.maxY + poly.minY) / 2d;
        double offsetZ = 0;

        // Jitters that make rifts shake
        float jitterSpeed = 0.014f; // Changes how quickly the rift jitters

        // Calculate jitter like for monoliths, depending x, y and z coordinates to avoid all rifts syncing
        float time = (System.currentTimeMillis() + riftRandom) % 2000000;

        double jitterScale = ModConfig.GRAPHICS.riftJitter * size * size * size / 2000f;
        // We use random constants here on purpose just to get different wave forms
        double xJitter = jitterScale * Math.sin(1.1f * time * size * jitterSpeed) * Math.sin(0.8f * time * jitterSpeed);
        double yJitter = jitterScale * Math.sin(1.2f * time * size * jitterSpeed) * Math.sin(0.9f * time * jitterSpeed);
        double zJitter = jitterScale * Math.sin(1.3f * time * size * jitterSpeed) * Math.sin(0.7f * time * jitterSpeed);

        // Flutters in the rift's triangles (most noticed in the edges)
        float flutterMagnitude = 0.6F; // Changes how far the triangles move
        int flutterModulo = 10; // Changes how "together" the overall motions are
        float flutterSpeed = 0.014f; // Changes the speed at which the rift flutters

        double[] flutters = new double[flutterModulo];
        for (int i = 0; i < flutterModulo; i += 1) {
            flutters[i] = Math.sin((1F + i / 10F) * time * flutterSpeed) * Math.cos(1F - i / 10F * time * flutterSpeed) * flutterMagnitude;
        }

        // Draw the rift
        for (RiftCurves.Point p : poly.points) {
            // Reduces most overlap between triangles inside the rift's center
            int flutterIndex = Math.abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y);

            // Apply flutter and jitter
            double x = (p.x + flutters[(flutterIndex + 1) % flutterModulo] - offsetX) * Math.cos(Math.toRadians(riftRotation)) - flutters[(flutterIndex + 2) % flutterModulo] * Math.sin(Math.toRadians(riftRotation));
            double y = p.y + flutters[flutterIndex % flutterModulo] - offsetY;
            double z = (p.x + flutters[(flutterIndex + 2) % flutterModulo] - offsetZ) * Math.sin(Math.toRadians(riftRotation)) + flutters[(flutterIndex + 2) % flutterModulo] * Math.cos(Math.toRadians(riftRotation));

            // Scale the rift
            x *= scale;
            y *= scale;
            z *= scale;

            vc.vertex(x + xJitter, y + yJitter, z + zJitter).color(0.08f, 0.08f, 0.08f, .3f).next();
        }
    }
}

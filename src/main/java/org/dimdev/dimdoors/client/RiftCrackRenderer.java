package org.dimdev.dimdoors.client;

import org.dimdev.dimdoors.DimensionalDoors;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class RiftCrackRenderer {
    public static void drawCrack(Matrix4f model, VertexConsumer vc, float riftRotation, RiftCurves.PolygonInfo poly, double size, long riftRandom) {
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

        float time = ((Util.getEpochTimeMs() + riftRandom) % 2000000) * motionSpeed;
        double[] jitters = new double[jCount];

        double jitterScale = DimensionalDoors.getConfig().getGraphicsConfig().riftJitter * size * size * size / 2000f;
        // We use random constants here on purpose just to get different wave forms
        double xJitter = jitterScale * Math.sin(1.1f * time*size) * Math.sin(0.8f * time);
        double yJitter = jitterScale * Math.sin(1.2f * time*size) * Math.sin(0.9f * time);
        double zJitter = jitterScale * Math.sin(1.3f * time*size) * Math.sin(0.7f * time);

        // generate a series of waveforms
        for (int i = 0; i < jCount; i += 1) {
            jitters[i] = Math.sin((1F + i / 10F) * time) * Math.cos(1F - i / 10F * time) * motionMagnitude;
        }

        // Draw the rift
        for (RiftCurves.Point p : poly.points) {
            // Reduces most overlap between triangles inside the rift's center
            int jIndex = Math.abs((p.x + p.y) * (p.x + p.y + 1) / 2 + p.y);

            double x = (p.x + jitters[(jIndex + 1) % jCount] - offsetX) * Math.cos(Math.toRadians(riftRotation)) - jitters[(jIndex + 2) % jCount] * Math.sin(Math.toRadians(riftRotation));
            double y = p.y + jitters[jIndex % jCount] - offsetY;
            double z = (p.x + jitters[(jIndex + 2) % jCount] - offsetZ) * Math.sin(Math.toRadians(riftRotation)) + jitters[(jIndex + 2) % jCount] * Math.cos(Math.toRadians(riftRotation));

            // Scale the rift
            x *= scale;
            y *= scale;
            z *= scale;

            vc.vertex(model, (float) (x + xJitter), (float) (y + yJitter), (float) (z + zJitter)).color(0.08f, 0.08f, 0.08f, .3f).next();
        }
    }
}

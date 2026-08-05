package org.dimdev.dimdoors.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.client.DimensionalDoorsClient;
import org.dimdev.dimdoors.client.PocketFogDebug;
import org.dimdev.dimdoors.mixin.client.accessor.FrustumAccessor;
import org.dimdev.dimdoors.mixin.client.accessor.LevelRendererAccessor;
import org.dimdev.dimdoors.network.client.ClientPacketListener;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
    @Unique
    private static final float FOG_FADE_DISTANCE = 8.0F;

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void dimdoors$setupFog(
            Camera camera,
            FogRenderer.FogMode fogMode,
            float farPlaneDistance,
            boolean shouldCreateFog,
            float partialTick,
            CallbackInfo ci
    ) {
        dimdoors$applyPocketFog(camera);
    }

    @Unique
    private static final BoundingBox box = PocketFogDebug.TEST_BOX;

    @Unique
    private static void dimdoors$applyPocketFog(Camera camera) {
        BoundingBox box = ClientPacketListener.getArea();
        ResourceKey<Level> pocketWorld = ClientPacketListener.getPocketWorld();

        if(DimensionalDoorsClient.INSTANCE.boxDebug) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (box == null || minecraft.level == null || !minecraft.level.dimension().equals(pocketWorld)) {
            return;
        }

        Vec3 position = camera.getPosition();
        AABB bounds = AABB.of(box);
        Frustum frustum = dimdoors$getFrustum(minecraft);

        if (frustum == null) {
            return;
        }

        float boundary = dimdoors$getMaxVisibleFogDistance(position, bounds, frustum);

        if (Float.isFinite(boundary)) {
            float fadeDistance = dimdoors$getFadeDistance();

            RenderSystem.setShaderFogStart(boundary);
            RenderSystem.setShaderFogEnd(boundary + fadeDistance);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
            return;
        }

        RenderSystem.setShaderFogStart(0.0F);
        RenderSystem.setShaderFogEnd(0.0F);
        RenderSystem.setShaderFogShape(FogShape.CYLINDER);
    }

    private static Frustum dimdoors$getFrustum(Minecraft minecraft) {
        LevelRendererAccessor levelRenderer = (LevelRendererAccessor) minecraft.levelRenderer;
        Frustum capturedFrustum = levelRenderer.dimdoors$getCapturedFrustum();

        return capturedFrustum != null ? capturedFrustum : levelRenderer.dimdoors$getCullingFrustum();
    }

    private static float dimdoors$getFadeDistance() {
        float vanillaFadeDistance = RenderSystem.getShaderFogEnd() - RenderSystem.getShaderFogStart();

        if (!Float.isFinite(vanillaFadeDistance) || vanillaFadeDistance <= 0.0F) {
            return FOG_FADE_DISTANCE;
        }

        return Math.min(vanillaFadeDistance, FOG_FADE_DISTANCE);
    }

    private static float dimdoors$getMaxVisibleFogDistance(Vec3 origin, AABB bounds, Frustum frustum) {
        FrustumAccessor frustumAccessor = (FrustumAccessor) frustum;
        Matrix4f matrix = frustumAccessor.dimdoors$getMatrix();
        Matrix4f inverseMatrix = new Matrix4f(matrix).invert();
        double frustumCamX = frustumAccessor.dimdoors$getCamX();
        double frustumCamY = frustumAccessor.dimdoors$getCamY();
        double frustumCamZ = frustumAccessor.dimdoors$getCamZ();

        Vec3[] boxCorners = dimdoors$getBoxCorners(bounds);
        Vec3[] frustumCorners = dimdoors$getFrustumCorners(inverseMatrix, frustumCamX, frustumCamY, frustumCamZ);
        double[] maxDistance = {-1.0};

        for (Vec3 corner : boxCorners) {
            if (dimdoors$isInFrustum(corner, matrix, frustumCamX, frustumCamY, frustumCamZ)) {
                dimdoors$includeFogDistance(maxDistance, origin, corner);
            }
        }

        for (Vec3 corner : frustumCorners) {
            if (bounds.contains(corner)) {
                dimdoors$includeFogDistance(maxDistance, origin, corner);
            }
        }

        dimdoors$includeBoxEdgeFrustumIntersections(maxDistance, origin, boxCorners, matrix, frustumCamX, frustumCamY, frustumCamZ);
        dimdoors$includeFrustumEdgeBoxIntersections(maxDistance, origin, frustumCorners, bounds);

        return maxDistance[0] >= 0.0 ? (float) maxDistance[0] : Float.NaN;
    }

    private static Vec3[] dimdoors$getBoxCorners(AABB bounds) {
        return new Vec3[]{
                new Vec3(bounds.minX, bounds.minY, bounds.minZ),
                new Vec3(bounds.minX, bounds.minY, bounds.maxZ),
                new Vec3(bounds.minX, bounds.maxY, bounds.minZ),
                new Vec3(bounds.minX, bounds.maxY, bounds.maxZ),
                new Vec3(bounds.maxX, bounds.minY, bounds.minZ),
                new Vec3(bounds.maxX, bounds.minY, bounds.maxZ),
                new Vec3(bounds.maxX, bounds.maxY, bounds.minZ),
                new Vec3(bounds.maxX, bounds.maxY, bounds.maxZ)
        };
    }

    private static Vec3[] dimdoors$getFrustumCorners(Matrix4f inverseMatrix, double camX, double camY, double camZ) {
        return new Vec3[]{
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, -1.0F, -1.0F, -1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, -1.0F, 1.0F, -1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, 1.0F, -1.0F, -1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, 1.0F, 1.0F, -1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, -1.0F, -1.0F, 1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, -1.0F, 1.0F, 1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, 1.0F, -1.0F, 1.0F),
                dimdoors$unproject(inverseMatrix, camX, camY, camZ, 1.0F, 1.0F, 1.0F)
        };
    }

    private static Vec3 dimdoors$unproject(Matrix4f inverseMatrix, double camX, double camY, double camZ, float x, float y, float z) {
        Vector4f point = inverseMatrix.transform(new Vector4f(x, y, z, 1.0F));
        point.div(point.w());

        return new Vec3(point.x() + camX, point.y() + camY, point.z() + camZ);
    }

    private static void dimdoors$includeBoxEdgeFrustumIntersections(
            double[] maxDistance,
            Vec3 origin,
            Vec3[] corners,
            Matrix4f matrix,
            double camX,
            double camY,
            double camZ
    ) {
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[0], corners[1], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[0], corners[2], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[0], corners[4], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[1], corners[3], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[1], corners[5], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[2], corners[3], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[2], corners[6], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[3], corners[7], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[4], corners[5], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[4], corners[6], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[5], corners[7], matrix, camX, camY, camZ);
        dimdoors$includeSegmentFrustumClip(maxDistance, origin, corners[6], corners[7], matrix, camX, camY, camZ);
    }

    private static void dimdoors$includeFrustumEdgeBoxIntersections(double[] maxDistance, Vec3 origin, Vec3[] corners, AABB bounds) {
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[0], corners[1], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[0], corners[2], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[1], corners[3], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[2], corners[3], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[4], corners[5], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[4], corners[6], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[5], corners[7], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[6], corners[7], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[0], corners[4], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[1], corners[5], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[2], corners[6], bounds);
        dimdoors$includeSegmentBoxClip(maxDistance, origin, corners[3], corners[7], bounds);
    }

    private static void dimdoors$includeSegmentFrustumClip(
            double[] maxDistance,
            Vec3 origin,
            Vec3 start,
            Vec3 end,
            Matrix4f matrix,
            double camX,
            double camY,
            double camZ
    ) {
        Vector4f clipStart = dimdoors$toClip(start, matrix, camX, camY, camZ);
        Vector4f clipEnd = dimdoors$toClip(end, matrix, camX, camY, camZ);
        double[] interval = {0.0, 1.0};

        for (int plane = 0; plane < 6; plane++) {
            if (!dimdoors$clipHomogeneousSegment(interval, dimdoors$clipPlaneDistance(clipStart, plane), dimdoors$clipPlaneDistance(clipEnd, plane))) {
                return;
            }
        }

        dimdoors$includeFogDistance(maxDistance, origin, dimdoors$lerp(start, end, interval[0]));
        dimdoors$includeFogDistance(maxDistance, origin, dimdoors$lerp(start, end, interval[1]));
    }

    private static boolean dimdoors$clipHomogeneousSegment(double[] interval, float startDistance, float endDistance) {
        if (startDistance < 0.0F && endDistance < 0.0F) {
            return false;
        }

        if (startDistance < 0.0F || endDistance < 0.0F) {
            double t = startDistance / (startDistance - endDistance);

            if (startDistance < 0.0F) {
                interval[0] = Math.max(interval[0], t);
            } else {
                interval[1] = Math.min(interval[1], t);
            }
        }

        return interval[0] <= interval[1];
    }

    private static float dimdoors$clipPlaneDistance(Vector4f point, int plane) {
        return switch (plane) {
            case 0 -> point.x() + point.w();
            case 1 -> -point.x() + point.w();
            case 2 -> point.y() + point.w();
            case 3 -> -point.y() + point.w();
            case 4 -> point.z() + point.w();
            default -> -point.z() + point.w();
        };
    }

    private static void dimdoors$includeSegmentBoxClip(double[] maxDistance, Vec3 origin, Vec3 start, Vec3 end, AABB bounds) {
        double[] interval = {0.0, 1.0};

        if (!dimdoors$clipAxis(interval, start.x, end.x - start.x, bounds.minX, bounds.maxX)) {
            return;
        }

        if (!dimdoors$clipAxis(interval, start.y, end.y - start.y, bounds.minY, bounds.maxY)) {
            return;
        }

        if (!dimdoors$clipAxis(interval, start.z, end.z - start.z, bounds.minZ, bounds.maxZ)) {
            return;
        }

        dimdoors$includeFogDistance(maxDistance, origin, dimdoors$lerp(start, end, interval[0]));
        dimdoors$includeFogDistance(maxDistance, origin, dimdoors$lerp(start, end, interval[1]));
    }

    private static boolean dimdoors$clipAxis(double[] interval, double start, double delta, double min, double max) {
        if (delta == 0.0) {
            return start >= min && start <= max;
        }

        double t1 = (min - start) / delta;
        double t2 = (max - start) / delta;

        if (t1 > t2) {
            double tmp = t1;
            t1 = t2;
            t2 = tmp;
        }

        interval[0] = Math.max(interval[0], t1);
        interval[1] = Math.min(interval[1], t2);

        return interval[0] <= interval[1];
    }

    private static boolean dimdoors$isInFrustum(Vec3 point, Matrix4f matrix, double camX, double camY, double camZ) {
        Vector4f clip = dimdoors$toClip(point, matrix, camX, camY, camZ);

        return clip.x() >= -clip.w() && clip.x() <= clip.w()
                && clip.y() >= -clip.w() && clip.y() <= clip.w()
                && clip.z() >= -clip.w() && clip.z() <= clip.w();
    }

    private static Vector4f dimdoors$toClip(Vec3 point, Matrix4f matrix, double camX, double camY, double camZ) {
        return matrix.transform(new Vector4f(
                (float) (point.x - camX),
                (float) (point.y - camY),
                (float) (point.z - camZ),
                1.0F
        ));
    }

    private static Vec3 dimdoors$lerp(Vec3 start, Vec3 end, double t) {
        return new Vec3(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t,
                start.z + (end.z - start.z) * t
        );
    }

    private static void dimdoors$includeFogDistance(double[] maxDistance, Vec3 origin, Vec3 point) {
        maxDistance[0] = Math.max(maxDistance[0], dimdoors$getFogDistance(origin, point.x, point.y, point.z));
    }

    private static double dimdoors$getFogDistance(Vec3 origin, double x, double y, double z) {
        double dx = x - origin.x;
        double dy = y - origin.y;
        double dz = z - origin.z;

        return Math.max(Math.sqrt(dx * dx + dz * dz), Math.abs(dy));
    }
}

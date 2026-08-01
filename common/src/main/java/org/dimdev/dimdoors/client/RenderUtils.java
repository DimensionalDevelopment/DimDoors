package org.dimdev.dimdoors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

import java.util.List;

public class RenderUtils {
    private static final int SUPPORTED_VERTEX_ELEMENTS = VertexFormatElement.POSITION.mask()
        | VertexFormatElement.COLOR.mask()
        | VertexFormatElement.UV0.mask()
        | VertexFormatElement.UV1.mask()
        | VertexFormatElement.UV2.mask()
        | VertexFormatElement.NORMAL.mask();

    public static void renderSolidColorSphere(RenderType renderType, VertexConsumer vc, PoseStack matrices, float radius, float red, float green, float blue, float alpha, int latitudeSegments, int longitudeSegments) {
        VertexFormat.Mode mode = renderType.mode();
        if (mode != VertexFormat.Mode.QUADS && mode != VertexFormat.Mode.TRIANGLES) {
            return;
        }

        VertexFormat format = renderType.format();
        if (!format.contains(VertexFormatElement.POSITION) || (format.getElementsMask() & ~SUPPORTED_VERTEX_ELEMENTS) != 0) {
            return;
        }

        PoseStack.Pose pose = matrices.last();
        for (int latitude = 0; latitude < latitudeSegments; latitude++) {
            float theta0 = Mth.PI * latitude / latitudeSegments;
            float theta1 = Mth.PI * (latitude + 1) / latitudeSegments;
            float v0 = (float) latitude / latitudeSegments;
            float v1 = (float) (latitude + 1) / latitudeSegments;

            for (int longitude = 0; longitude < longitudeSegments; longitude++) {
                float phi0 = Mth.TWO_PI * longitude / longitudeSegments;
                float phi1 = Mth.TWO_PI * (longitude + 1) / longitudeSegments;
                float u0 = (float) longitude / longitudeSegments;
                float u1 = (float) (longitude + 1) / longitudeSegments;

                if (mode == VertexFormat.Mode.QUADS) {
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi1, u1, v0, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi0, u0, v1, red, green, blue, alpha);
                } else {
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi1, u1, v0, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta0, phi0, u0, v0, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi1, u1, v1, red, green, blue, alpha);
                    addSolidColorSphereVertex(vc, pose, format, radius, theta1, phi0, u0, v1, red, green, blue, alpha);
                }
            }
        }
    }

    private static void addSolidColorSphereVertex(VertexConsumer vc, PoseStack.Pose pose, VertexFormat format, float radius, float theta, float phi, float u, float v, float red, float green, float blue, float alpha) {
        float sinTheta = Mth.sin(theta);
        float normalX = sinTheta * Mth.cos(phi);
        float normalY = Mth.cos(theta);
        float normalZ = sinTheta * Mth.sin(phi);
        VertexConsumer next = vc.addVertex(pose, radius * normalX, radius * normalY, radius * normalZ);

        if (format.contains(VertexFormatElement.COLOR)) {
            next.setColor(red, green, blue, alpha);
        }

        if (format.contains(VertexFormatElement.UV0)) {
            next.setUv(u, v);
        }

        if (format.contains(VertexFormatElement.UV1)) {
            next.setOverlay(OverlayTexture.NO_OVERLAY);
        }

        if (format.contains(VertexFormatElement.UV2)) {
            next.setLight(LightTexture.FULL_BRIGHT);
        }

        if (format.contains(VertexFormatElement.NORMAL)) {
            next.setNormal(pose, normalX, normalY, normalZ);
        }
    }

    public static void renderTextLines(
            List<Component> lines,
            PoseStack poseStack,
            MultiBufferSource buffer,
            Font font,
            int packedLight
    ) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        Matrix4f matrix4f = poseStack.last().pose();

        float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int backgroundColor = (int)(backgroundOpacity * 255.0F) << 24;

        int lineHeight = font.lineHeight;
        float startY = -((lines.size() - 1) * lineHeight) / 2.0F;

        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            Component line = lines.get(lineIndex);
            if (line == null) {
                continue;
            }

            float textX = (float)(-font.width(line) / 2);
            float textY = startY + lineIndex * lineHeight;

            font.drawInBatch(
                    line,
                    textX,
                    textY,
                    553648127,
                    false,
                    matrix4f,
                    buffer,
                    Font.DisplayMode.SEE_THROUGH,
                    backgroundColor,
                    packedLight
            );

            font.drawInBatch(
                    line,
                    textX,
                    textY,
                    -1,
                    false,
                    matrix4f,
                    buffer,
                    Font.DisplayMode.NORMAL,
                    0,
                    packedLight
            );
        }
    }
}

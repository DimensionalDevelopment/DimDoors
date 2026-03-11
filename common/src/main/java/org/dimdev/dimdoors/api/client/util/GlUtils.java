package org.dimdev.dimdoors.api.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

public class GlUtils {

    /**
     * Copies the color buffer from one RenderTarget to another.
     * This method is an efficient way to transfer the rendered image.
     *
     * @param to   The destination RenderTarget.
     * @param from The source RenderTarget.
     */
    public static void copyColorFrom(RenderTarget to, RenderTarget from) {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer(36008, from.frameBufferId); // Binds the source framebuffer as the read target (GL_READ_FRAMEBUFFER)
        GlStateManager._glBindFramebuffer(36009, to.frameBufferId);   // Binds the destination framebuffer as the draw target (GL_DRAW_FRAMEBUFFER)
        GlStateManager._glBlitFrameBuffer(
                0, 0, from.width, from.height, // Source rectangle (x0, y0, x1, y1)
                0, 0, from.width, from.height, // Destination rectangle (x0, y0, x1, y1)
                16384, // Mask for the buffer to copy (GL_COLOR_BUFFER_BIT)
                9728   // Filter for scaling (GL_NEAREST), though it's not used here since the sizes are the same
        );
        GlStateManager._glBindFramebuffer(36160, 0); // Unbinds both the read and draw framebuffers, returning to default (GL_FRAMEBUFFER, 0)
    }
}
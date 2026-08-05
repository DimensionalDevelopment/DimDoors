package org.dimdev.dimdoors.mixin.client.accessor;

import net.minecraft.client.renderer.culling.Frustum;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Frustum.class)
public interface FrustumAccessor {
    @Accessor("matrix")
    Matrix4f dimdoors$getMatrix();

    @Accessor("camX")
    double dimdoors$getCamX();

    @Accessor("camY")
    double dimdoors$getCamY();

    @Accessor("camZ")
    double dimdoors$getCamZ();
}

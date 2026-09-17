package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * A Transformer is a matrix stack consumer.
 *
 * <p>It modifies the matrices' transformations.
 * It is not recommended to push/pop</p>
 */
public interface Transformer {
    void transform(PoseStack matrices);
}

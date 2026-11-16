package org.dimdev.dimdoors.client.render

import com.mojang.blaze3d.vertex.PoseStack

/**
 * A Transformer is a matrix stack consumer.
 * 
 * 
 * It modifies the matrices' transformations.
 * It is not recommended to push/pop
 */
interface Transformer {
    fun transform(matrices: PoseStack)
}

package org.dimdev.dimdoors.api.client;

import net.minecraft.client.util.math.MatrixStack;

/**
 * A Transformer is a matrix stack consumer.
 *
 * <p>It modifies the matrices' transformations.
 * It is not recommended to push/pop</p>
 */
public interface Transformer {
	void transform(MatrixStack matrices);
}

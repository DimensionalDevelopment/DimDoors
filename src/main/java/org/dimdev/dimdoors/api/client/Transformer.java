package org.dimdev.dimdoors.api.client;

import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A Transformer is a matrix stack consumer.
 *
 * <p>It modifies the matrices' transformations.
 * It is not recommended to push/pop</p>
 */
@Environment(EnvType.CLIENT)
public interface Transformer {
	void transform(MatrixStack matrices);
}

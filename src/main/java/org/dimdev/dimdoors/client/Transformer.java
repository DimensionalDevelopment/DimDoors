package org.dimdev.dimdoors.client;

import net.minecraft.client.util.math.MatrixStack;

public interface Transformer {
	void transform(MatrixStack matrices);

	void setupTallTransform(MatrixStack matrices);
}

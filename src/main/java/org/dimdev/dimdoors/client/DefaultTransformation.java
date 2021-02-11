package org.dimdev.dimdoors.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public enum DefaultTransformation implements Transformer {
	NONE {
		@Override
		public void transform(MatrixStack matrices) {
		}
	},
	DIMENSIONAL_PORTAL {
		@Override
		public void transform(MatrixStack matrices) {
			matrices.translate(0, 0, 0.5F);
		}
	},
	NORTH_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			matrices.translate(0, 0, 0.81F);
		}
	},
	SOUTH_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			matrices.translate(0, 0, 0.19F);
		}
	},
	WEST_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
			matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90.0F));
			matrices.translate(0, 0, -0.81F);
		}
	},
	EAST_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
			matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(90.0F));
			matrices.translate(0, 0, -0.19F);
		}
	};

	private static final DefaultTransformation[] VALUES = values();

	public static DefaultTransformation fromDirection(Direction direction) {
		return VALUES[direction.ordinal()];
	}
}

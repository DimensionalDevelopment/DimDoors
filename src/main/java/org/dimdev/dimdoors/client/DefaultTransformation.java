package org.dimdev.dimdoors.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Direction;

public enum DefaultTransformation implements Transformer {
	DOWN {
		@Override
		public void transform(MatrixStack matrices) {

		}
	},
	UP {
		@Override
		public void transform(MatrixStack matrices) {

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
			matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90.0F));
			matrices.translate(0, 0, -0.81F);
		}
	},
	EAST_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
			matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion(90.0F));
			matrices.translate(0, 0, -0.19F);
		}
	},
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
	};

	private static final DefaultTransformation[] VALUES = values();

	public static DefaultTransformation fromDirection(Direction direction) {
		return VALUES[direction.ordinal()];
	}
}

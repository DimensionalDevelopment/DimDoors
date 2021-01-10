package org.dimdev.dimdoors.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public enum DefaultTransformation implements Transformer {
	NONE {
		@Override
		public void transform(MatrixStack matrices) {
		}
	},
	DIMENSIONAL_PORTAL {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
		}
	},
	NORTH_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
			matrices.translate(0, 0, 0.81F);
		}
	},
	SOUTH_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
			matrices.translate(0, 0, 0.19F);
		}
	},
	WEST_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
//			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			matrices.translate(-0.2F, 0, 0);
		}
	},
	EAST_DOOR {
		@Override
		public void transform(MatrixStack matrices) {
			// TODO
//			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
			matrices.translate(0.8F, 0, 0);
		}
	};

	private static final DefaultTransformation[] VALUES = values();

	public static DefaultTransformation fromDirection(Direction direction) {
		return VALUES[direction.ordinal()];
	}

	@Override
	public void setupTallTransform(MatrixStack matrices) {
		matrices.translate(0, 1, 0);
	}
}

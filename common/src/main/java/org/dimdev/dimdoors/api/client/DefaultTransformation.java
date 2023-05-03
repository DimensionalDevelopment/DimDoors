package org.dimdev.dimdoors.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import org.joml.Quaternionf;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public enum DefaultTransformation implements Transformer {
	DOWN {
		@Override
		public void transform(PoseStack matrices) {

		}
	},
	UP {
		@Override
		public void transform(PoseStack matrices) {

		}
	},
	NORTH_DOOR {
		@Override
		public void transform(PoseStack matrices) {
			matrices.translate(0, 0, 0.81F);
		}
	},
	SOUTH_DOOR {
		@Override
		public void transform(PoseStack matrices) {
			matrices.translate(0, 0, 0.19F);
		}
	},
	WEST_DOOR {
		@Override
		public void transform(PoseStack matrices) {
			// TODO
			matrices.mulPose(new Quaternionf().rotateY((float) Math.toRadians(-90f)));
			matrices.translate(0, 0, -0.81F);
		}
	},
	EAST_DOOR {
		@Override
		public void transform(PoseStack matrices) {
			// TODO
			matrices.mulPose(new Quaternionf().rotateY((float) Math.toRadians(-90f)));
			matrices.translate(0, 0, -0.19F);
		}
	},
	NONE {
		@Override
		public void transform(PoseStack matrices) {
		}
	},
	DIMENSIONAL_PORTAL {
		@Override
		public void transform(PoseStack matrices) {
			matrices.translate(0, 0, 0.5F);
		}
	};

	private static final DefaultTransformation[] VALUES = values();

	public static DefaultTransformation fromDirection(Direction direction) {
		return VALUES[direction.ordinal()];
	}
}

package org.dimdev.dimdoors.api.util.math;

import net.minecraft.core.Rotations;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.phys.Vec3;

public class TransformationMatrix3d extends TransformationMatrixdImpl<TransformationMatrix3d> {
	public TransformationMatrix3d(double[][] matrix) {
		super(matrix);
	}

	public TransformationMatrix3d(AbstractMatrixd<? extends AbstractMatrixd<?>> matrix) {
		super(matrix);
	}

	public TransformationMatrix3d(Vectord... vectors) {
		super(vectors);
	}

	public static TransformationMatrix3d identity() {
		return new TransformationMatrix3d(Matrixd.identity(4, 4));
	}

	@Override
	public TransformationMatrix3d construct(double[][] matrix) {
		return new TransformationMatrix3d(matrix);
	}

	@Override
	public TransformationMatrix3d construct(AbstractMatrixd<? extends AbstractMatrixd<?>> matrixd) {
		return new TransformationMatrix3d(matrixd);
	}

	@Override
	public TransformationMatrix3d construct(Vectord... vectors) {
		return new TransformationMatrix3d(vectors);
	}

	public Vec3 transform(Vec3 vector) {
		Vectord vec = transform(new Vectord(vector.x, vector.y, vector.z));
		return new Vec3(vec.get(0), vec.get(1), vec.get(2));
	}

	// Should only be called on pure rotation matrices, behaviour undefined otherwise.
	public Rotations transform(Rotations angle) {
		TransformationMatrix3d rotator = TransformationMatrix3d.builder().rotate(angle).build();
		// angle vector representation
		Vec3 direction = rotator.transform(new Vec3(0, 0, 1));
		Vec3 upwards = rotator.transform(new Vec3(0, 1, 0));

		direction = transform(direction);
		upwards = transform(upwards);

		return MathUtil.eulerAngle(direction, upwards);
	}

	public static TransformationMatrix3dBuilder builder() {
		return new TransformationMatrix3dBuilder(identity());
	}

	public static class TransformationMatrix3dBuilder extends TransformationMatrixdBuilderImpl<TransformationMatrix3dBuilder, TransformationMatrix3d> {
		protected TransformationMatrix3dBuilder(TransformationMatrix3d instance) {
			super(3, instance);
		}

		@Override
		public TransformationMatrix3dBuilder getSelf() {
			return this;
		}

		public TransformationMatrix3dBuilder translate(Vec3 vector) {
			return translate(new Vectord(vector.x, vector.y, vector.z));
		}

		public TransformationMatrix3dBuilder inverseTranslate(Vec3 vector) {
			return translate(new Vectord(-vector.x, -vector.y, -vector.z));
		}


		public TransformationMatrix3dBuilder rotateX(double angle) {
			return rotateAroundBasePlane(angle, 1, 2);
		}

		public TransformationMatrix3dBuilder rotateY(double angle) {
			return rotateAroundBasePlane(angle, 2, 0);
		}

		public TransformationMatrix3dBuilder rotateZ(double angle) {
			return rotateAroundBasePlane(angle, 0, 1);
		}

		public TransformationMatrix3dBuilder rotate(Rotations angle) {
			return this.rotateZ(Math.toRadians(angle.getZ())) // roll
					.rotateX(Math.toRadians(angle.getX())) // pitch
					.rotateY(Math.toRadians(-angle.getY())); // yaw
		}

		public TransformationMatrix3dBuilder inverseRotate(Rotations angle) {
			return this.rotateZ(-Math.toRadians(angle.getZ())) // roll
					.rotateX(-Math.toRadians(angle.getX())) // pitch
					.rotateY(-Math.toRadians(-angle.getY())); // yaw
		}
	}
}

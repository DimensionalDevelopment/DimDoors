package org.dimdev.dimdoors.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;

public interface CoordinateTransformerBlock {
	TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos);

	TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos);

	default Vec3d transformTo(TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder, Vec3d vector) {
		return transformationBuilder.build().transform(vector);
	}

	default Vec3d transformOut(TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder, Vec3d vector) {
		return transformationBuilder.buildReverse().transform(vector);
	}

	default EulerAngle rotateTo(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, EulerAngle angle) {
		return rotatorBuilder.build().transform(angle);
	}

	default Vec3d rotateTo(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Vec3d vector) {
		return rotatorBuilder.build().transform(vector);
	}

	default EulerAngle rotateOut(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, EulerAngle angle) {
		return rotatorBuilder.buildReverse().transform(angle);
	}

	default Vec3d rotateOut(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Vec3d vector) {
		return rotatorBuilder.buildReverse().transform(vector);
	}

	default boolean isExitFlipped() {
		return false;
	}
}

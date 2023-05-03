package org.dimdev.dimdoors.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.util.math.TransformationMatrix3d;

public interface CoordinateTransformerBlock {
	TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder(BlockState state, BlockPos pos);

	TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder(BlockState state, BlockPos pos);

	default Vec3 transformTo(TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder, Vec3 vector) {
		return transformationBuilder.build().transform(vector);
	}

	default Vec3 transformOut(TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder, Vec3 vector) {
		return transformationBuilder.buildReverse().transform(vector);
	}

	default Rotations rotateTo(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Rotations angle) {
		return rotatorBuilder.build().transform(angle);
	}

	default Vec3 rotateTo(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Vec3 vector) {
		return rotatorBuilder.build().transform(vector);
	}

	default Rotations rotateOut(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Rotations angle) {
		return rotatorBuilder.buildReverse().transform(angle);
	}

	default Vec3 rotateOut(TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder, Vec3 vector) {
		return rotatorBuilder.buildReverse().transform(vector);
	}

	default boolean isExitFlipped() {
		return false;
	}
}

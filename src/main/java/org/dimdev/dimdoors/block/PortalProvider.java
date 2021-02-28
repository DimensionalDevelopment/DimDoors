package org.dimdev.dimdoors.block;

import com.qouteall.immersive_portals.my_util.DQuaternion;
import com.qouteall.immersive_portals.portal.Portal;
import net.minecraft.block.BlockState;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.dimdev.dimdoors.util.math.TransformationMatrix3d;

public interface PortalProvider extends CoordinateTransformationProvider {
	int getPortalHeight();

	int getPortalWidth();

	default Pair<Portal, Portal> createTwoSidedUnboundPortal(BlockState state, World world, BlockPos pos, TransformationMatrix3d.TransformationMatrix3dBuilder targetRotatorBuilder) {
		TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformationBuilder(state, pos);
		Vec3d origin = this.transformOut(transformationBuilder, new Vec3d(0, 0, 0));

		TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = rotatorBuilder(state, pos);
		Vec3d axisW = this.transformOut(rotatorBuilder, new Vec3d(1, 0, 0));
		Vec3d axisH = this.transformOut(rotatorBuilder, new Vec3d(0, 1, 0));

		Vec3d forwards = new Vec3d(0, 0, 1);

		Vec3d first = transformOut(rotatorBuilder, forwards);
		Vec3d second = transformOut(targetRotatorBuilder, forwards);
		Quaternion rotationTransformation;
		if (first.squaredDistanceTo(second.multiply(-1)) < 0.01) {
			rotationTransformation = DQuaternion.rotationByDegrees(new Vec3d(0, 1, 0), 180).toMcQuaternion(); // weird edge case
		} else {
			rotationTransformation = DQuaternion.getRotationBetween(first, second).toMcQuaternion();
		}

		Portal portal = Portal.entityType.create(world);
		portal.setOriginPos(origin);
		portal.setOrientationAndSize(axisW, axisH, getPortalWidth(), getPortalHeight());
		portal.setRotationTransformation(rotationTransformation);

		Portal flippedPortal = Portal.entityType.create(world);
		flippedPortal.setOriginPos(origin);
		flippedPortal.setOrientationAndSize(axisW.multiply(-1), axisH, getPortalWidth(), getPortalHeight());
		flippedPortal.setRotationTransformation(rotationTransformation);

		return new Pair<>(portal, flippedPortal);
	}

	default Portal createUnboundPortal(BlockState state, World world, BlockPos pos) {
		TransformationMatrix3d.TransformationMatrix3dBuilder transformationBuilder = transformationBuilder(state, pos);
		Vec3d origin = this.transformOut(transformationBuilder, new Vec3d(0, 0, 0));

		TransformationMatrix3d.TransformationMatrix3dBuilder rotatorBuilder = rotatorBuilder(state, pos);
		Vec3d axisW = this.transformOut(rotatorBuilder, new Vec3d(1, 0, 0));
		Vec3d axisH = this.transformOut(rotatorBuilder, new Vec3d(0, 1, 0));

		Portal portal = Portal.entityType.create(world);
		portal.setOriginPos(origin);
		portal.setOrientationAndSize(axisW, axisH, getPortalWidth(), getPortalHeight());

		return portal;
	}

	void setupAsReceivingPortal(BlockState state, World world, BlockPos pos, BlockState sourceState);

	void setupAsSendingPortal(BlockState state, World world, BlockPos pos);
}

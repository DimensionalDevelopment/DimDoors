package org.dimdev.dimdoors.entity.ai.mask;

import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.entity.AbstractMaskEntity;
import org.dimdev.dimdoors.world.ModDimensions;
import org.jetbrains.annotations.Nullable;

public class MaskWanderGoal extends WanderAroundGoal {

	public MaskWanderGoal(AbstractMaskEntity mask, double speed, boolean canDespawn) {
		super(mask,speed,10,canDespawn);
	}

	@Override
	public boolean canStart() {
		if(ModDimensions.isDungeonPocketDimension(this.mob.getEntityWorld())) {
			return super.canStart();
		} return false;
	}

	@Override
	@Nullable
	protected Vec3d getWanderTarget() {
		AbstractMaskEntity mask = (AbstractMaskEntity)this.mob;
		BlockPos origin = mask.getPocketOrigin();
		BlockBox box = mask.getPocketBounds();
		int horizontalRange = Math.abs(box.getMaxX()-box.getMinX());
		int zRange = Math.abs(box.getMaxZ()-box.getMinZ());
		if (zRange>horizontalRange) horizontalRange = zRange;
		return NoPenaltyTargeting.findFrom(this.mob,horizontalRange,Math.abs(box.getMaxY()-box.getMinY()),new Vec3d(origin.getX(),origin.getY(),origin.getZ()));
	}
}

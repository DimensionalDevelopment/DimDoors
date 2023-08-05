package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModDimensions;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public class LimboTarget extends VirtualTarget implements EntityTarget {
	public static final LimboTarget INSTANCE = new LimboTarget();
	public static final Codec<LimboTarget> CODEC = Codec.unit(INSTANCE);

	private LimboTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		BlockPos teleportPos = entity.blockPosition();
		while(ModDimensions.LIMBO_DIMENSION.getBlockState(VirtualLocation.getTopPos(ModDimensions.LIMBO_DIMENSION, teleportPos.getX(), teleportPos.getZ())).getBlock() == ModBlocks.ETERNAL_FLUID.get()) {
			teleportPos = teleportPos.offset(1, 0, 1);
		}
		TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, teleportPos.atY(255), relativeAngle, relativeVelocity);
		return true;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.LIMBO.get();
	}

	@Override
	public VirtualTarget copy() {
		return INSTANCE;
	}
}

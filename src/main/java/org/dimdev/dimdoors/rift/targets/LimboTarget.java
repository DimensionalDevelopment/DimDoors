package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.TeleportUtil;
import org.dimdev.dimdoors.block.ModBlocks;
import org.dimdev.dimdoors.world.ModDimensions;

import net.minecraft.entity.Entity;
import org.dimdev.dimdoors.world.pocket.VirtualLocation;

public class LimboTarget extends VirtualTarget implements EntityTarget {
	public static final LimboTarget INSTANCE = new LimboTarget();
	public static final Codec<LimboTarget> CODEC = Codec.unit(INSTANCE);

	private LimboTarget() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		BlockPos teleportPos = entity.getBlockPos();
		while(ModDimensions.LIMBO_DIMENSION.getBlockState(VirtualLocation.getTopPos(ModDimensions.LIMBO_DIMENSION, teleportPos.getX(), teleportPos.getZ())).getBlock() == ModBlocks.ETERNAL_FLUID) {
			teleportPos = teleportPos.add(1, 0, 1);
		}
		TeleportUtil.teleport(entity, ModDimensions.LIMBO_DIMENSION, teleportPos.withY(255), relativeAngle, relativeVelocity);
		return true;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.LIMBO;
	}
}

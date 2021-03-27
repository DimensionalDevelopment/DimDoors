package org.dimdev.dimdoors.rift.targets;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class PocketExitMarker extends VirtualTarget implements EntityTarget {
	public static final Codec<PocketExitMarker> CODEC = Codec.unit(PocketExitMarker::new);

	public PocketExitMarker() {
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		EntityUtils.chat(entity, new TranslatableText("The exit of this dungeon has not been linked. If this is a normally generated pocket, please report this bug."));
		return false;
	}

	@Override
	public VirtualTargetType<? extends VirtualTarget> getType() {
		return VirtualTargetType.POCKET_EXIT;
	}
}

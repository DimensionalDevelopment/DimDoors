package org.dimdev.dimdoors.rift.targets;

import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;

import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

public class MessageTarget implements EntityTarget {
	private final Target forwardTo;
	private final String message;
	private final Object[] messageParams;

	public MessageTarget(Target forwardTo, String message, Object... messageParams) {
		this.forwardTo = forwardTo;
		this.message = message;
		this.messageParams = messageParams;
	}

	public MessageTarget(String message, Object... messageParams) {
		this(null, message, messageParams);
	}

	@Override
	public boolean receiveEntity(Entity entity, Vec3d relativePos, EulerAngle relativeAngle, Vec3d relativeVelocity) {
		EntityUtils.chat(entity, new TranslatableText(this.message, this.messageParams));

		if (this.forwardTo != null) {
			this.forwardTo.as(Targets.ENTITY).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
			return true;
		} else {
			return false;
		}
	}
}

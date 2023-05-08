package org.dimdev.dimdoors.rift.targets;

import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.rift.target.EntityTarget;
import org.dimdev.dimdoors.api.rift.target.Target;
import org.dimdev.dimdoors.api.util.EntityUtils;

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
	public boolean receiveEntity(Entity entity, Vec3 relativePos, Rotations relativeAngle, Vec3 relativeVelocity) {
		EntityUtils.chat(entity, Component.translatable(this.message, this.messageParams));

		if (this.forwardTo != null) {
			this.forwardTo.as(Targets.ENTITY).receiveEntity(entity, relativePos, relativeAngle, relativeVelocity);
			return true;
		} else {
			return false;
		}
	}
}

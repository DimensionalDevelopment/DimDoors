package org.dimdev.dimdoors.shared.rifts.targets;

import net.minecraft.entity.Entity;
import org.dimdev.dimdoors.DimDoors;

@SuppressWarnings("OverloadedVarargsMethod")
public class MessageTarget implements IEntityTarget {
    private ITarget forwardTo;
    private final String message;
    private final Object[] messageParams;

    public MessageTarget(ITarget forwardTo, String message, Object... messageParams) {
        this.forwardTo = forwardTo;
        this.message = message;
        this.messageParams = messageParams;
    }

    public MessageTarget(String message, Object... messageParams) {
        this.message = message;
        this.messageParams = messageParams;
    }

    @Override
    public boolean receiveEntity(Entity entity, float relativeYaw, float relativePitch) {
        DimDoors.sendTranslatedMessage(entity, message, messageParams);

        if (forwardTo != null) {
            forwardTo.as(Targets.ENTITY).receiveEntity(entity, relativeYaw, relativePitch);
            return true;
        } return false;
    }
}

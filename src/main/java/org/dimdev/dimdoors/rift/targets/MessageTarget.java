package org.dimdev.dimdoors.rift.targets;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

@SuppressWarnings("OverloadedVarargsMethod")
public class MessageTarget implements EntityTarget {
    private Target forwardTo;
    private String message;
    private Object[] messageParams;

    public MessageTarget(Target forwardTo, String message, Object... messageParams) {
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
        entity.sendMessage(new TranslatableText(message, messageParams));

        if (forwardTo != null) {
            forwardTo.as(Targets.ENTITY).receiveEntity(entity, relativeYaw, relativePitch);
            return true;
        } else {
            return false;
        }
    }
}

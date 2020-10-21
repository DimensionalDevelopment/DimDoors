package org.dimdev.dimdoors.rift.targets;

import org.dimdev.dimdoors.util.EntityUtils;

import net.minecraft.entity.Entity;
import net.minecraft.text.TranslatableText;

@SuppressWarnings("OverloadedVarargsMethod")
public class MessageTarget implements EntityTarget {
    private Target forwardTo;
    private final String message;
    private final Object[] messageParams;

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
    public boolean receiveEntity(Entity entity, float yawOffset) {
        EntityUtils.chat(entity, new TranslatableText(this.message, this.messageParams));

        if (this.forwardTo != null) {
            this.forwardTo.as(Targets.ENTITY).receiveEntity(entity, yawOffset);
            return true;
        } else {
            return false;
        }
    }
}

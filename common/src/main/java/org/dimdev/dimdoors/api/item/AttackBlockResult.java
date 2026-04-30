package org.dimdev.dimdoors.api.item;

import net.minecraft.world.InteractionResult;

public record AttackBlockResult(InteractionResult result, boolean sendPacket) {
    public static AttackBlockResult success(boolean sendPacket) {
        return new AttackBlockResult(InteractionResult.SUCCESS, sendPacket);
    }

    public static AttackBlockResult fail(boolean sendPacket) {
        return new AttackBlockResult(InteractionResult.FAIL, sendPacket);
    }
}

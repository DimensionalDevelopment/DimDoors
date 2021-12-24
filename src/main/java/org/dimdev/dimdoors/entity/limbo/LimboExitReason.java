package org.dimdev.dimdoors.entity.limbo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

public enum LimboExitReason implements StringIdentifiable {
	ETERNAL_FLUID,
	GENERIC,
	RIFT;

	@Override
	public String asString() {
		return "limbo.exit." + this.name().toLowerCase();
	}

	public void broadcast(PlayerEntity player) {
		//noinspection ConstantConditions
		player.getServer().getPlayerManager().broadcast(new TranslatableText(asString(), player.getGameProfile().getName()), MessageType.SYSTEM, Util.NIL_UUID);
	}
}

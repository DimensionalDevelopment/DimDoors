package org.dimdev.dimdoors.entity.limbo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.StringIdentifiable;

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
		player.getServer().getPlayerManager().broadcast(MutableText.of(new TranslatableTextContent(asString(), player.getGameProfile().getName())), MessageType.SYSTEM);
	}
}

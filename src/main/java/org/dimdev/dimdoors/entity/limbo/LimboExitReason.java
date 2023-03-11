package org.dimdev.dimdoors.entity.limbo;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

public enum LimboExitReason implements StringRepresentable {
	ETERNAL_FLUID,
	GENERIC,
	RIFT;

	@Override
	public String getSerializedName() {
		return "limbo.exit." + this.name().toLowerCase();
	}

	public void broadcast(Player player) {
		//noinspection ConstantConditions
		player.getServer().getPlayerList().broadcastSystemMessage(MutableComponent.create(new TranslatableContents(getSerializedName(), player.getGameProfile().getName())), false);
	}
}

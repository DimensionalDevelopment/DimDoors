package org.dimdev.dimdoors.entity.limbo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;

public abstract class LimboEntranceSource {
	public abstract Text getMessage(PlayerEntity player);

	public void broadcast(PlayerEntity player, MinecraftServer server) {
		server.getPlayerManager().broadcast(this.getMessage(player), MessageType.SYSTEM, Util.NIL_UUID);
	}

	public static LimboDeathEntranceSource ofDamageSource(DamageSource source) {
		return new LimboDeathEntranceSource(source);
	}

	public static class LimboDeathEntranceSource extends LimboEntranceSource {
		private final DamageSource damageSource;

		private LimboDeathEntranceSource(DamageSource damageSource) {
			this.damageSource = damageSource;
		}

		@Override
		public Text getMessage(PlayerEntity player) {
			TranslatableText message = (TranslatableText) this.damageSource.getDeathMessage(player);
			return new TranslatableText("limbo." + message.getKey(), message.getArgs());
		}
	}
}

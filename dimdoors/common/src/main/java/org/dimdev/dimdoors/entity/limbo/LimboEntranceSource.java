package org.dimdev.dimdoors.entity.limbo;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.dimdev.dimdoors.DimensionalDoors;

public abstract class LimboEntranceSource {
    public abstract Component getMessage(Player player);

    public void broadcast(Player player, MinecraftServer server) {
        server.getPlayerList().broadcastSystemMessage(this.getMessage(player), false);
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
        public Component getMessage(Player player) {
            if (DimensionalDoors.getConfig().getLimboConfig().genericDeathMessages) {
                return this.damageSource.getLocalizedDeathMessage(player).copy().append(Component.translatable("limbo.death.generic"));
            } else {
                TranslatableContents message = (TranslatableContents) this.damageSource.getLocalizedDeathMessage(player).getContents();
                return Component.translatable("limbo." + message.getKey(), message.getArgs());
            }
        }
    }
}

package org.dimdev.dimdoors.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import org.dimdev.dimdoors.listener.pocket.PocketListenerUtil;
import org.dimdev.dimdoors.world.pocket.type.addon.MusicAddon;
import org.dimdev.dimdoors.world.pocket.type.addon.PocketAddon;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow
    @Nullable
    public LocalPlayer player;

    @Inject(
            method = "getSituationalMusic",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void checkPocketMusic(CallbackInfoReturnable<Music> cir) {
        if(this.player != null) {
            PocketListenerUtil.getAddon(PocketAddon.PocketAddonType.MUSIC_ADDON, this.player.level(), this.player.blockPosition()).map(MusicAddon::music).ifPresent(cir::setReturnValue);
        }
    }
}

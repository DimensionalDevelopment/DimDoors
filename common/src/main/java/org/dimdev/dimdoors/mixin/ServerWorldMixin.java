package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.forge.world.decay.Decay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

//	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(target = "Lnet/minecraft/server/level/ServerLevel;fluidTicks:Lnet/minecraft/world/ticks/LevelTicks;", value = "FIELD", ordinal = 0, shift = At.Shift.AFTER))
//	public void afterScheduledTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//		Decay.tick((ServerLevel) (Object) this);
//	}
}

package org.dimdev.dimdoors.mixin;

import net.minecraft.server.world.ServerWorld;
import org.dimdev.dimdoors.world.decay.LimboDecay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/world/tick/WorldTickScheduler;", value = "FIELD", ordinal = 0, shift = At.Shift.AFTER))
	public void afterScheduledTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		LimboDecay.tick((ServerWorld) (Object) this);
	}
}

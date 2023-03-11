package org.dimdev.dimdoors.mixin;

import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.dimdev.dimdoors.world.decay.LimboDecay;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(target = "Lnet/minecraft/server/world/ServerWorld;fluidTickScheduler:Lnet/minecraft/world/tick/WorldTickScheduler;", value = "FIELD", ordinal = 0, shift = At.Shift.AFTER))
	public void afterScheduledTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
		LimboDecay.tick((ServerLevel) (Object) this);
	}
}

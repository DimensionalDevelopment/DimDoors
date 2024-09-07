package org.dimdev.dimdoors.mixin;

import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

//	@Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(target = "Lnet/minecraft/server/level/ServerLevel;fluidTicks:Lnet/minecraft/world/ticks/LevelTicks;", value = "FIELD", ordinal = 0, shift = At.Shift.AFTER))
//	public void afterScheduledTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//		Decay.tick((ServerLevel) (Object) this);
//	}
}

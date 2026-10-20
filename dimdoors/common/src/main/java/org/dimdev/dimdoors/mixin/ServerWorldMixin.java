package org.dimdev.dimdoors.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.dimdev.dimdoors.block.entity.DetachedRiftBlockEntity;
import org.dimdev.dimdoors.item.MaskWearerEffects;
import org.dimdev.dimdoors.tag.ModBlockTags;
import org.dimdev.dimdoors.world.ModDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

//    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V", at = @At(target = "Lnet/minecraft/server/level/ServerLevel;fluidTicks:Lnet/minecraft/world/ticks/LevelTicks;", value = "FIELD", ordinal = 0, shift = At.Shift.AFTER))
//    public void afterScheduledTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//    Decay.tick((ServerLevel) (Object) this);
//    }

    @Inject(method = "wakeUpAllPlayers", at = @At("HEAD"))
    private void dimdoors$cureMasksOnSleep(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        if (ModDimensions.isRootDimension(level)) {
            level.players().stream().filter(LivingEntity::isSleeping).forEach(MaskWearerEffects::cure);
        }
    }
}

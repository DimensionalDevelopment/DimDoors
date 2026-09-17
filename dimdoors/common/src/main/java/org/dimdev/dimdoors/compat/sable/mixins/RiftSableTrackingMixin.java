package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.nbt.CompoundTag;
import org.dimdev.dimdoors.compat.sable.SableRiftData;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Rift.class)
public class RiftSableTrackingMixin implements SableRiftData {
    @Unique
    private UUID dimdoors$sableTrackingPoint;

    @Override
    public UUID dimdoors$getSableTrackingPoint() {
        return this.dimdoors$sableTrackingPoint;
    }

    @Override
    public void dimdoors$setSableTrackingPoint(UUID trackingPoint) {
        this.dimdoors$sableTrackingPoint = trackingPoint;
    }

    @Inject(method = "toNbt", at = @At("RETURN"))
    private static void dimdoors$writeSableTrackingPoint(Rift rift, CallbackInfoReturnable<CompoundTag> cir) {
        UUID trackingPoint = ((SableRiftData) rift).dimdoors$getSableTrackingPoint();
        if (trackingPoint != null) {
            cir.getReturnValue().putUUID("sableTrackingPoint", trackingPoint);
        }
    }

    @Inject(method = "fromNbt", at = @At("RETURN"))
    private static void dimdoors$readSableTrackingPoint(CompoundTag nbt, CallbackInfoReturnable<Rift> cir) {
        if (nbt.contains("sableTrackingPoint")) {
            ((SableRiftData) cir.getReturnValue()).dimdoors$setSableTrackingPoint(nbt.getUUID("sableTrackingPoint"));
        }
    }
}

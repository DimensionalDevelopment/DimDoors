package org.dimdev.dimdoors.compat.sable.mixins;

import net.minecraft.server.level.ServerLevel;
import org.dimdev.dimdoors.api.util.Location;
import org.dimdev.dimdoors.util.LevelSpaceHelper;
import org.dimdev.dimdoors.rift.registry.Rift;
import org.dimdev.dimdoors.rift.registry.RiftRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RiftRegistry.class, remap = false)
public class RiftRegistrySableTrackingMixin {
    @Inject(method = "addRift", at = @At("TAIL"))
    private void dimdoors$trackAddedRift(Location location, CallbackInfo ci) {
        this.dimdoors$updateSableTrackingPoint(location);
    }

    @Inject(method = "moveRift", at = @At("TAIL"))
    private void dimdoors$trackMovedRift(Location oldLocation, Location newLocation, CallbackInfo ci) {
        this.dimdoors$updateSableTrackingPoint(newLocation);
    }

    @Inject(method = "removeRift", at = @At("HEAD"))
    private void dimdoors$untrackRemovedRift(Location location, CallbackInfo ci) {
        ServerLevel level = location.getWorld();
        if (level == null) {
            return;
        }

        var registry = RiftRegistry.getInstance();
        if (!registry.isRiftAt(location)) {
            return;
        }

        LevelSpaceHelper.INSTANCE.removeRiftTrackingPoint(level, registry.getRift(location));
    }

    @Unique
    private void dimdoors$updateSableTrackingPoint(Location location) {
        ServerLevel level = location.getWorld();
        if (level == null) {
            return;
        }

        Rift rift = RiftRegistry.getInstance().getRift(location);
        LevelSpaceHelper.INSTANCE.updateRiftTrackingPoint(level, rift);
    }
}

package org.dimdev.dimdoors.mixin;

import java.util.Objects;

import org.dimdev.dimdoors.util.EntityExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

@Mixin(Entity.class)
public class EntityMixin implements EntityExtensions {
    @Shadow
    public World world;
    @Unique
    private boolean dimdoors_readyToTeleport = false;
    private TeleportTarget dimdoors_teleportTarget = null;

    @Inject(at = @At("HEAD"), cancellable = true, method = "getTeleportTarget")
    public void interceptTeleportTarget(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        if (destination.getRegistryKey().getValue().getNamespace().equals("dimdoors")) {
            if (this.dimdoors_isReadyToTeleport()) {
                cir.setReturnValue(Objects.requireNonNull(this.dimdoors_teleportTarget));
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "moveToWorld")
    public void cleanup(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        this.dimdoors_teleportTarget = null;
        this.dimdoors_setReadyToTeleport(false);
    }

    @Override
    public boolean dimdoors_isReadyToTeleport() {
        return this.dimdoors_readyToTeleport;
    }

    @Override
    public void dimdoors_setReadyToTeleport(boolean value) {
        this.dimdoors_readyToTeleport = value;
    }

    @Override
    public void dimdoors_setTeleportTarget(TeleportTarget target) {
        this.dimdoors_teleportTarget = target;
    }
}

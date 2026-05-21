package org.dimdev.dimdoors.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin implements LastPositionProvider {
    @Shadow
    public abstract Level level();

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    private Vec3 lastPos;

    @Inject(method = "checkInsideBlocks()V", at = @At("TAIL"))
    public void checkBlockCollisionSaveLastPos(CallbackInfo ci) {
        lastPos = ((Entity) (Object) this).position();
    }

    public Vec3 getLastPos() {
        return lastPos == null ? ((Entity) (Object) this).position() : lastPos;
    }
}

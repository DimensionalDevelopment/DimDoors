package org.dimdev.dimdoors.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.dimdev.dimdoors.api.entity.LastPositionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class EntityMixin implements LastPositionProvider {
	private Vec3d lastPos;

	@Inject(method = "checkBlockCollision()V", at = @At("TAIL"))
	public void checkBlockCollisionSaveLastPos(CallbackInfo ci) {
		lastPos = ((Entity) (Object) this).getPos();
	}

	public Vec3d getLastPos() {
		return lastPos == null ? ((Entity) (Object) this).getPos() : lastPos;
	}
}

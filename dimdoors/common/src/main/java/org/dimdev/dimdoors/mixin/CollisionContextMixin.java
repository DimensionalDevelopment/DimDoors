package org.dimdev.dimdoors.mixin;

import net.minecraft.world.phys.shapes.CollisionContext;
import org.dimdev.dimdoors.api.util.IDimDoorsCollisionContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CollisionContext.class)
public interface CollisionContextMixin extends IDimDoorsCollisionContext {}

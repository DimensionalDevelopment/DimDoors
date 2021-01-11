package org.dimdev.dimdoors.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.math.Direction;

@Mixin(Direction.class)
public interface DirectionAccessor {
	@Accessor("HORIZONTAL")
	static Direction[] getHorizontal() {
		throw new AssertionError();
	}
}
